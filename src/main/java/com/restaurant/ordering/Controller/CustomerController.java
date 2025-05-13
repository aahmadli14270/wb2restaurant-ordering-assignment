package com.restaurant.ordering.Controller;

import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Model.TableItem;
import com.restaurant.ordering.Repository.TableItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.restaurant.ordering.Model.MenuItem;
import com.restaurant.ordering.Service.OrderService;
import com.restaurant.ordering.Service.MenuService;
import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TableItemRepository tableItemRepository;

    @PostMapping("/order/{tableId}")
    public ResponseEntity<?> placeOrder(@PathVariable Long tableId, @RequestBody Order order) {
        TableItem table = tableItemRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found with ID: " + tableId));

        order.setTable(table); // this sets the actual TableItem object
        Order savedOrder = orderService.createOrder(order);
        return ResponseEntity.ok(savedOrder);
    }

    // Simulated endpoint from QR code scan (tableId embedded)
    @GetMapping("/menu/{tableId}")
    public List<MenuItem> getMenu(@PathVariable String tableId) {
        // Return menu for the customer at specific table
        return menuService.getAllMenuItems();
    }


    @PutMapping("/order/{orderId}/item")
    public Order updateOrderItem(@PathVariable Long orderId, @RequestBody Order updatedOrder) {
        return orderService.updateOrderItems(orderId, updatedOrder);
    }

    @DeleteMapping("/order/{orderId}/item/{itemId}")
    public Order removeOrderItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        return orderService.removeItemFromOrder(orderId, itemId);
    }

    @GetMapping("/order/status/{orderId}")
    public OrderStatus getOrderStatus(@PathVariable Long orderId) {
        return orderService.getOrderStatus(orderId);
    }
}