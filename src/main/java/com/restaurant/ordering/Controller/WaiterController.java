package com.restaurant.ordering.Controller;

import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Service.WaiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/waiter")
@RequiredArgsConstructor
public class WaiterController {

    private final WaiterService waiterService;

    // ✅ Get all orders that are ready to be delivered
    @GetMapping("/ready-orders")
    public ResponseEntity<List<Order>> getReadyOrders() {
        return ResponseEntity.ok(waiterService.getReadyOrders());
    }

    // ✅ Mark an order as delivered
    @PutMapping("/{orderId}/deliver")
    public ResponseEntity<String> markOrderDelivered(@PathVariable Long orderId) {
        waiterService.markOrderDelivered(orderId);
        return ResponseEntity.ok("Order marked as DELIVERED");
    }
}