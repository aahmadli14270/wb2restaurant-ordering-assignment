package com.restaurant.ordering.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Enums.UserRole;
import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Model.TableItem;
import com.restaurant.ordering.Model.Users.KitchenStaff;
import com.restaurant.ordering.Model.Users.User;
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
public class KitchenControllerIntegrationTest {

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

    private Order createdOrder;
    private Order inPreparationOrder;
    private Order readyOrder;
    private TableItem testTable;
    private User testUser;
    private String authToken;
    private final String TEST_USERNAME = "testkitchen";
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

        // Create orders with different statuses
        createdOrder = new Order();
        createdOrder.setTable(testTable);
        createdOrder.setStatus(OrderStatus.CREATED);
        createdOrder.setTotal(20.0);
        createdOrder.setItems(new ArrayList<>());
        createdOrder = orderRepository.save(createdOrder);

        inPreparationOrder = new Order();
        inPreparationOrder.setTable(testTable);
        inPreparationOrder.setStatus(OrderStatus.IN_PREPARATION);
        inPreparationOrder.setTotal(30.0);
        inPreparationOrder.setItems(new ArrayList<>());
        inPreparationOrder = orderRepository.save(inPreparationOrder);

        readyOrder = new Order();
        readyOrder.setTable(testTable);
        readyOrder.setStatus(OrderStatus.READY);
        readyOrder.setTotal(40.0);
        readyOrder.setItems(new ArrayList<>());
        readyOrder = orderRepository.save(readyOrder);

        // Create test kitchen staff user
        testUser = new KitchenStaff(TEST_USERNAME, passwordEncoder.encode(TEST_PASSWORD));
        testUser.setRole(UserRole.KITCHEN);
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
    void getIncomingOrders_ReturnsCreatedOrders() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/kitchen/incoming")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].status", is("CREATED")))
                .andExpect(jsonPath("$[0].total", is(20.0)));
    }

    @Test
    void markInPreparation_ValidOrder_UpdatesStatus() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/kitchen/" + createdOrder.getId() + "/prepare")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Order marked as IN_PREPARATION"));

        // Verify order status was updated
        mockMvc.perform(get("/api/kitchen/preparing")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + createdOrder.getId() + ")].status", contains("IN_PREPARATION")));
    }

    @Test
    void markInPreparation_InvalidOrder_ThrowsException() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/kitchen/999/prepare")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void markReady_ValidOrder_UpdatesStatus() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/kitchen/" + inPreparationOrder.getId() + "/ready")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Order marked as READY"));

        // Verify order status was updated
        mockMvc.perform(get("/api/kitchen/ready")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + inPreparationOrder.getId() + ")].status", contains("READY")));
    }

    @Test
    void markReady_InvalidOrder_ThrowsException() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/kitchen/999/ready")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrdersInPreparation_ReturnsInPreparationOrders() throws Exception {
        // Check if there are any orders in preparation
        List<Order> inPreparationOrders = orderRepository.findByStatus(OrderStatus.IN_PREPARATION);

        if (inPreparationOrders.isEmpty()) {
            // If no orders in preparation, expect 404 NOT_FOUND
            mockMvc.perform(get("/api/kitchen/preparing")
                    .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().isNotFound());
        } else {
            // If orders in preparation exist, expect 200 OK with non-empty list
            mockMvc.perform(get("/api/kitchen/preparing")
                    .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                    .andExpect(jsonPath("$[0].status", is("IN_PREPARATION")));
        }
    }

    @Test
    void getReadyOrders_ReturnsReadyOrders() throws Exception {
        // Check if there are any ready orders
        List<Order> readyOrders = orderRepository.findByStatus(OrderStatus.READY);

        if (readyOrders.isEmpty()) {
            // If no ready orders, expect 404 NOT_FOUND
            mockMvc.perform(get("/api/kitchen/ready")
                    .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().isNotFound());
        } else {
            // If ready orders exist, expect 200 OK with non-empty list
            mockMvc.perform(get("/api/kitchen/ready")
                    .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                    .andExpect(jsonPath("$[0].status", is("READY")));
        }
    }
}
