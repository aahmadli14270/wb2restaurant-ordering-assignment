package com.restaurant.ordering.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.ordering.Model.TableItem;
import com.restaurant.ordering.Repository.TableItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TableControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TableItemRepository tableItemRepository;

    private TableItem testTable;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        tableItemRepository.deleteAll();

        // Create test table
        testTable = new TableItem();
        testTable.setTableId(101L);
        testTable.setOrders(new ArrayList<>());
    }

    @Test
    void getAllTables_ReturnsTables() throws Exception {
        // Arrange
        TableItem savedTable = tableItemRepository.save(testTable);

        // Act & Assert
        mockMvc.perform(get("/tables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].tableId", is(101)));
    }

    @Test
    void getAllTables_NoTables_ThrowsException() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/tables"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTableById_ExistingTable_ReturnsTable() throws Exception {
        // Arrange
        TableItem savedTable = tableItemRepository.save(testTable);

        // Act & Assert
        mockMvc.perform(get("/tables/" + savedTable.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tableId", is(101)));
    }

    @Test
    void getTableById_NonExistingTable_ThrowsException() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/tables/999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void addTable_ValidTable_ReturnsCreatedTable() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTable)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tableId", is(101)));
    }

    @Test
    void updateTable_ExistingTable_ReturnsUpdatedTable() throws Exception {
        // Arrange
        TableItem savedTable = tableItemRepository.save(testTable);

        TableItem updatedTable = new TableItem();
        updatedTable.setTableId(102L);

        // Act & Assert
        mockMvc.perform(put("/tables/" + savedTable.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTable)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tableId", is(102)));
    }

    @Test
    void updateTable_NonExistingTable_ThrowsException() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/tables/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTable)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteTable_ExistingTable_DeletesTable() throws Exception {
        // Arrange
        TableItem savedTable = tableItemRepository.save(testTable);

        // Act & Assert
        mockMvc.perform(delete("/tables/" + savedTable.getId()))
                .andExpect(status().isOk());

        // Verify table is deleted
        mockMvc.perform(get("/tables/" + savedTable.getId()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteTable_NonExistingTable_ReturnsOk() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/tables/999"))
                .andExpect(status().isOk());
    }
}
