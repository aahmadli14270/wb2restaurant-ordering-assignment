package com.restaurant.ordering.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Enums.UserRole;
import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Model.TableItem;
import com.restaurant.ordering.Model.Users.User;
import com.restaurant.ordering.Model.Users.Waiter;
import com.restaurant.ordering.Repository.OrderRepository;
import com.restaurant.ordering.Repository.TableItemRepository;
import com.restaurant.ordering.Repository.UserRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class WaiterControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TableItemRepository tableItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Order readyOrder;
    private TableItem testTable;
    private User testUser;
    private String authToken;
    private final String TEST_USERNAME = "testwaiter";
    private final String TEST_PASSWORD = "password";

    @BeforeEach
    void setUp() throws Exception {
        // Clean up existing data
        orderRepository.deleteAll();
        tableItemRepository.deleteAll();
        userRepository.deleteAll();

        // Create test table
        testTable = new TableItem();
        testTable.setTableId(101L);
        testTable.setOrders(new ArrayList<>());
        testTable = tableItemRepository.save(testTable);

        // Create a ready order
        readyOrder = new Order();
        readyOrder.setTable(testTable);
        readyOrder.setStatus(OrderStatus.READY);
        readyOrder.setTotal(40.0);
        readyOrder.setItems(new ArrayList<>());
        readyOrder = orderRepository.save(readyOrder);

        // Create test waiter user
        testUser = new Waiter(TEST_USERNAME, passwordEncoder.encode(TEST_PASSWORD));
        testUser.setRole(UserRole.WAITER);
        testUser = userRepository.save(testUser);

        // Get JWT token
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", TEST_USERNAME);
        credentials.put("password", TEST_PASSWORD);

        String response = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONObject jsonResponse = new JSONObject(response);
        authToken = jsonResponse.getString("token");
    }

    @Test
    void getReadyOrders_ReturnsReadyOrders() throws Exception {
        // Check if there are any ready orders
        List<Order> readyOrders = orderRepository.findByStatus(OrderStatus.READY);

        if (readyOrders.isEmpty()) {
            // If no ready orders, expect 404 NOT_FOUND
            mockMvc.perform(get("/api/waiter/ready-orders")
                    .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().isNotFound());
        } else {
            // If ready orders exist, expect 200 OK with non-empty list
            mockMvc.perform(get("/api/waiter/ready-orders")
                    .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                    .andExpect(jsonPath("$[0].status", is("READY")));
        }
    }

    @Test
    void markOrderDelivered_ValidOrder_UpdatesStatus() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/waiter/" + readyOrder.getId() + "/deliver")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Order marked as DELIVERED"));

        // Verify order status was updated
        Order updatedOrder = orderRepository.findById(readyOrder.getId()).orElseThrow();
        assert updatedOrder.getStatus() == OrderStatus.DELIVERED;
    }

    @Test
    void markOrderDelivered_InvalidOrder_ThrowsException() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/waiter/999/deliver")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }
}
