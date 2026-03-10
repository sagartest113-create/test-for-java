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
        Product product = new Product(productId, name, category, price);

        // Assert
        assertEquals(productId, product.getProductId());
        assertEquals(name, product.getName());
        assertEquals(category, product.getCategory());
        assertEquals(price, product.getPrice(), 0.01);
    }

    @Test
    public void testProductGetters() {
        // Arrange
        String productId = "P001";
        String name = "Product 1";
        String category = "Electronics";
        double price = 100.50;

        // Act
        Product product = new Product(productId, name, category, price);

        // Assert
        assertEquals(productId, product.getProductId());
        assertEquals(name, product.getName());
        assertEquals(category, product.getCategory());
        assertEquals(price, product.getPrice(), 0.01);
    }

    @Test
    public void testProductToString() {
        // Arrange
        String productId = "P001";
        String name = "Product 1";
        String category = "Electronics";
        double price = 100.50;

        // Act
        Product product = new Product(productId, name, category, price);

        // Assert
        assertEquals(String.format("%s - %s (₹%.2f)", productId, name, price), product.toString());
    }

    @Test
    public void testProductNullConstructor() {
        // Act and Assert
        assertThrows(NullPointerException.class, () -> new Product(null, "Product 1", "Electronics", 100.50));
    }

    @Test
    public void testProductNullNameConstructor() {
        // Act and Assert
        assertThrows(NullPointerException.class, () -> new Product("P001", null, "Electronics", 100.50));
    }

    @Test
    public void testProductNullCategoryConstructor() {
        // Act and Assert
        assertThrows(NullPointerException.class, () -> new Product("P001", "Product 1", null, 100.50));
    }

    @Test
    public void testProductNegativePriceConstructor() {
        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> new Product("P001", "Product 1", "Electronics", -100.50));
    }

    @Test
    public void testProductZeroPriceConstructor() {
        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> new Product("P001", "Product 1", "Electronics", 0));
    }
}