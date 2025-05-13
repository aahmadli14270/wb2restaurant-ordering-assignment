package com.restaurant.ordering.Controller;

import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Model.TableItem;
import com.restaurant.ordering.Repository.TableItemRepository;
import com.restaurant.ordering.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private TableItemRepository tableItemRepository;

    // Place a new order for a specific table
    @PostMapping("/{tableId}")
    public Order placeOrder(@PathVariable Long tableId, @RequestBody Order order) {
        TableItem table = tableItemRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found with ID: " + tableId));
        order.setTable(table);
        return orderService.createOrder(order);
    }

    // Get all orders (optional: for kitchen or manager)
    @GetMapping("/all")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // Get order by ID
    @GetMapping("/{orderId}")
    public Order getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

    // Update order status
    @PutMapping("/{orderId}/status")
    public Order updateOrderStatus(@PathVariable Long orderId, @RequestParam OrderStatus status) {
        return orderService.updateOrderStatus(orderId, status);
    }

    // Cancel an order
    @PutMapping("/{orderId}/cancel")
    public Order cancelOrder(@PathVariable Long orderId) {
        return orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);
    }

    // Get all orders by table
    @GetMapping("/table/{tableId}")
    public List<Order> getOrdersByTable(@PathVariable Long tableId) {
        return orderService.getOrdersByTableId(tableId);
    }
}