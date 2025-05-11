package com.restaurant.ordering.Repository;


import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find all orders by status (e.g., NEW, IN_PREPARATION)
    List<Order> findByStatus(OrderStatus status);

    // Find an order by its ID (returns Optional)
    Optional<Order> findById(Long id);

    // Find all orders by a customer
    List<Order> findByCustomerId(Long customerId);

    // Find orders that were created after a certain date
    List<Order> findByCreatedAtAfter(LocalDateTime createdAt);
}
