package com.restaurant.ordering.Controller;


import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Service.KitchenStaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kitchen")
@RequiredArgsConstructor
public class KitchenController {

    private final KitchenStaffService kitchenStaffService;

    // ✅ Get all incoming orders (status = CREATED)
    @GetMapping("/incoming")
    public ResponseEntity<List<Order>> getIncomingOrders() {
        return ResponseEntity.ok(kitchenStaffService.getIncomingOrders());
    }

    // ✅ Mark an order as "IN_PREPARATION"
    @PutMapping("/{orderId}/prepare")
    public ResponseEntity<String> markInPreparation(@PathVariable Long orderId) {
        kitchenStaffService.markOrderInPreparation(orderId);
        return ResponseEntity.ok("Order marked as IN_PREPARATION");
    }

    // ✅ Mark an order as "READY"
    @PutMapping("/{orderId}/ready")
    public ResponseEntity<String> markReady(@PathVariable Long orderId) {
        kitchenStaffService.markOrderReady(orderId);
        return ResponseEntity.ok("Order marked as READY");
    }

    // ✅ Get all orders that are currently being prepared
    @GetMapping("/preparing")
    public ResponseEntity<List<Order>> getOrdersInPreparation() {
        return ResponseEntity.ok(kitchenStaffService.getOrdersInPreparation());
    }

    // ✅ Get all orders that are ready
    @GetMapping("/ready")
    public ResponseEntity<List<Order>> getReadyOrders() {
        return ResponseEntity.ok(kitchenStaffService.getReadyOrders());
    }
}
