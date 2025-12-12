package com.inventory.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.manager.dto.MovementDTO;
import com.inventory.manager.entity.Movement;
import com.inventory.manager.service.MovementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovementController.class)
class MovementControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private MovementService movementService;
    
    private MovementDTO testMovementDTO;
    
    @BeforeEach
    void setUp() {
        testMovementDTO = MovementDTO.builder()
                .id(1L)
                .itemId(1L)
                .itemName("Test Item")
                .fromLocationId(1L)
                .toLocationId(2L)
                .quantity(10)
                .type(Movement.MovementType.TRANSFER)
                .performedBy("Admin")
                .build();
    }
    
    @Test
    void whenGetAllMovements_thenReturnJsonArray() throws Exception {
        // given
        when(movementService.getAllMovements()).thenReturn(Arrays.asList(testMovementDTO));
        
        // when & then
        mockMvc.perform(get("/api/movements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
    
    @Test
    void whenCreateMovement_thenReturnCreated() throws Exception {
        // given
        MovementDTO newMovement = MovementDTO.builder()
                .itemId(1L)
                .toLocationId(2L)
                .quantity(5)
                .type(Movement.MovementType.TRANSFER)
                .build();
        
        when(movementService.createMovement(any(MovementDTO.class))).thenReturn(testMovementDTO);
        
        // when & then
        mockMvc.perform(post("/api/movements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMovement)))
                .andExpect(status().isCreated());
    }
}
