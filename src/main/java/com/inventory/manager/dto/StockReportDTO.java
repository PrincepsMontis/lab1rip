package com.inventory.manager.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockReportDTO {
    private Long itemId;
    private String itemName;
    private String sku;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalValue;
    private String locationName;
    private String supplierName;
    private String status;
}
