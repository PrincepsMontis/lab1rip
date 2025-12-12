package com.inventory.manager.dto;

import com.inventory.manager.entity.Item;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDTO {
    
    private Long id;
    
    @NotBlank(message = "Item name is required")
    @Size(min = 2, max = 100)
    private String name;
    
    private String description;
    
    @NotBlank(message = "SKU is required")
    private String sku;
    
    @NotNull(message = "Quantity is required")
    @Min(0)
    private Integer quantity;
    
    @NotNull(message = "Unit price is required")
    @DecimalMin("0.0")
    private BigDecimal unitPrice;
    
    private Long locationId;
    private String locationName;
    
    private Long supplierId;
    private String supplierName;
    
    private Item.ItemStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
