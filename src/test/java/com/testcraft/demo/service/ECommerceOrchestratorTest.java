package com.testcraft.demo.service;

import com.testcraft.demo.model.*;
import com.testcraft.demo.service.InventoryManagementService;
import com.testcraft.demo.service.OrderManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
public class ECommerceOrchestratorTest {

    @Mock
    private InventoryManagementService inventoryService;

    @Mock
    private OrderManagementService orderService;

    @Mock
    private StockObserver stockObserver;

    @InjectMocks
    private ECommerceOrchestrator eCommerceOrchestrator;

    private Map<String, Customer> customers;

    @BeforeEach
    public void setup() {
        customers = new ConcurrentHashMap<>();
        eCommerceOrchestrator = new ECommerceOrchestrator();
        eCommerceOrchestrator.customers = customers;
    }

    @Test
    public void testPlaceOrder_Success() throws Exception {
        // Arrange
        String customerId = "C001";
        Map<String, Integer> productQuantities = new HashMap<>();
        productQuantities.put("P001", 2);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Customer customer = new Customer("C001", "Rahul Sharma", "rahul@example.com", "Premium");
        customers.put(customerId, customer);

        Product product = new Product("P001", "Product 1", 10.99);
        when(inventoryService.getProduct("P001")).thenReturn(product);

        // Act
        Order order = eCommerceOrchestrator.placeOrder(customerId, productQuantities, paymentMethod);

        // Assert
        assertNotNull(order);
        assertEquals(customerId, order.getCustomerId());
        assertEquals(paymentMethod, order.getPaymentMethod());
        assertEquals(2, order.getOrderItems().size());
        assertEquals("P001", order.getOrderItems().get(0).getProductId());
        assertEquals(2, order.getOrderItems().get(0).getQuantity());
    }

    @Test
    public void testPlaceOrder_CustomerNotFound() {
        // Arrange
        String customerId = "C005";
        Map<String, Integer> productQuantities = new HashMap<>();
        productQuantities.put("P001", 2);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> eCommerceOrchestrator.placeOrder(customerId, productQuantities, paymentMethod));
    }

    @Test
    public void testPlaceOrder_ProductNotFound() throws Exception {
        // Arrange
        String customerId = "C001";
        Map<String, Integer> productQuantities = new HashMap<>();
        productQuantities.put("P005", 2);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Customer customer = new Customer("C001", "Rahul Sharma", "rahul@example.com", "Premium");
        customers.put(customerId, customer);

        // Act and Assert
        assertThrows(Exception.class, () -> eCommerceOrchestrator.placeOrder(customerId, productQuantities, paymentMethod));
    }

    @Test
    public void testPlaceOrder_InsufficientStock() throws Exception {
        // Arrange
        String customerId = "C001";
        Map<String, Integer> productQuantities = new HashMap<>();
        productQuantities.put("P001", 10);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Customer customer = new Customer("C001", "Rahul Sharma", "rahul@example.com", "Premium");
        customers.put(customerId, customer);

        Product product = new Product("P001", "Product 1", 10.99);
        when(inventoryService.getProduct("P001")).thenReturn(product);
        when(inventoryService.reserveStock("P001", 10)).thenReturn(false);

        // Act and Assert
        assertThrows(Exception.class, () -> eCommerceOrchestrator.placeOrder(customerId, productQuantities, paymentMethod));
    }

    @Test
    public void testInitializeSampleCustomers() {
        // Act
        eCommerceOrchestrator.initializeSampleCustomers();

        // Assert
        assertEquals(4, eCommerceOrchestrator.customers.size());
    }
}