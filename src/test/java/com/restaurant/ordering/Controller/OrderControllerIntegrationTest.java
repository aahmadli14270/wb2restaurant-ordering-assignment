package com.restaurant.ordering.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Model.TableItem;
import com.restaurant.ordering.Repository.OrderRepository;
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
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TableItemRepository tableItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    private TableItem testTable;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        orderRepository.deleteAll();
        
        // Create test table
        testTable = new TableItem();
        testTable.setTableId(101L);
        testTable.setOrders(new ArrayList<>());
        testTable = tableItemRepository.save(testTable);

        // Create test order
        testOrder = new Order();
        testOrder.setTable(testTable);
        testOrder.setStatus(OrderStatus.CREATED);
        testOrder.setTotal(20.0);
        testOrder.setItems(new ArrayList<>());
    }

    @Test
    void placeOrder_ValidOrder_ReturnsCreatedOrder() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/order/" + testTable.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CREATED")))
                .andExpect(jsonPath("$.total", is(20.0)));
    }

    @Test
    void placeOrder_TableNotFound_ThrowsException() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/order/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllOrders_ReturnsAllOrders() throws Exception {
        // Arrange
        Order savedOrder = orderRepository.save(testOrder);

        // Act & Assert
        mockMvc.perform(get("/order/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].status", is("CREATED")));
    }

    @Test
    void getOrderById_ExistingOrder_ReturnsOrder() throws Exception {
        // Arrange
        Order savedOrder = orderRepository.save(testOrder);

        // Act & Assert
        mockMvc.perform(get("/order/" + savedOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CREATED")))
                .andExpect(jsonPath("$.total", is(20.0)));
    }

    @Test
    void getOrderById_NonExistingOrder_ThrowsException() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/order/999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateOrderStatus_ExistingOrder_UpdatesStatus() throws Exception {
        // Arrange
        Order savedOrder = orderRepository.save(testOrder);

        // Act & Assert
        mockMvc.perform(put("/order/" + savedOrder.getId() + "/status")
                .param("status", "IN_PREPARATION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("IN_PREPARATION")));
    }

    @Test
    void cancelOrder_ExistingOrder_CancelsOrder() throws Exception {
        // Arrange
        Order savedOrder = orderRepository.save(testOrder);

        // Act & Assert
        mockMvc.perform(put("/order/" + savedOrder.getId() + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")));
    }

    @Test
    void getOrdersByTable_ReturnsOrdersForTable() throws Exception {
        // Arrange
        Order savedOrder = orderRepository.save(testOrder);

        // Act & Assert
        mockMvc.perform(get("/order/table/" + testTable.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].status", is("CREATED")));
    }
}