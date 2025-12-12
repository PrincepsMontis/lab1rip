package com.inventory.manager.controller;

import com.inventory.manager.dto.MovementDTO;
import com.inventory.manager.service.MovementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
public class MovementController {
    
    private final MovementService movementService;
    
    @GetMapping
    public ResponseEntity<List<MovementDTO>> getAllMovements() {
        return ResponseEntity.ok(movementService.getAllMovements());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MovementDTO> getMovementById(@PathVariable Long id) {
        return ResponseEntity.ok(movementService.getMovementById(id));
    }
    
    @PostMapping
    public ResponseEntity<MovementDTO> createMovement(@Valid @RequestBody MovementDTO movementDTO) {
        MovementDTO created = movementService.createMovement(movementDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<MovementDTO>> getMovementsByItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(movementService.getMovementsByItem(itemId));
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<MovementDTO>> getMovementsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(movementService.getMovementsByDateRange(startDate, endDate));
    }
}
