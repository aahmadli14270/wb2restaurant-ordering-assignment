package com.restaurant.ordering.ServiceImpl;

import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Model.OrderItem;
import com.restaurant.ordering.Repository.OrderRepository;
import com.restaurant.ordering.Repository.TableItemRepository;
import com.restaurant.ordering.Service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Iterator;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TableItemRepository tableItemRepository;

    // Create a new order
    @Override
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    // Update order items (e.g., add/remove items)
    @Override
    public Order updateOrderItems(Long orderId, Order updatedOrder) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setItems(updatedOrder.getItems());
            order.setTotal(updatedOrder.getTotal());
            return orderRepository.save(order);
        }
        throw new RuntimeException("Order not found");
    }

    // Remove an item from the order
    @Override
    public Order removeItemFromOrder(Long orderId, Long itemId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.getItems().removeIf(item -> item.getId().equals(itemId));
            return orderRepository.save(order);
        }
        throw new RuntimeException("Order not found");
    }

    // Get order status by order ID
    @Override
    public OrderStatus getOrderStatus(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            return orderOptional.get().getStatus();
        }
        throw new RuntimeException("Order not found");
    }

    // Get orders by status (e.g., CREATED, IN_PREPARATION, etc.)
    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    // Update the status of an order (e.g., from CREATED to IN_PREPARATION)
    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setStatus(status);
            return orderRepository.save(order);
        }
        throw new RuntimeException("Order not found");
    }

    // Get orders by table ID
    @Override
    public List<Order> getOrdersByTableId(Long tableId) {
        return orderRepository.findByTableId(tableId);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();  // Retrieve all orders
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
    }
}