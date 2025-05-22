package com.restaurant.ordering.ServiceImpl;

import com.restaurant.ordering.Config.RabbitMQConfig;
import com.restaurant.ordering.DTO.OrderDTO;
import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Repository.OrderRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class OrderMessageConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderMessageConsumer.class);
    private final OrderRepository orderRepository;
    
    public OrderMessageConsumer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    @Transactional
    public void receiveOrder(OrderDTO message) {
        try {
            logger.info("Received order: {} with status: {}", message.getId(), message.getStatus());
            
            // Process the order message with items eagerly fetched
            Order order = orderRepository.findByIdWithItems(message.getId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + message.getId()));
            
            // Process based on order status
            switch (order.getStatus()) {
                case CREATED:
                    handleNewOrder(order);
                    break;
                case IN_PREPARATION:
                    handleOrderInPreparation(order);
                    break;
                case READY:
                    handleOrderReady(order);
                    break;
                case DELIVERED:
                    handleOrderDelivered(order);
                    break;
                default:
                    logger.info("Processing order: {} for table: {} with status: {}", 
                        order.getId(), order.getTable().getId(), order.getStatus());
            }
            
        } catch (Exception e) {
            logger.error("Error processing order: {}", message.getId(), e);
            // Re-throw to trigger RabbitMQ's retry mechanism
            throw e;
        }
    }
    
    @Transactional
    private void handleNewOrder(Order order) {
        logger.info("New order received - Order ID: {}, Table: {}, Items: {}", 
            order.getId(), 
            order.getTable().getId(),
            order.getItems().size());
            
        // Log order details for kitchen staff
        order.getItems().forEach(item -> 
            logger.info("Item: {} x{}", 
                item.getMenuItem().getName(),
                item.getQuantity())
        );
    }
    
    @Transactional
    private void handleOrderInPreparation(Order order) {
        logger.info("Order in preparation - Order ID: {}, Table: {}", 
            order.getId(), order.getTable().getId());
            
        // Calculate time since order creation
        long minutesSinceCreation = ChronoUnit.MINUTES.between(
            order.getCreatedAt(), 
            LocalDateTime.now()
        );
        
        if (minutesSinceCreation > 15) {
            logger.warn("Order {} has been in preparation for {} minutes", 
                order.getId(), minutesSinceCreation);
        }
    }
    
    @Transactional
    private void handleOrderReady(Order order) {
        // Calculate preparation time
        long preparationTime = ChronoUnit.MINUTES.between(
            order.getCreatedAt(), 
            LocalDateTime.now()
        );
        
        // Log detailed information for waiter notification
        logger.info("Order ready for delivery - Order ID: {}, Table: {}, Preparation Time: {} minutes", 
            order.getId(), 
            order.getTable().getId(), 
            preparationTime);
            
        // Log items for waiter reference
        order.getItems().forEach(item -> 
            logger.info("Ready for delivery - Item: {} x{}", 
                item.getMenuItem().getName(),
                item.getQuantity())
        );
    }
    
    @Transactional
    private void handleOrderDelivered(Order order) {
        logger.info("Order delivered - Order ID: {}, Table: {}", 
            order.getId(), order.getTable().getId());
            
        // Calculate total order lifecycle time
        long totalTime = ChronoUnit.MINUTES.between(
            order.getCreatedAt(), 
            LocalDateTime.now()
        );
        
        logger.info("Order {} completed in {} minutes", order.getId(), totalTime);
    }
} 