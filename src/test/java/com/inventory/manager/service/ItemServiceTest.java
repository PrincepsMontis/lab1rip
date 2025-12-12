package com.inventory.manager.service;

import com.inventory.manager.dto.ItemDTO;
import com.inventory.manager.entity.Item;
import com.inventory.manager.entity.Location;
import com.inventory.manager.repository.ItemRepository;
import com.inventory.manager.repository.LocationRepository;
import com.inventory.manager.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    
    @Mock
    private ItemRepository itemRepository;
    
    @Mock
    private LocationRepository locationRepository;
    
    @Mock
    private SupplierRepository supplierRepository;
    
    @InjectMocks
    private ItemService itemService;
    
    private Item testItem;
    private Location testLocation;
    
    @BeforeEach
    void setUp() {
        testLocation = Location.builder()
                .id(1L)
                .name("Test Location")
                .code("TL-001")
                .build();
        
        testItem = Item.builder()
                .id(1L)
                .name("Test Item")
                .sku("TEST-001")
                .quantity(10)
                .unitPrice(BigDecimal.valueOf(99.99))
                .location(testLocation)
                .status(Item.ItemStatus.AVAILABLE)
                .build();
    }
    
    @Test
    void whenGetAllItems_thenReturnItemList() {
        // given
        when(itemRepository.findAll()).thenReturn(Arrays.asList(testItem));
        
        // when
        List<ItemDTO> items = itemService.getAllItems();
        
        // then
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Test Item");
        verify(itemRepository, times(1)).findAll();
    }
    
    @Test
    void whenGetItemById_thenReturnItem() {
        // given
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        
        // when
        ItemDTO found = itemService.getItemById(1L);
        
        // then
        assertThat(found.getName()).isEqualTo("Test Item");
        assertThat(found.getSku()).isEqualTo("TEST-001");
    }
    
    @Test
    void whenGetItemByInvalidId_thenThrowException() {
        // given
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // then
        assertThatThrownBy(() -> itemService.getItemById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Item not found");
    }
    
    @Test
    void whenCreateItem_thenReturnCreatedItem() {
        // given
        ItemDTO itemDTO = ItemDTO.builder()
                .name("New Item")
                .sku("NEW-001")
                .quantity(5)
                .unitPrice(BigDecimal.valueOf(50.00))
                .locationId(1L)
                .build();
        
        when(itemRepository.findBySku("NEW-001")).thenReturn(Optional.empty());
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);
        
        // when
        ItemDTO created = itemService.createItem(itemDTO);
        
        // then
        assertThat(created).isNotNull();
        verify(itemRepository, times(1)).save(any(Item.class));
    }
    
    @Test
    void whenCreateItemWithDuplicateSku_thenThrowException() {
        // given
        ItemDTO itemDTO = ItemDTO.builder()
                .sku("TEST-001")
                .name("Duplicate")
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(10.00))
                .build();
        
        when(itemRepository.findBySku("TEST-001")).thenReturn(Optional.of(testItem));
        
        // then
        assertThatThrownBy(() -> itemService.createItem(itemDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already exists");
    }
    
    @Test
    void whenUpdateItem_thenReturnUpdatedItem() {
        // given
        ItemDTO updateDTO = ItemDTO.builder()
                .name("Updated Item")
                .sku("TEST-001")
                .quantity(20)
                .unitPrice(BigDecimal.valueOf(119.99))
                .status(Item.ItemStatus.AVAILABLE)
                .build();
        
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);
        
        // when
        ItemDTO updated = itemService.updateItem(1L, updateDTO);
        
        // then
        assertThat(updated).isNotNull();
        verify(itemRepository, times(1)).save(any(Item.class));
    }
    
    @Test
    void whenDeleteItem_thenRepositoryDeleteIsCalled() {
        // given
        when(itemRepository.existsById(1L)).thenReturn(true);
        doNothing().when(itemRepository).deleteById(1L);
        
        // when
        itemService.deleteItem(1L);
        
        // then
        verify(itemRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void whenSearchItems_thenReturnMatchingItems() {
        // given
        when(itemRepository.searchByName("test")).thenReturn(Arrays.asList(testItem));
        
        // when
        List<ItemDTO> items = itemService.searchItems("test");
        
        // then
        assertThat(items).hasSize(1);
        verify(itemRepository, times(1)).searchByName("test");
    }
}
