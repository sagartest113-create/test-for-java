package com.testcraft.demo.service;

import com.testcraft.demo.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
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
    public void testCreateOrderSuccess() throws OrderProcessingException {
        // Arrange
        Order order = new Order("ORDER-1", "CUSTOMER-1", OrderStatus.PENDING);
        when(orders.put(anyString(), any(Order.class))).thenReturn(order);
        when(customerOrders.computeIfAbsent(anyString(), any())).thenReturn(new CopyOnWriteArrayList<>());
        when(orderCounter.incrementAndGet()).thenReturn(1);

        // Act
        Order createdOrder = orderManagementService.createOrder(order);

        // Assert
        assertNotNull(createdOrder);
        assertEquals("ORDER-1", createdOrder.getOrderId());
        assertEquals("CUSTOMER-1", createdOrder.getCustomerId());
        assertEquals(OrderStatus.PENDING, createdOrder.getStatus());
        assertEquals(1, orderCounter.get());
    }

    @Test
    public void testCreateOrderFailure_NullOrder() {
        // Arrange
        when(orders.put(anyString(), any(Order.class))).thenThrow(new NullPointerException("Order cannot be null"));

        // Act and Assert
        assertThrows(NullPointerException.class, () -> orderManagementService.createOrder(null));
    }

    @Test
    public void testProcessOrderAsyncSuccess() throws InterruptedException, ExecutionException {
        // Arrange
        Order order = new Order("ORDER-1", "CUSTOMER-1", OrderStatus.PENDING);
        when(orders.get(anyString())).thenReturn(order);
        when(order.updateStatus(any(OrderStatus.class))).thenReturn(order);

        // Act
        CompletableFuture<Order> future = orderManagementService.processOrderAsync("ORDER-1");
        Order processedOrder = future.get();

        // Assert
        assertNotNull(processedOrder);
        assertEquals("ORDER-1", processedOrder.getOrderId());
        assertEquals("CUSTOMER-1", processedOrder.getCustomerId());
        assertEquals(OrderStatus.CONFIRMED, processedOrder.getStatus());
    }

    @Test
    public void testProcessOrderAsyncFailure_OrderNotFound() {
        // Arrange
        when(orders.get(anyString())).thenReturn(null);

        // Act and Assert
        assertThrows(OrderProcessingException.class, () -> orderManagementService.processOrderAsync("ORDER-1"));
    }

    @Test
    public void testProcessOrderAsyncFailure_OrderNotPending() {
        // Arrange
        Order order = new Order("ORDER-1", "CUSTOMER-1", OrderStatus.CONFIRMED);
        when(orders.get(anyString())).thenReturn(order);

        // Act and Assert
        assertThrows(OrderProcessingException.class, () -> orderManagementService.processOrderAsync("ORDER-1"));
    }
}