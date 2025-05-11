package com.restaurant.ordering.ServiceImpl;

import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Repository.OrderRepository;
import com.restaurant.ordering.Service.KitchenStaffService;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class KitchenStaffServiceImpl implements KitchenStaffService {

    private final OrderRepository orderRepository;

    @Override
    public List<Order> getIncomingOrders() {
        return orderRepository.findByStatus(OrderStatus.CREATED);
    }

    @Override
    public void markOrderInPreparation(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(OrderStatus.IN_PREPARATION);
        orderRepository.save(order);
    }

    @Override
    public void markOrderReady(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(OrderStatus.READY);
        orderRepository.save(order);
    }
}

