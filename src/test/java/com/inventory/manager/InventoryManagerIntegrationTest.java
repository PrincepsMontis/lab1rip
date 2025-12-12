package com.inventory.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.manager.dto.ItemDTO;
import com.inventory.manager.entity.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class InventoryManagerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void whenApplicationStarts_thenSeedDataIsLoaded() throws Exception {
        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }
    
    @Test
    void fullItemLifecycle_createUpdateDelete() throws Exception {
        // 1. Создание
        ItemDTO newItem = ItemDTO.builder()
                .name("Integration Test Item")
                .sku("INT-TEST-001")
                .quantity(100)
                .unitPrice(BigDecimal.valueOf(150.00))
                .locationId(1L)
                .supplierId(1L)
                .status(Item.ItemStatus.AVAILABLE)
                .build();
        
        String createResponse = mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Integration Test Item")))
                .andReturn().getResponse().getContentAsString();
        
        ItemDTO created = objectMapper.readValue(createResponse, ItemDTO.class);
        Long itemId = created.getId();
        
        // 2. Чтение
        mockMvc.perform(get("/api/items/" + itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku", is("INT-TEST-001")));
        
        // 3. Обновление
        created.setName("Updated Integration Test Item");
        created.setQuantity(200);
        
        mockMvc.perform(put("/api/items/" + itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Integration Test Item")));
        
        // 4. Удаление
        mockMvc.perform(delete("/api/items/" + itemId))
                .andExpect(status().isNoContent());
        
        // 5. Проверка что удалено
        mockMvc.perform(get("/api/items/" + itemId))
                .andExpect(status().is4xxClientError());
    }
}
