package com.inventory.manager.service;

import com.inventory.manager.dto.StockReportDTO;
import com.inventory.manager.entity.Item;
import com.inventory.manager.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {
    
    private final ItemRepository itemRepository;
    
    public List<StockReportDTO> generateStockReport() {
        return itemRepository.findAll().stream()
                .map(this::convertToStockReport)
                .collect(Collectors.toList());
    }
    
    public List<StockReportDTO> generateStockReportByLocation(Long locationId) {
        return itemRepository.findByLocationId(locationId).stream()
                .map(this::convertToStockReport)
                .collect(Collectors.toList());
    }
    
    private StockReportDTO convertToStockReport(Item item) {
        BigDecimal totalValue = item.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));
        
        return StockReportDTO.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .sku(item.getSku())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalValue(totalValue)
                .locationName(item.getLocation() != null ? item.getLocation().getName() : "N/A")
                .supplierName(item.getSupplier() != null ? item.getSupplier().getName() : "N/A")
                .status(item.getStatus().toString())
                .build();
    }
}
