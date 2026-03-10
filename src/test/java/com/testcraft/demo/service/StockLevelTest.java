package com.testcraft.demo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StockLevelTest {

    @Mock
    private StockLevel stockLevel;

    @Test
    public void testStockLevelInitialization() {
        // Arrange
        String productId = "TEST_PRODUCT";
        int quantity = 10;

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
        int quantity = 10;
        int amount = 5;

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
        int quantity = 10;
        int amount = 15;

        // Act and Assert
        StockLevel stockLevel = new StockLevel(productId, quantity);
        assertThrows(IllegalStateException.class, () -> stockLevel.reserve(amount));
    }

    @Test
    public void testReleaseSuccess() {
        // Arrange
        String productId = "TEST_PRODUCT";
        int quantity = 10;
        int reservedQuantity = 5;
        int amount = 3;

        // Act
        StockLevel stockLevel = new StockLevel(productId, quantity);
        stockLevel.reserve(reservedQuantity);
        stockLevel.release(amount);

        // Assert
        assertEquals(quantity, stockLevel.getQuantity());
        assertEquals(reservedQuantity - amount, stockLevel.getReservedQuantity());
        assertEquals(quantity - reservedQuantity + amount, stockLevel.getAvailableQuantity());
    }

    @Test
    public void testReleaseFailure() {
        // Arrange
        String productId = "TEST_PRODUCT";
        int quantity = 10;
        int reservedQuantity = 5;
        int amount = 10;

        // Act and Assert
        StockLevel stockLevel = new StockLevel(productId, quantity);
        stockLevel.reserve(reservedQuantity);
        assertThrows(IllegalStateException.class, () -> stockLevel.release(amount));
    }

    @Test
    public void testConfirmSuccess() {
        // Arrange
        String productId = "TEST_PRODUCT";
        int quantity = 10;
        int reservedQuantity = 5;
        int amount = 3;

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
        int quantity = 10;
        int reservedQuantity = 5;
        int amount = 10;

        // Act and Assert
        StockLevel stockLevel = new StockLevel(productId, quantity);
        stockLevel.reserve(reservedQuantity);
        assertThrows(IllegalStateException.class, () -> stockLevel.confirm(amount));
    }

    @Test
    public void testGetAvailableQuantitySuccess() {
        // Arrange
        String productId = "TEST_PRODUCT";
        int quantity = 10;
        int reservedQuantity = 5;

        // Act
        StockLevel stockLevel = new StockLevel(productId, quantity);

        // Assert
        assertEquals(quantity - reservedQuantity, stockLevel.getAvailableQuantity());
    }

    @Test
    public void testGetAvailableQuantityFailure() {
        // Arrange
        String productId = "TEST_PRODUCT";
        int quantity = 10;
        int reservedQuantity = 15;

        // Act and Assert
        StockLevel stockLevel = new StockLevel(productId, quantity);
        assertThrows(IllegalStateException.class, stockLevel::reserve);
    }
}