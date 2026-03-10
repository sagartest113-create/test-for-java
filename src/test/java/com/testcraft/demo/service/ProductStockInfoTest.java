package com.testcraft.demo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.testcraft.demo.model.Product;

@ExtendWith(MockitoExtension.class)
public class ProductStockInfoTest {

    @Mock
    private Product product;

    @InjectMocks
    private ProductStockInfo productStockInfo;

    @Test
    public void testProductStockInfoConstructor() {
        // Arrange
        int totalQuantity = 100;
        int reservedQuantity = 20;
        int availableQuantity = 80;

        // Act
        productStockInfo = new ProductStockInfo(product, totalQuantity, reservedQuantity, availableQuantity);

        // Assert
        assertEquals(product, productStockInfo.getProduct());
        assertEquals(totalQuantity, productStockInfo.getTotalQuantity());
        assertEquals(reservedQuantity, productStockInfo.getReservedQuantity());
        assertEquals(availableQuantity, productStockInfo.getAvailableQuantity());
    }

    @Test
    public void testProductStockInfoConstructorNullProduct() {
        // Arrange
        int totalQuantity = 100;
        int reservedQuantity = 20;
        int availableQuantity = 80;

        // Act and Assert
        assertThrows(NullPointerException.class, () -> new ProductStockInfo(null, totalQuantity, reservedQuantity, availableQuantity));
    }

    @Test
    public void testProductStockInfoConstructorNegativeTotalQuantity() {
        // Arrange
        int totalQuantity = -100;
        int reservedQuantity = 20;
        int availableQuantity = 80;

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> new ProductStockInfo(product, totalQuantity, reservedQuantity, availableQuantity));
    }

    @Test
    public void testProductStockInfoConstructorNegativeReservedQuantity() {
        // Arrange
        int totalQuantity = 100;
        int reservedQuantity = -20;
        int availableQuantity = 80;

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> new ProductStockInfo(product, totalQuantity, reservedQuantity, availableQuantity));
    }

    @Test
    public void testProductStockInfoConstructorNegativeAvailableQuantity() {
        // Arrange
        int totalQuantity = 100;
        int reservedQuantity = 20;
        int availableQuantity = -80;

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> new ProductStockInfo(product, totalQuantity, reservedQuantity, availableQuantity));
    }

    @Test
    public void testProductStockInfoGetProduct() {
        // Arrange
        int totalQuantity = 100;
        int reservedQuantity = 20;
        int availableQuantity = 80;

        // Act
        productStockInfo = new ProductStockInfo(product, totalQuantity, reservedQuantity, availableQuantity);

        // Assert
        assertEquals(product, productStockInfo.getProduct());
    }

    @Test
    public void testProductStockInfoGetTotalQuantity() {
        // Arrange
        int totalQuantity = 100;
        int reservedQuantity = 20;
        int availableQuantity = 80;

        // Act
        productStockInfo = new ProductStockInfo(product, totalQuantity, reservedQuantity, availableQuantity);

        // Assert
        assertEquals(totalQuantity, productStockInfo.getTotalQuantity());
    }

    @Test
    public void testProductStockInfoGetReservedQuantity() {
        // Arrange
        int totalQuantity = 100;
        int reservedQuantity = 20;
        int availableQuantity = 80;

        // Act
        productStockInfo = new ProductStockInfo(product, totalQuantity, reservedQuantity, availableQuantity);

        // Assert
        assertEquals(reservedQuantity, productStockInfo.getReservedQuantity());
    }

    @Test
    public void testProductStockInfoGetAvailableQuantity() {
        // Arrange
        int totalQuantity = 100;
        int reservedQuantity = 20;
        int availableQuantity = 80;

        // Act
        productStockInfo = new ProductStockInfo(product, totalQuantity, reservedQuantity, availableQuantity);

        // Assert
        assertEquals(availableQuantity, productStockInfo.getAvailableQuantity());
    }

    @Test
    public void testProductStockInfoToString() {
        // Arrange
        int totalQuantity = 100;
        int reservedQuantity = 20;
        int availableQuantity = 80;

        // Act
        productStockInfo = new ProductStockInfo(product, totalQuantity, reservedQuantity, availableQuantity);

        // Assert
        String expectedString = String.format("%s | Total: %d | Reserved: %d | Available: %d",
                product, totalQuantity, reservedQuantity, availableQuantity);
        assertEquals(expectedString, productStockInfo.toString());
    }
}