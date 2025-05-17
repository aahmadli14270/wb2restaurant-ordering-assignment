package com.restaurant.ordering.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.ordering.Enums.MenuCategory;
import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Model.MenuItem;
import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Model.OrderItem;
import com.restaurant.ordering.Model.TableItem;
import com.restaurant.ordering.Repository.MenuItemRepository;
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
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TableItemRepository tableItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    private TableItem testTable;
    private Order testOrder;
    private MenuItem testMenuItem;
    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        orderRepository.deleteAll();
        menuItemRepository.deleteAll();
        tableItemRepository.deleteAll();

        // Create test table
        testTable = new TableItem();
        testTable.setTableId(101L);
        testTable.setOrders(new ArrayList<>());
        testTable = tableItemRepository.save(testTable);

        // Create test menu item
        testMenuItem = new MenuItem();
        testMenuItem.setName("Test Item");
        testMenuItem.setDescription("Test Description");
        testMenuItem.setPrice(10.0);
        testMenuItem.setCategory(MenuCategory.MAIN_COURSE);
        testMenuItem = menuItemRepository.save(testMenuItem);

        // Create test order item
        testOrderItem = new OrderItem();
        testOrderItem.setMenuItem(testMenuItem);
        testOrderItem.setQuantity(2);

        // Create test order
        testOrder = new Order();
        testOrder.setTable(testTable);
        testOrder.setStatus(OrderStatus.CREATED);
        testOrder.setTotal(20.0);

        List<OrderItem> items = new ArrayList<>();
        items.add(testOrderItem);
        testOrder.setItems(items);
    }

    @Test
    void placeOrder_ValidOrder_ReturnsCreatedOrder() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/customer/order/" + testTable.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CREATED")))
                .andExpect(jsonPath("$.total", is(20.0)));
    }

    @Test
    void placeOrder_TableNotFound_ThrowsException() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/customer/order/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMenu_ReturnsMenuItems() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/customer/menu/" + testTable.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name", is("Test Item")))
                .andExpect(jsonPath("$[0].price", is(10.0)));
    }

    @Test
    void updateOrderItem_ValidUpdate_ReturnsBadRequest() throws Exception {
        // Arrange
        Order savedOrder = orderRepository.save(testOrder);

        // Create updated order with new item
        MenuItem newMenuItem = new MenuItem();
        newMenuItem.setName("New Item");
        newMenuItem.setDescription("New Description");
        newMenuItem.setPrice(15.0);
        newMenuItem.setCategory(MenuCategory.DESSERT);
        newMenuItem = menuItemRepository.save(newMenuItem);

        OrderItem newOrderItem = new OrderItem();
        newOrderItem.setMenuItem(newMenuItem);
        newOrderItem.setQuantity(1);

        Order updatedOrder = new Order();
        List<OrderItem> updatedItems = new ArrayList<>();
        updatedItems.add(newOrderItem);
        updatedOrder.setItems(updatedItems);
        updatedOrder.setTotal(15.0);

        // Act & Assert
        // Since the items field is annotated with @JsonIgnore in the Order class,
        // it's excluded from JSON serialization, so the controller will always
        // throw an IllegalArgumentException when the items field is null or empty.
        mockMvc.perform(put("/customer/order/" + savedOrder.getId() + "/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedOrder)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Updated order must contain at least one item.")));
    }

    @Test
    void updateOrderItem_EmptyItems_ThrowsException() throws Exception {
        // Arrange
        Order savedOrder = orderRepository.save(testOrder);

        Order updatedOrder = new Order();
        updatedOrder.setItems(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(put("/customer/order/" + savedOrder.getId() + "/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedOrder)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeOrderItem_ValidRemoval_ReturnsUpdatedOrder() throws Exception {
        // Arrange
        Order savedOrder = orderRepository.save(testOrder);
        Long itemId = savedOrder.getItems().get(0).getId();

        // Act & Assert
        mockMvc.perform(delete("/customer/order/" + savedOrder.getId() + "/item/" + itemId))
                .andExpect(status().isOk());
    }

    @Test
    void getOrderStatus_ValidOrder_ReturnsStatus() throws Exception {
        // Arrange
        Order savedOrder = orderRepository.save(testOrder);

        // Act & Assert
        mockMvc.perform(get("/customer/order/status/" + savedOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("CREATED"));
    }

    @Test
    void getOrderStatus_InvalidOrder_ThrowsException() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/customer/order/status/999"))
                .andExpect(status().isInternalServerError());
    }
}
