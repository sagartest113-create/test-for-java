package com.testcraft.demo.service;

import com.testcraft.demo.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitWebConfig
public class OrderManagementServiceTest {

    @Mock
    private ConcurrentHashMap<String, Order> orders;

    @Mock
    private ConcurrentHashMap<String, List<Order>> customerOrders;

    @Mock
    private ExecutorService executorService;

    @Mock
    private ScheduledExecutorService scheduledExecutor;

    @Mock
    private AtomicInteger orderCounter;

    @Mock
    private BlockingQueue<OrderProcessingTask> processingQueue;

    @InjectMocks
    private OrderManagementService orderManagementService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderManagementService).build();
    }

    @Test
    public void testCreateOrder() throws OrderProcessingException {
        // Arrange
        Order order = new Order("ORDER-1", "CUSTOMER-1", OrderStatus.PENDING);

        // Act
        Order createdOrder = orderManagementService.createOrder(order);

        // Assert
        assertNotNull(createdOrder);
        assertEquals("ORDER-1", createdOrder.getOrderId());
        assertEquals("CUSTOMER-1", createdOrder.getCustomerId());
        assertEquals(OrderStatus.PENDING, createdOrder.getStatus());
        assertTrue(orderManagementService.orders.containsKey("ORDER-1"));
        assertTrue(orderManagementService.customerOrders.containsKey("CUSTOMER-1"));
        assertEquals(1, orderManagementService.orderCounter.get());
    }

    @Test
    public void testCreateOrderNullOrder() {
        // Act and Assert
        assertThrows(OrderProcessingException.class, () -> orderManagementService.createOrder(null));
    }

    @Test
    public void testCreateOrderInvalidOrderStatus() {
        // Arrange
        Order order = new Order("ORDER-1", "CUSTOMER-1", OrderStatus.CONFIRMED);

        // Act and Assert
        assertThrows(OrderProcessingException.class, () -> orderManagementService.createOrder(order));
    }

    @Test
    public void testProcessOrderAsync() throws InterruptedException, ExecutionException {
        // Arrange
        Order order = new Order("ORDER-1", "CUSTOMER-1", OrderStatus.PENDING);
        when(orders.get("ORDER-1")).thenReturn(order);

        // Act
        CompletableFuture<Order> future = orderManagementService.processOrderAsync("ORDER-1");
        Order processedOrder = future.get();

        // Assert
        assertNotNull(processedOrder);
        assertEquals("ORDER-1", processedOrder.getOrderId());
        assertEquals("CUSTOMER-1", processedOrder.getCustomerId());
        assertEquals(OrderStatus.CONFIRMED, processedOrder.getStatus());
        verify(orders, times(1)).put(anyString(), any(Order.class));
        verify(orders, times(1)).get("ORDER-1");
    }

    @Test
    public void testProcessOrderAsyncOrderNotFound() {
        // Arrange
        when(orders.get("ORDER-1")).thenReturn(null);

        // Act and Assert
        assertThrows(OrderProcessingException.class, () -> orderManagementService.processOrderAsync("ORDER-1"));
    }

    @Test
    public void testProcessOrderAsyncOrderNotPending() {
        // Arrange
        Order order = new Order("ORDER-1", "CUSTOMER-1", OrderStatus.CONFIRMED);
        when(orders.get("ORDER-1")).thenReturn(order);

        // Act and Assert
        assertThrows(OrderProcessingException.class, () -> orderManagementService.processOrderAsync("ORDER-1"));
    }

    @Test
    public void testProcessOrderAsyncMultipleCalls() throws InterruptedException, ExecutionException {
        // Arrange
        Order order = new Order("ORDER-1", "CUSTOMER-1", OrderStatus.PENDING);
        when(orders.get("ORDER-1")).thenReturn(order);

        // Act
        CompletableFuture<Order> future1 = orderManagementService.processOrderAsync("ORDER-1");
        CompletableFuture<Order> future2 = orderManagementService.processOrderAsync("ORDER-1");
        Order processedOrder1 = future1.get();
        Order processedOrder2 = future2.get();

        // Assert
        assertNotNull(processedOrder1);
        assertNotNull(processedOrder2);
        assertEquals("ORDER-1", processedOrder1.getOrderId());
        assertEquals("CUSTOMER-1", processedOrder1.getCustomerId());
        assertEquals(OrderStatus.CONFIRMED, processedOrder1.getStatus());
        assertEquals("ORDER-1", processedOrder2.getOrderId());
        assertEquals("CUSTOMER-1", processedOrder2.getCustomerId());
        assertEquals(OrderStatus.CONFIRMED, processedOrder2.getStatus());
        verify(orders, times(2)).put(anyString(), any(Order.class));
        verify(orders, times(2)).get("ORDER-1");
    }
}