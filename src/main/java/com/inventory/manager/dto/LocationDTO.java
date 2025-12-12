package com.inventory.manager.dto;

import com.inventory.manager.entity.Location;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDTO {
    
    private Long id;
    
    @NotBlank(message = "Location name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @Size(max = 200, message = "Address must not exceed 200 characters")
    private String address;
    
    @NotBlank(message = "Location code is required")
    @Pattern(regexp = "^[A-Z]{2}-\\d{3}$", message = "Code must follow pattern: XX-000")
    private String code;
    
    private Location.LocationType type;
    
    private Integer itemCount;
    
    private LocalDateTime createdAt;
}
