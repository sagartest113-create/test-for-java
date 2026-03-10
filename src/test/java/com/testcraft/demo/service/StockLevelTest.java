package com.testcraft.demo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StockLevelTest {

    @Mock
    private StockLevel stockLevel;

    @Test
    public void testStockLevelInitialization() {
        // Arrange
        String productId = "TEST_PRODUCT";
        int quantity = 100;

        // Act
        StockLevel stockLevel = new StockLevel(productId, quantity);

        // Assert
        assertEquals(productId, stockLevel.getProductId());
        assertEquals(quantity, stockLevel.getQuantity());
        assertEquals(0, stockLevel.getReservedQuantity());
    }

    @Test
    public void testReserveSuccess() {
        // Arrange
        String productId = "TEST_PRODUCT";
        int quantity = 100;
        int amount = 50;

        // Act
        StockLevel stockLevel = new StockLevel(productId, quantity);
        stockLevel.reserve(amount);

        // Assert
        assertEquals(quantity, stockLevel.getQuantity());
        assertEquals(amount, stockLevel.getReservedQuantity());
        assertEquals(quantity - amount, stockLevel.getAvailableQuantity());
    }

    @Test
    public void testReserveFailure() {
        // Arrange
        String productId = "TEST_PRODUCT";
        int quantity = 100;
        int amount = 150;

        // Act and Assert
        assertThrows(IllegalStateException.class, () -> {
            StockLevel stockLevel = new StockLevel(productId, quantity);
            stockLevel.reserve(amount);
        });
    }

    @Test
    public void testReleaseSuccess() {
        // Arrange
        String productId = "TEST_PRODUCT";
        int quantity = 100;
        int reservedQuantity = 50;

        // Act
        StockLevel stockLevel = new StockLevel(productId, quantity);
        stockLevel.reserve(reservedQuantity);
        stockLevel.release(reservedQuantity);

        // Assert
        assertEquals(quantity, stockLevel.getQuantity());
        assertEquals(0, stockLevel.getReservedQuantity());
        assertEquals(quantity, stockLevel.getAvailableQuantity());
    }

    @Test
    public void testReleaseFailure() {
        // Arrange
        String productId = "TEST_PRODUCT";
        int quantity = 100;
        int reservedQuantity = 50;
        int amount = 75;

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> {
            StockLevel stockLevel = new StockLevel(productId, quantity);
            stockLevel.reserve(reservedQuantity);
            stockLevel.release(amount);
        });
    }

    @Test
    public void testConfirmSuccess() {
        // Arrange
        String productId = "TEST_PRODUCT";
        int quantity = 100;
        int reservedQuantity = 50;
        int amount = 25;

        // Act
        StockLevel stockLevel = new StockLevel(productId, quantity);
        stockLevel.reserve(reservedQuantity);
        stockLevel.confirm(amount);

        // Assert
        assertEquals(quantity - amount, stockLevel.getQuantity());
        assertEquals(reservedQuantity - amount, stockLevel.getReservedQuantity());
        assertEquals(quantity - reservedQuantity, stockLevel.getAvailableQuantity());
    }

    @Test
    public void testConfirmFailure() {
        // Arrange
        String productId = "TEST_PRODUCT";
        int quantity = 100;
        int reservedQuantity = 50;
        int amount = 75;

        // Act and Assert
        assertThrows(IllegalStateException.class, () -> {
            StockLevel stockLevel = new StockLevel(productId, quantity);
            stockLevel.reserve(reservedQuantity);
            stockLevel.confirm(amount);
        });
    }

    @Test
    public void testSetQuantitySuccess() {
        // Arrange
        String productId = "TEST_PRODUCT";
        int quantity = 100;

        // Act
        StockLevel stockLevel = new StockLevel(productId, quantity);
        stockLevel.setQuantity(150);

        // Assert
        assertEquals(150, stockLevel.getQuantity());
    }

    @Test
    public void testSetQuantityFailure() {
        // Arrange
        String productId = "TEST_PRODUCT";
        int quantity = 100;

        // Act and Assert
        assertThrows(UnsupportedOperationException.class, () -> {
            StockLevel stockLevel = new StockLevel(productId, quantity);
            stockLevel.setQuantity(0);
        });
    }
}