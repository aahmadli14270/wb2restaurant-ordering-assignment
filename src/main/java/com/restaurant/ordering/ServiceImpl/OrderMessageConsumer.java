package com.restaurant.ordering.ServiceImpl;

import com.restaurant.ordering.Config.RabbitMQConfig;
import com.restaurant.ordering.DTO.OrderDTO;
import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Repository.OrderRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OrderMessageConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderMessageConsumer.class);
    private final OrderRepository orderRepository;
    
    public OrderMessageConsumer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void receiveOrder(OrderDTO message) {
        try {
            logger.info("Received order: {}", message.getId());
            
            // Process the order message
            Order order = orderRepository.findById(message.getId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + message.getId()));
                
            // Here you would implement the kitchen display system logic
            // For example:
            // - Update order status
            // - Notify kitchen staff
            // - Update display boards
            // - Send notifications
            
            logger.info("Processing order: {} for table: {}", order.getId(), order.getTable().getId());
            
            // Add your kitchen processing logic here
            
        } catch (Exception e) {
            logger.error("Error processing order: {}", message.getId(), e);
            // Implement retry logic or dead letter queue handling here
            throw e; // Re-throw to trigger RabbitMQ's retry mechanism
        }
    }
} 