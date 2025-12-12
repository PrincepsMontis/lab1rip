package com.inventory.manager.service;

import com.inventory.manager.dto.MovementDTO;
import com.inventory.manager.entity.Item;
import com.inventory.manager.entity.Location;
import com.inventory.manager.entity.Movement;
import com.inventory.manager.repository.ItemRepository;
import com.inventory.manager.repository.LocationRepository;
import com.inventory.manager.repository.MovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovementServiceTest {
    
    @Mock
    private MovementRepository movementRepository;
    
    @Mock
    private ItemRepository itemRepository;
    
    @Mock
    private LocationRepository locationRepository;
    
    @InjectMocks
    private MovementService movementService;
    
    private Item testItem;
    private Location fromLocation;
    private Location toLocation;
    
    @BeforeEach
    void setUp() {
        fromLocation = Location.builder()
                .id(1L)
                .name("From Location")
                .code("FROM-001")
                .build();
        
        toLocation = Location.builder()
                .id(2L)
                .name("To Location")
                .code("TO-001")
                .build();
        
        testItem = Item.builder()
                .id(1L)
                .name("Test Item")
                .sku("TEST-001")
                .quantity(50)
                .unitPrice(BigDecimal.valueOf(99.99))
                .location(fromLocation)
                .build();
    }
    
    @Test
    void whenCreateTransferMovement_thenQuantityIsUpdated() {
        // given
        MovementDTO movementDTO = MovementDTO.builder()
                .itemId(1L)
                .fromLocationId(1L)
                .toLocationId(2L)
                .quantity(10)
                .type(Movement.MovementType.TRANSFER)
                .performedBy("Admin")
                .build();
        
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(fromLocation));
        when(locationRepository.findById(2L)).thenReturn(Optional.of(toLocation));
        when(movementRepository.save(any(Movement.class))).thenReturn(new Movement());
        
        // when
        movementService.createMovement(movementDTO);
        
        // then
        assertThat(testItem.getQuantity()).isEqualTo(40); // 50 - 10
        verify(itemRepository, times(1)).save(testItem);
        verify(movementRepository, times(1)).save(any(Movement.class));
    }
    
    @Test
    void whenTransferInsufficientQuantity_thenThrowException() {
        // given
        MovementDTO movementDTO = MovementDTO.builder()
                .itemId(1L)
                .fromLocationId(1L)
                .toLocationId(2L)
                .quantity(100) // Больше чем есть
                .type(Movement.MovementType.TRANSFER)
                .build();
        
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(locationRepository.findById(2L)).thenReturn(Optional.of(toLocation));
        
        // then
        assertThatThrownBy(() -> movementService.createMovement(movementDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Insufficient quantity");
    }
    
    @Test
    void whenCreateReceiptMovement_thenQuantityIncreases() {
        // given
        MovementDTO movementDTO = MovementDTO.builder()
                .itemId(1L)
                .toLocationId(2L)
                .quantity(20)
                .type(Movement.MovementType.RECEIPT)
                .build();
        
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(locationRepository.findById(2L)).thenReturn(Optional.of(toLocation));
        when(movementRepository.save(any(Movement.class))).thenReturn(new Movement());
        
        // when
        movementService.createMovement(movementDTO);
        
        // then
        assertThat(testItem.getQuantity()).isEqualTo(70); // 50 + 20
        verify(itemRepository, times(1)).save(testItem);
    }
}
