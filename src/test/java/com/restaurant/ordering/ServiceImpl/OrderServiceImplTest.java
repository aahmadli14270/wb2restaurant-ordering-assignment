package com.restaurant.ordering.ServiceImpl;

import com.restaurant.ordering.Enums.MenuCategory;
import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Model.MenuItem;
import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Model.OrderItem;
import com.restaurant.ordering.Model.TableItem;
import com.restaurant.ordering.Repository.OrderRepository;
import com.restaurant.ordering.Repository.TableItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private TableItemRepository tableItemRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private TableItem testTable;
    private List<OrderItem> testItems;

    @BeforeEach
    void setUp() {
        testTable = new TableItem();
        testTable.setId(1L);
        testTable.setTableId(101L);

        MenuItem menuItem = new MenuItem();
        menuItem.setId(1L);
        menuItem.setName("Test Item");
        menuItem.setPrice(10.0);
        menuItem.setCategory(MenuCategory.MAIN_COURSE);

        testItems = new ArrayList<>();
        OrderItem item1 = new OrderItem();
        item1.setId(1L);
        item1.setQuantity(2);
        item1.setMenuItem(menuItem);
        testItems.add(item1);

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setTable(testTable);
        testOrder.setItems(testItems);
        testOrder.setStatus(OrderStatus.CREATED);
        testOrder.setTotal(20.0);
    }

    @Test
    void createOrder_Success() {
        // Arrange
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.createOrder(testOrder);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(OrderStatus.CREATED, result.getStatus());
        assertEquals(20.0, result.getTotal());
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    void getOrderById_ExistingOrder_ReturnsOrder() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        Order result = orderService.getOrderById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(OrderStatus.CREATED, result.getStatus());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderById_NonExistingOrder_ThrowsException() {
        // Arrange
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderById(99L);
        });
        assertEquals("Order not found", exception.getMessage());
        verify(orderRepository, times(1)).findById(99L);
    }

    @Test
    void updateOrderStatus_ExistingOrder_UpdatesStatus() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.updateOrderStatus(1L, OrderStatus.IN_PREPARATION);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.IN_PREPARATION, result.getStatus());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    void updateOrderStatus_NonExistingOrder_ThrowsException() {
        // Arrange
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateOrderStatus(99L, OrderStatus.IN_PREPARATION);
        });
        assertEquals("Order not found", exception.getMessage());
        verify(orderRepository, times(1)).findById(99L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getAllOrders_ReturnsAllOrders() {
        // Arrange
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findAll()).thenReturn(orders);

        // Act
        List<Order> result = orderService.getAllOrders();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getOrdersByTableId_ReturnsOrdersForTable() {
        // Arrange
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findByTableId(1L)).thenReturn(orders);

        // Act
        List<Order> result = orderService.getOrdersByTableId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(1L, result.get(0).getTable().getId());
        verify(orderRepository, times(1)).findByTableId(1L);
    }

    @Test
    void updateOrderItems_ExistingOrder_UpdatesItems() {
        // Arrange
        Order updatedOrder = new Order();
        updatedOrder.setId(1L);

        MenuItem newMenuItem = new MenuItem();
        newMenuItem.setId(2L);
        newMenuItem.setName("New Test Item");
        newMenuItem.setPrice(15.0);
        newMenuItem.setCategory(MenuCategory.DESSERT);

        List<OrderItem> newItems = new ArrayList<>();
        OrderItem newItem = new OrderItem();
        newItem.setId(2L);
        newItem.setQuantity(1);
        newItem.setMenuItem(newMenuItem);
        newItems.add(newItem);

        updatedOrder.setItems(newItems);
        updatedOrder.setTotal(15.0);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.updateOrderItems(1L, updatedOrder);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(newItems, result.getItems());
        assertEquals(15.0, result.getTotal());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    void updateOrderItems_NonExistingOrder_ThrowsException() {
        // Arrange
        Order updatedOrder = new Order();
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateOrderItems(99L, updatedOrder);
        });
        assertEquals("Order not found", exception.getMessage());
        verify(orderRepository, times(1)).findById(99L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void removeItemFromOrder_ExistingOrderAndItem_RemovesItem() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.removeItemFromOrder(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertTrue(result.getItems().isEmpty());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    void removeItemFromOrder_NonExistingOrder_ThrowsException() {
        // Arrange
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.removeItemFromOrder(99L, 1L);
        });
        assertEquals("Order not found", exception.getMessage());
        verify(orderRepository, times(1)).findById(99L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrderStatus_ExistingOrder_ReturnsStatus() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        OrderStatus result = orderService.getOrderStatus(1L);

        // Assert
        assertEquals(OrderStatus.CREATED, result);
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderStatus_NonExistingOrder_ThrowsException() {
        // Arrange
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderStatus(99L);
        });
        assertEquals("Order not found", exception.getMessage());
        verify(orderRepository, times(1)).findById(99L);
    }

    @Test
    void getOrdersByStatus_ReturnsOrdersWithSpecifiedStatus() {
        // Arrange
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findByStatus(OrderStatus.CREATED)).thenReturn(orders);

        // Act
        List<Order> result = orderService.getOrdersByStatus(OrderStatus.CREATED);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(OrderStatus.CREATED, result.get(0).getStatus());
        verify(orderRepository, times(1)).findByStatus(OrderStatus.CREATED);
    }
}
