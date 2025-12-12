package com.inventory.manager.service;

import com.inventory.manager.dto.ItemDTO;
import com.inventory.manager.entity.Item;
import com.inventory.manager.entity.Location;
import com.inventory.manager.entity.Supplier;
import com.inventory.manager.repository.ItemRepository;
import com.inventory.manager.repository.LocationRepository;
import com.inventory.manager.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {
    
    private final ItemRepository itemRepository;
    private final LocationRepository locationRepository;
    private final SupplierRepository supplierRepository;
    
    public List<ItemDTO> getAllItems() {
        return itemRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public ItemDTO getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
        return convertToDTO(item);
    }
    
    public ItemDTO createItem(ItemDTO itemDTO) {
        if (itemRepository.findBySku(itemDTO.getSku()).isPresent()) {
            throw new RuntimeException("Item with SKU " + itemDTO.getSku() + " already exists");
        }
        
        Item item = convertToEntity(itemDTO);
        Item savedItem = itemRepository.save(item);
        return convertToDTO(savedItem);
    }
    
    public ItemDTO updateItem(Long id, ItemDTO itemDTO) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
        
        item.setName(itemDTO.getName());
        item.setDescription(itemDTO.getDescription());
        item.setQuantity(itemDTO.getQuantity());
        item.setUnitPrice(itemDTO.getUnitPrice());
        item.setStatus(itemDTO.getStatus());
        
        if (itemDTO.getLocationId() != null) {
            Location location = locationRepository.findById(itemDTO.getLocationId())
                    .orElseThrow(() -> new RuntimeException("Location not found"));
            item.setLocation(location);
        }
        
        if (itemDTO.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(itemDTO.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            item.setSupplier(supplier);
        }
        
        Item updatedItem = itemRepository.save(item);
        return convertToDTO(updatedItem);
    }
    
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new RuntimeException("Item not found with id: " + id);
        }
        itemRepository.deleteById(id);
    }
    
    public List<ItemDTO> searchItems(String keyword) {
        return itemRepository.searchByName(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ItemDTO> getLowStockItems(Integer threshold) {
        return itemRepository.findLowStockItems(threshold).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private ItemDTO convertToDTO(Item item) {
        return ItemDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .sku(item.getSku())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .locationId(item.getLocation() != null ? item.getLocation().getId() : null)
                .locationName(item.getLocation() != null ? item.getLocation().getName() : null)
                .supplierId(item.getSupplier() != null ? item.getSupplier().getId() : null)
                .supplierName(item.getSupplier() != null ? item.getSupplier().getName() : null)
                .status(item.getStatus())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
    
    private Item convertToEntity(ItemDTO dto) {
        Item item = Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .sku(dto.getSku())
                .quantity(dto.getQuantity())
                .unitPrice(dto.getUnitPrice())
                .status(dto.getStatus() != null ? dto.getStatus() : Item.ItemStatus.AVAILABLE)
                .build();
        
        if (dto.getLocationId() != null) {
            Location location = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new RuntimeException("Location not found"));
            item.setLocation(location);
        }
        
        if (dto.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            item.setSupplier(supplier);
        }
        
        return item;
    }
}
