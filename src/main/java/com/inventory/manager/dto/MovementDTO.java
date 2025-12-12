package com.inventory.manager.dto;

import com.inventory.manager.entity.Movement;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovementDTO {
    
    private Long id;
    
    @NotNull(message = "Item ID is required")
    private Long itemId;
    private String itemName;
    
    private Long fromLocationId;
    private String fromLocationName;
    
    @NotNull(message = "Destination location is required")
    private Long toLocationId;
    private String toLocationName;
    
    @NotNull(message = "Quantity is required")
    @Min(1)
    private Integer quantity;
    
    @NotNull(message = "Movement type is required")
    private Movement.MovementType type;
    
    private String notes;
    private LocalDateTime movementDate;
    private String performedBy;
}
