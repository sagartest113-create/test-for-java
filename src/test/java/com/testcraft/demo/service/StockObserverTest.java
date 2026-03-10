package com.testcraft.demo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StockObserverTest {

    @Mock
    private StockObserver stockObserver;

    @Test
    public void testOnLowStock() {
        // Arrange
        String productId = "product-123";
        int quantity = 10;

        // Act
        stockObserver.onLowStock(productId, quantity);

        // Assert
        verify(stockObserver).onLowStock(productId, quantity);
    }

    @Test
    public void testOnLowStock_NullProductId() {
        // Arrange
        String productId = null;
        int quantity = 10;

        // Act and Assert
        assertThrows(NullPointerException.class, () -> stockObserver.onLowStock(productId, quantity));
    }

    @Test
    public void testOnLowStock_ZeroQuantity() {
        // Arrange
        String productId = "product-123";
        int quantity = 0;

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> stockObserver.onLowStock(productId, quantity));
    }

    @Test
    public void testOnLowStock_NegativeQuantity() {
        // Arrange
        String productId = "product-123";
        int quantity = -10;

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> stockObserver.onLowStock(productId, quantity));
    }

    @Test
    public void testOnLowStock_MultipleCalls() {
        // Arrange
        String productId = "product-123";
        int quantity = 10;

        // Act
        stockObserver.onLowStock(productId, quantity);
        stockObserver.onLowStock(productId, quantity);

        // Assert
        verify(stockObserver, times(2)).onLowStock(productId, quantity);
    }

    @Test
    public void testOnLowStock_NoCalls() {
        // Arrange
        String productId = "product-123";
        int quantity = 10;

        // Act
        stockObserver.onLowStock(productId, quantity);

        // Assert
        verify(stockObserver, never()).onLowStock(any(String.class), any(int.class));
    }
}