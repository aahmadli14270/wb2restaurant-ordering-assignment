package com.restaurant.ordering.ServiceImpl;

import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Repository.OrderRepository;
import com.restaurant.ordering.Service.WaiterService;
import lombok.*;
import org.springframework.stereotype.Service;
import java.util.*;
import com.restaurant.ordering.Enums.OrderStatus;

@Service
@RequiredArgsConstructor
public class WaiterServiceImpl implements WaiterService {

    private final OrderRepository orderRepository;

    @Override
    public List<Order> getReadyOrders() {
        return orderRepository.findByStatus(OrderStatus.READY);
    }

    @Override
    public void markOrderDelivered(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
    }
}
