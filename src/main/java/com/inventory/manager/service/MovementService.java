package com.inventory.manager.service;

import com.inventory.manager.dto.MovementDTO;
import com.inventory.manager.entity.Item;
import com.inventory.manager.entity.Location;
import com.inventory.manager.entity.Movement;
import com.inventory.manager.repository.ItemRepository;
import com.inventory.manager.repository.LocationRepository;
import com.inventory.manager.repository.MovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MovementService {
    
    private final MovementRepository movementRepository;
    private final ItemRepository itemRepository;
    private final LocationRepository locationRepository;
    
    public List<MovementDTO> getAllMovements() {
        return movementRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public MovementDTO getMovementById(Long id) {
        Movement movement = movementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movement not found with id: " + id));
        return convertToDTO(movement);
    }
    
    public MovementDTO createMovement(MovementDTO movementDTO) {
        Item item = itemRepository.findById(movementDTO.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));
        
        Location toLocation = locationRepository.findById(movementDTO.getToLocationId())
                .orElseThrow(() -> new RuntimeException("Destination location not found"));
        
        // Validate quantity
        if (movementDTO.getType() == Movement.MovementType.TRANSFER && 
            item.getQuantity() < movementDTO.getQuantity()) {
            throw new RuntimeException("Insufficient quantity for transfer");
        }
        
        Movement movement = Movement.builder()
                .item(item)
                .toLocation(toLocation)
                .quantity(movementDTO.getQuantity())
                .type(movementDTO.getType())
                .notes(movementDTO.getNotes())
                .performedBy(movementDTO.getPerformedBy())
                .build();
        
        if (movementDTO.getFromLocationId() != null) {
            Location fromLocation = locationRepository.findById(movementDTO.getFromLocationId())
                    .orElseThrow(() -> new RuntimeException("Source location not found"));
            movement.setFromLocation(fromLocation);
        }
        
        // Update item location and quantity
        switch (movementDTO.getType()) {
            case TRANSFER:
                item.setQuantity(item.getQuantity() - movementDTO.getQuantity());
                break;
            case RECEIPT:
                item.setQuantity(item.getQuantity() + movementDTO.getQuantity());
                break;
            case SHIPMENT:
                item.setQuantity(item.getQuantity() - movementDTO.getQuantity());
                break;
            case ADJUSTMENT:
                item.setQuantity(movementDTO.getQuantity());
                break;
        }
        
        item.setLocation(toLocation);
        itemRepository.save(item);
        
        Movement savedMovement = movementRepository.save(movement);
        return convertToDTO(savedMovement);
    }
    
    public List<MovementDTO> getMovementsByItem(Long itemId) {
        return movementRepository.findByItemId(itemId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MovementDTO> getMovementsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return movementRepository.findByDateRange(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private MovementDTO convertToDTO(Movement movement) {
        return MovementDTO.builder()
                .id(movement.getId())
                .itemId(movement.getItem().getId())
                .itemName(movement.getItem().getName())
                .fromLocationId(movement.getFromLocation() != null ? movement.getFromLocation().getId() : null)
                .fromLocationName(movement.getFromLocation() != null ? movement.getFromLocation().getName() : null)
                .toLocationId(movement.getToLocation().getId())
                .toLocationName(movement.getToLocation().getName())
                .quantity(movement.getQuantity())
                .type(movement.getType())
                .notes(movement.getNotes())
                .movementDate(movement.getMovementDate())
                .performedBy(movement.getPerformedBy())
                .build();
    }
}
