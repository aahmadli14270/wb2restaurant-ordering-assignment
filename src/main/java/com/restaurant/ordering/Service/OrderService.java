package com.restaurant.ordering.Service;

import com.restaurant.ordering.DTO.CreateOrderDTO;
import com.restaurant.ordering.DTO.OrderDTO;
import com.restaurant.ordering.Enums.OrderStatus;
import java.util.List;

public interface OrderService {
    // Create and update operations
    OrderDTO createOrder(CreateOrderDTO orderDTO);
    OrderDTO updateOrderStatus(Long orderId, OrderStatus status);
    OrderDTO updateOrderItems(Long orderId, CreateOrderDTO updatedOrder);
    OrderDTO removeItemFromOrder(Long orderId, Long itemId);
    
    // Get single order operations
    OrderDTO getOrder(Long orderId);
    OrderDTO getOrderByTable(Long tableId);
    OrderStatus getOrderStatus(Long orderId);
    
    // Get multiple orders operations
    List<OrderDTO> getOrdersByStatus(OrderStatus status);
    List<OrderDTO> getOrdersByTableId(Long tableId);
    List<OrderDTO> getAllOrders();
}