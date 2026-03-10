package com.testcraft.demo.service;

import com.testcraft.demo.model.Product;
import com.testcraft.demo.model.ProductNotFoundException;
import com.testcraft.demo.model.StockLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
public class InventoryManagementServiceTest {

    @Mock
    private StockObserver stockObserver;

    @InjectMocks
    private InventoryManagementService inventoryManagementService;

    @BeforeEach
    public void setup() {
        inventoryManagementService = new InventoryManagementService();
        inventoryManagementService.observers.add(stockObserver);
    }

    @Test
    public void testAddProduct() {
        // Arrange
        Product product = new Product("P008", "Tablet", "Electronics", 30000.0);

        // Act
        inventoryManagementService.addProduct(product);

        // Assert
        assertEquals(8, inventoryManagementService.products.size());
        assertEquals(product, inventoryManagementService.products.get("P008"));
    }

    @Test
    public void testAddProductExistingProduct() {
        // Arrange
        Product product = new Product("P001", "Laptop", "Electronics", 45000.0);

        // Act and Assert
        assertThrows(ProductNotFoundException.class, () -> inventoryManagementService.addProduct(product));
    }

    @Test
    public void testGetProduct() throws ProductNotFoundException {
        // Arrange
        String productId = "P001";

        // Act
        Product product = inventoryManagementService.getProduct(productId);

        // Assert
        assertEquals(productId, product.getProductId());
        assertEquals("Laptop", product.getName());
        assertEquals("Electronics", product.getCategory());
        assertEquals(45000.0, product.getPrice());
    }

    @Test
    public void testGetProductNotFound() {
        // Act and Assert
        assertThrows(ProductNotFoundException.class, () -> inventoryManagementService.getProduct("P008"));
    }

    @Test
    public void testGetAllProducts() {
        // Act
        List<Product> products = inventoryManagementService.getAllProducts();

        // Assert
        assertEquals(7, products.size());
    }

    @Test
    public void testGetProductsByCategory() {
        // Act
        List<Product> electronicsProducts = inventoryManagementService.getProductsByCategory("Electronics");

        // Assert
        assertEquals(4, electronicsProducts.size());
        assertEquals("P001", electronicsProducts.get(0).getProductId());
        assertEquals("P002", electronicsProducts.get(1).getProductId());
        assertEquals("P003", electronicsProducts.get(2).getProductId());
        assertEquals("P006", electronicsProducts.get(3).getProductId());
    }

    @Test
    public void testUpdateStock() {
        // Arrange
        String productId = "P001";
        int newQuantity = 20;

        // Act
        inventoryManagementService.updateStock(productId, newQuantity);

        // Assert
        assertEquals(newQuantity, inventoryManagementService.stockLevels.get(productId).getQuantity());
    }

    @Test
    public void testUpdateStockLowThreshold() {
        // Arrange
        String productId = "P001";
        int newQuantity = 9;

        // Act
        inventoryManagementService.updateStock(productId, newQuantity);

        // Assert
        assertEquals(newQuantity, inventoryManagementService.stockLevels.get(productId).getQuantity());
        verify(stockObserver, times(1)).notifyLowStock(productId);
    }

    @Test
    public void testUpdateStockReorderQuantity() {
        // Arrange
        String productId = "P001";
        int newQuantity = 5;

        // Act
        inventoryManagementService.updateStock(productId, newQuantity);

        // Assert
        assertEquals(newQuantity, inventoryManagementService.stockLevels.get(productId).getQuantity());
        verify(stockObserver, times(1)).notifyReorderQuantity(productId);
    }
}