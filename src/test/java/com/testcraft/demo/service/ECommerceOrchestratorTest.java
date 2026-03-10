package com.testcraft.demo.service;

import com.testcraft.demo.model.*;
import com.testcraft.demo.service.InventoryManagementService;
import com.testcraft.demo.service.OrderManagementService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitWebConfig
public class ECommerceOrchestratorTest {

    @Mock
    private OrderManagementService orderService;

    @Mock
    private InventoryManagementService inventoryService;

    @Mock
    private StockObserver stockObserver;

    @InjectMocks
    private ECommerceOrchestrator eCommerceOrchestrator;

    private static final String CUSTOMER_ID = "C001";
    private static final String PRODUCT_ID = "P001";
    private static final String PRODUCT_ID_2 = "P002";
    private static final PaymentMethod PAYMENT_METHOD = PaymentMethod.CREDIT_CARD;
    private static final Map<String, Integer> PRODUCT_QUANTITIES = new HashMap<>();
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();

    @BeforeEach
    public void setup() {
        eCommerceOrchestrator = new ECommerceOrchestrator();
        PRODUCT_QUANTITIES.put(PRODUCT_ID, 2);
        PRODUCT_QUANTITIES.put(PRODUCT_ID_2, 3);
    }

    @Test
    public void testPlaceOrder_Success() throws Exception {
        // Arrange
        Customer customer = new Customer(CUSTOMER_ID, "Rahul Sharma", "rahul@example.com", "Premium");
        eCommerceOrchestrator.customers.put(CUSTOMER_ID, customer);
        when(inventoryService.getProduct(PRODUCT_ID)).thenReturn(new Product(PRODUCT_ID, "Product 1", 10.0));
        when(inventoryService.getProduct(PRODUCT_ID_2)).thenReturn(new Product(PRODUCT_ID_2, "Product 2", 20.0));
        when(inventoryService.reserveStock(PRODUCT_ID, 2)).thenReturn(true);
        when(inventoryService.reserveStock(PRODUCT_ID_2, 3)).thenReturn(true);
        when(orderService.placeOrder(any(), any(), any())).thenReturn(new Order(CUSTOMER_ID, PAYMENT_METHOD));

        // Act
        Order order = eCommerceOrchestrator.placeOrder(CUSTOMER_ID, PRODUCT_QUANTITIES, PAYMENT_METHOD);

        // Assert
        assertNotNull(order);
        assertEquals(CUSTOMER_ID, order.getCustomerId());
        assertEquals(PAYMENT_METHOD, order.getPaymentMethod());
        assertEquals(2, order.getOrderItems().size());
        assertEquals(PRODUCT_ID, order.getOrderItems().get(0).getProductId());
        assertEquals(PRODUCT_ID_2, order.getOrderItems().get(1).getProductId());
    }

    @Test
    public void testPlaceOrder_CustomerNotFound() {
        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> eCommerceOrchestrator.placeOrder("C005", PRODUCT_QUANTITIES, PAYMENT_METHOD));
    }

    @Test
    public void testPlaceOrder_ProductNotFound() throws Exception {
        // Arrange
        Customer customer = new Customer(CUSTOMER_ID, "Rahul Sharma", "rahul@example.com", "Premium");
        eCommerceOrchestrator.customers.put(CUSTOMER_ID, customer);
        when(inventoryService.getProduct(PRODUCT_ID)).thenReturn(null);
        when(inventoryService.getProduct(PRODUCT_ID_2)).thenReturn(new Product(PRODUCT_ID_2, "Product 2", 20.0));
        when(inventoryService.reserveStock(PRODUCT_ID_2, 3)).thenReturn(true);
        when(orderService.placeOrder(any(), any(), any())).thenReturn(new Order(CUSTOMER_ID, PAYMENT_METHOD));

        // Act and Assert
        assertThrows(Exception.class, () -> eCommerceOrchestrator.placeOrder(CUSTOMER_ID, PRODUCT_QUANTITIES, PAYMENT_METHOD));
    }

    @Test
    public void testPlaceOrder_InsufficientStock() throws Exception {
        // Arrange
        Customer customer = new Customer(CUSTOMER_ID, "Rahul Sharma", "rahul@example.com", "Premium");
        eCommerceOrchestrator.customers.put(CUSTOMER_ID, customer);
        when(inventoryService.getProduct(PRODUCT_ID)).thenReturn(new Product(PRODUCT_ID, "Product 1", 10.0));
        when(inventoryService.getProduct(PRODUCT_ID_2)).thenReturn(new Product(PRODUCT_ID_2, "Product 2", 20.0));
        when(inventoryService.reserveStock(PRODUCT_ID, 2)).thenReturn(true);
        when(inventoryService.reserveStock(PRODUCT_ID_2, 4)).thenReturn(false);
        when(orderService.placeOrder(any(), any(), any())).thenReturn(new Order(CUSTOMER_ID, PAYMENT_METHOD));

        // Act and Assert
        assertThrows(Exception.class, () -> eCommerceOrchestrator.placeOrder(CUSTOMER_ID, PRODUCT_QUANTITIES, PAYMENT_METHOD));
    }

    @Test
    public void testInitializeSampleCustomers() {
        // Act
        eCommerceOrchestrator.initializeSampleCustomers();

        // Assert
        assertEquals(4, eCommerceOrchestrator.customers.size());
    }
}