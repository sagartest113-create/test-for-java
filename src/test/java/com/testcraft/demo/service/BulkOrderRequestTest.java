package com.testcraft.demo.service;

import com.testcraft.demo.model.PaymentMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BulkOrderRequestTest {

    @Mock
    private PaymentMethod paymentMethod;

    @Test
    public void testBulkOrderRequestConstructor() {
        // Arrange
        String customerId = "test_customer_id";
        Map<String, Integer> productQuantities = new HashMap<>();
        productQuantities.put("product1", 10);
        productQuantities.put("product2", 20);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        // Act
        BulkOrderRequest bulkOrderRequest = new BulkOrderRequest(customerId, productQuantities, paymentMethod);

        // Assert
        assertEquals(customerId, bulkOrderRequest.getCustomerId());
        assertEquals(productQuantities, bulkOrderRequest.getProductQuantities());
        assertEquals(paymentMethod, bulkOrderRequest.getPaymentMethod());
    }

    @Test
    public void testBulkOrderRequestConstructorNullCustomerId() {
        // Arrange
        String customerId = null;
        Map<String, Integer> productQuantities = new HashMap<>();
        productQuantities.put("product1", 10);
        productQuantities.put("product2", 20);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        // Act and Assert
        assertThrows(NullPointerException.class, () -> new BulkOrderRequest(customerId, productQuantities, paymentMethod));
    }

    @Test
    public void testBulkOrderRequestConstructorNullProductQuantities() {
        // Arrange
        String customerId = "test_customer_id";
        Map<String, Integer> productQuantities = null;
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        // Act and Assert
        assertThrows(NullPointerException.class, () -> new BulkOrderRequest(customerId, productQuantities, paymentMethod));
    }

    @Test
    public void testBulkOrderRequestConstructorNullPaymentMethod() {
        // Arrange
        String customerId = "test_customer_id";
        Map<String, Integer> productQuantities = new HashMap<>();
        productQuantities.put("product1", 10);
        productQuantities.put("product2", 20);
        PaymentMethod paymentMethod = null;

        // Act and Assert
        assertThrows(NullPointerException.class, () -> new BulkOrderRequest(customerId, productQuantities, paymentMethod));
    }

    @Test
    public void testGetCustomerId() {
        // Arrange
        String customerId = "test_customer_id";
        Map<String, Integer> productQuantities = new HashMap<>();
        productQuantities.put("product1", 10);
        productQuantities.put("product2", 20);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        BulkOrderRequest bulkOrderRequest = new BulkOrderRequest(customerId, productQuantities, paymentMethod);

        // Act and Assert
        assertEquals(customerId, bulkOrderRequest.getCustomerId());
    }

    @Test
    public void testGetProductQuantities() {
        // Arrange
        String customerId = "test_customer_id";
        Map<String, Integer> productQuantities = new HashMap<>();
        productQuantities.put("product1", 10);
        productQuantities.put("product2", 20);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        BulkOrderRequest bulkOrderRequest = new BulkOrderRequest(customerId, productQuantities, paymentMethod);

        // Act and Assert
        assertEquals(productQuantities, bulkOrderRequest.getProductQuantities());
    }

    @Test
    public void testGetPaymentMethod() {
        // Arrange
        String customerId = "test_customer_id";
        Map<String, Integer> productQuantities = new HashMap<>();
        productQuantities.put("product1", 10);
        productQuantities.put("product2", 20);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        BulkOrderRequest bulkOrderRequest = new BulkOrderRequest(customerId, productQuantities, paymentMethod);

        // Act and Assert
        assertEquals(paymentMethod, bulkOrderRequest.getPaymentMethod());
    }
}