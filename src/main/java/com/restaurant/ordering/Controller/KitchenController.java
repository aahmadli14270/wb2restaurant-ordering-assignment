package com.restaurant.ordering.Controller;


import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Service.KitchenStaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/kitchen")
@RequiredArgsConstructor
public class KitchenController {

    private final KitchenStaffService kitchenStaffService;

    // ✅ Get all incoming orders (status = CREATED)
    @GetMapping("/incoming")
    public ResponseEntity<List<Order>> getIncomingOrders() {
        List<Order> orders = kitchenStaffService.getIncomingOrders();
        if (orders.isEmpty()) {
            throw new NoSuchElementException("No incoming orders found.");
        }
        return ResponseEntity.ok(kitchenStaffService.getIncomingOrders());
    }

    // ✅ Mark an order as "IN_PREPARATION"
    @PutMapping("/{orderId}/prepare")
    public ResponseEntity<String> markInPreparation(@PathVariable Long orderId) {
        try {
            kitchenStaffService.markOrderInPreparation(orderId);
            return ResponseEntity.ok("Order marked as IN_PREPARATION");
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Order with ID " + orderId + " not found.");
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Order cannot be marked as IN_PREPARATION: " + e.getMessage());
        }
    }

    // ✅ Mark an order as "READY"
    @PutMapping("/{orderId}/ready")
    public ResponseEntity<String> markReady(@PathVariable Long orderId) {
        try {
            kitchenStaffService.markOrderReady(orderId);
            return ResponseEntity.ok("Order marked as READY");
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Order with ID " + orderId + " not found.");
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Order cannot be marked as READY: " + e.getMessage());
        }
    }

    // ✅ Get all orders that are currently being prepared
    @GetMapping("/preparing")
    public ResponseEntity<List<Order>> getOrdersInPreparation() {
        List<Order> preparingOrders = kitchenStaffService.getOrdersInPreparation();
        if (preparingOrders.isEmpty()) {
            throw new NoSuchElementException("No orders currently in preparation.");
        }
        return ResponseEntity.ok(kitchenStaffService.getOrdersInPreparation());
    }

    // ✅ Get all orders that are ready
    @GetMapping("/ready")
    public ResponseEntity<List<Order>> getReadyOrders() {
        List<Order> readyOrders = kitchenStaffService.getReadyOrders();
        if (readyOrders.isEmpty()) {
            throw new NoSuchElementException("No orders are currently ready.");
        }
        return ResponseEntity.ok(kitchenStaffService.getReadyOrders());
    }
}
