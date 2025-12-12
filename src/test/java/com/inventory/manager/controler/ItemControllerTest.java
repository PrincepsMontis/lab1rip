package com.inventory.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.manager.dto.ItemDTO;
import com.inventory.manager.entity.Item;
import com.inventory.manager.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ItemService itemService;
    
    private ItemDTO testItemDTO;
    
    @BeforeEach
    void setUp() {
        testItemDTO = ItemDTO.builder()
                .id(1L)
                .name("Test Laptop")
                .sku("TEST-001")
                .quantity(10)
                .unitPrice(BigDecimal.valueOf(999.99))
                .status(Item.ItemStatus.AVAILABLE)
                .build();
    }
    
    @Test
    void whenGetAllItems_thenReturnJsonArray() throws Exception {
        // given
        List<ItemDTO> items = Arrays.asList(testItemDTO);
        when(itemService.getAllItems()).thenReturn(items);
        
        // when & then
        mockMvc.perform(get("/api/items")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test Laptop")));
    }
    
    @Test
    void whenGetItemById_thenReturnItem() throws Exception {
        // given
        when(itemService.getItemById(1L)).thenReturn(testItemDTO);
        
        // when & then
        mockMvc.perform(get("/api/items/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Laptop")))
                .andExpect(jsonPath("$.sku", is("TEST-001")));
    }
    
    @Test
    void whenCreateItem_thenReturnCreated() throws Exception {
        // given
        ItemDTO newItem = ItemDTO.builder()
                .name("New Item")
                .sku("NEW-001")
                .quantity(5)
                .unitPrice(BigDecimal.valueOf(50.00))
                .build();
        
        when(itemService.createItem(any(ItemDTO.class))).thenReturn(testItemDTO);
        
        // when & then
        mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }
    
    @Test
    void whenCreateItemWithInvalidData_thenReturnBadRequest() throws Exception {
        // given - пустое имя и отрицательная цена
        ItemDTO invalidItem = ItemDTO.builder()
                .name("")
                .sku("TEST")
                .quantity(-5)
                .unitPrice(BigDecimal.valueOf(-10.00))
                .build();
        
        // when & then
        mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidItem)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void whenUpdateItem_thenReturnUpdatedItem() throws Exception {
        // given
        when(itemService.updateItem(anyLong(), any(ItemDTO.class))).thenReturn(testItemDTO);
        
        // when & then
        mockMvc.perform(put("/api/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testItemDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Laptop")));
    }
    
    @Test
    void whenDeleteItem_thenReturnNoContent() throws Exception {
        // given
        doNothing().when(itemService).deleteItem(1L);
        
        // when & then
        mockMvc.perform(delete("/api/items/1"))
                .andExpect(status().isNoContent());
        
        verify(itemService, times(1)).deleteItem(1L);
    }
    
    @Test
    void whenSearchItems_thenReturnMatchingItems() throws Exception {
        // given
        when(itemService.searchItems("laptop")).thenReturn(Arrays.asList(testItemDTO));
        
        // when & then
        mockMvc.perform(get("/api/items/search")
                .param("keyword", "laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", containsString("Laptop")));
    }
}
