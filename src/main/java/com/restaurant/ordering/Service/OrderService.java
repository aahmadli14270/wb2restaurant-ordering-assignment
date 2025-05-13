package com.restaurant.ordering.Service;


import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Model.Order;

import java.util.List;


public interface OrderService {
    Order createOrder(Order order);
    Order updateOrderItems(Long orderId, Order updatedOrder);
    Order removeItemFromOrder(Long orderId, Long itemId);
    OrderStatus getOrderStatus(Long orderId);
    List<Order> getOrdersByStatus(OrderStatus status);
    Order updateOrderStatus(Long orderId, OrderStatus status);
    List<Order> getOrdersByTableId(Long tableId);
    List<Order> getAllOrders();  // Get all orders
    Order getOrderById(Long orderId);
}