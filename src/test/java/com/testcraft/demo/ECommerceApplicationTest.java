package com.testcraft.demo.test;

import com.testcraft.demo.ECommerceApplication;
import com.testcraft.demo.model.*;
import com.testcraft.demo.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
public class ECommerceApplicationTest {

    @Mock
    private ECommerceOrchestrator orchestrator;

    @Mock
    private OrderService orderService;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private ECommerceApplication application;

    @BeforeEach
    public void setup() {
        when(orchestrator.placeOrder(anyString(), any(Map.class), any(PaymentMethod.class)))
                .thenReturn(new Order("C001", new HashMap<>(), PaymentMethod.UPI));
        when(orchestrator.placeOrder(anyString(), any(Map.class), any(PaymentMethod.class)))
                .thenReturn(new Order("C002", new HashMap<>(), PaymentMethod.CARD));
        when(orchestrator.placeOrder(anyString(), any(Map.class), any(PaymentMethod.class)))
                .thenReturn(new Order("C004", new HashMap<>(), PaymentMethod.COD));
    }

    @Test
    public void testDemonstrateCompleteWorkflow() {
        // Test scenario 1: Simple Order Placement
        Map<String, Integer> cart1 = new HashMap<>();
        cart1.put("P003", 2);
        cart1.put("P005", 1);
        Order order1 = application.demonstrateCompleteWorkflow(orchestrator);
        assertNotNull(order1);
        assertEquals("C001", order1.getCustomerId());
        assertEquals(PaymentMethod.UPI, order1.getPaymentMethod());

        // Test scenario 2: Bulk Order with Discount
        Map<String, Integer> cart2 = new HashMap<>();
        cart2.put("P001", 3);
        cart2.put("P002", 5);
        cart2.put("P006", 2);
        Order order2 = application.demonstrateCompleteWorkflow(orchestrator);
        assertNotNull(order2);
        assertEquals("C002", order2.getCustomerId());
        assertEquals(PaymentMethod.CARD, order2.getPaymentMethod());

        // Test scenario 3: COD Order for Regular Customer
        Map<String, Integer> cart3 = new HashMap<>();
        cart3.put("P004", 1);
        cart3.put("P007", 5);
        Order order3 = application.demonstrateCompleteWorkflow(orchestrator);
        assertNotNull(order3);
        assertEquals("C004", order3.getCustomerId());
        assertEquals(PaymentMethod.COD, order3.getPaymentMethod());
    }

    @Test
    public void testPlaceOrderSuccess() {
        // Test simple order placement
        Map<String, Integer> cart = new HashMap<>();
        cart.put("P003", 2);
        cart.put("P005", 1);
        Order order = application.demonstrateCompleteWorkflow(orchestrator);
        assertNotNull(order);
        assertEquals("C001", order.getCustomerId());
        assertEquals(PaymentMethod.UPI, order.getPaymentMethod());

        // Test bulk order with discount
        cart = new HashMap<>();
        cart.put("P001", 3);
        cart.put("P002", 5);
        cart.put("P006", 2);
        order = application.demonstrateCompleteWorkflow(orchestrator);
        assertNotNull(order);
        assertEquals("C002", order.getCustomerId());
        assertEquals(PaymentMethod.CARD, order.getPaymentMethod());

        // Test COD order for regular customer
        cart = new HashMap<>();
        cart.put("P004", 1);
        cart.put("P007", 5);
        order = application.demonstrateCompleteWorkflow(orchestrator);
        assertNotNull(order);
        assertEquals("C004", order.getCustomerId());
        assertEquals(PaymentMethod.COD, order.getPaymentMethod());
    }

    @Test
    public void testPlaceOrderFailure() {
        // Test null customer ID
        Map<String, Integer> cart = new HashMap<>();
        cart.put("P003", 2);
        cart.put("P005", 1);
        assertThrows(NullPointerException.class, () -> application.demonstrateCompleteWorkflow(orchestrator));

        // Test null cart
        cart = null;
        assertThrows(NullPointerException.class, () -> application.demonstrateCompleteWorkflow(orchestrator));

        // Test null payment method
        cart = new HashMap<>();
        cart.put("P003", 2);
        cart.put("P005", 1);
        assertThrows(NullPointerException.class, () -> application.demonstrateCompleteWorkflow(orchestrator));
    }

    @Test
    public void testShutdown() {
        // Test shutdown of services
        application.shutdown();
        verify(orchestrator).shutdown();
    }
}