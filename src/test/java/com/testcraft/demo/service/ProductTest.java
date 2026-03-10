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

@ExtendWith(MockitoExtension.class)
public class ProductTest {

    @Mock
    private Product product;

    @Test
    public void testProductConstructor() {
        // Arrange
        String productId = "P001";
        String name = "Product 1";
        String category = "Electronics";
        double price = 100.50;

        // Act
        Product actualProduct = new Product(productId, name, category, price);

        // Assert
        assertEquals(productId, actualProduct.getProductId());
        assertEquals(name, actualProduct.getName());
        assertEquals(category, actualProduct.getCategory());
        assertEquals(price, actualProduct.getPrice(), 0.01);
    }

    @Test
    public void testProductGetters() {
        // Arrange
        String productId = "P001";
        String name = "Product 1";
        String category = "Electronics";
        double price = 100.50;

        Product actualProduct = new Product(productId, name, category, price);

        // Act & Assert
        assertEquals(productId, actualProduct.getProductId());
        assertEquals(name, actualProduct.getName());
        assertEquals(category, actualProduct.getCategory());
        assertEquals(price, actualProduct.getPrice(), 0.01);
    }

    @Test
    public void testProductToString() {
        // Arrange
        String productId = "P001";
        String name = "Product 1";
        String category = "Electronics";
        double price = 100.50;

        Product actualProduct = new Product(productId, name, category, price);

        // Act & Assert
        String expectedString = String.format("%s - %s (₹%.2f)", productId, name, price);
        assertEquals(expectedString, actualProduct.toString());
    }

    @Test
    public void testProductNullConstructor() {
        // Arrange & Act & Assert
        assertThrows(NullPointerException.class, () -> new Product(null, "Product 1", "Electronics", 100.50));
    }

    @Test
    public void testProductNullNameConstructor() {
        // Arrange & Act & Assert
        assertThrows(NullPointerException.class, () -> new Product("P001", null, "Electronics", 100.50));
    }

    @Test
    public void testProductNullCategoryConstructor() {
        // Arrange & Act & Assert
        assertThrows(NullPointerException.class, () -> new Product("P001", "Product 1", null, 100.50));
    }

    @Test
    public void testProductNegativePriceConstructor() {
        // Arrange & Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Product("P001", "Product 1", "Electronics", -100.50));
    }

    @Test
    public void testProductZeroPriceConstructor() {
        // Arrange & Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Product("P001", "Product 1", "Electronics", 0.0));
    }
}