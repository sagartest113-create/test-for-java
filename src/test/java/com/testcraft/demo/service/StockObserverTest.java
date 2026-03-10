package com.testcraft.demo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StockObserverTest {

    @Mock
    private StockObserver stockObserver;

    @Test
    public void testOnLowStockSuccess() {
        // Given
        String productId = "product-123";
        int quantity = 10;

        // When
        stockObserver.onLowStock(productId, quantity);

        // Then
        assertDoesNotThrow(() -> stockObserver.onLowStock(productId, quantity));
        verify(stockObserver).onLowStock(productId, quantity);
    }

    @Test
    public void testOnLowStockFailure() {
        // Given
        String productId = null;
        int quantity = 10;

        // When & Then
        assertThrows(NullPointerException.class, () -> stockObserver.onLowStock(productId, quantity));
        verify(stockObserver, never()).onLowStock(productId, quantity);
    }

    @Test
    public void testOnLowStockEdgeCase() {
        // Given
        String productId = "";
        int quantity = 0;

        // When & Then
        assertDoesNotThrow(() -> stockObserver.onLowStock(productId, quantity));
        verify(stockObserver).onLowStock(productId, quantity);
    }
}