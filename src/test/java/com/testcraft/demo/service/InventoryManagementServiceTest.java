package com.testcraft.demo.service;

import com.testcraft.demo.model.Product;
import com.testcraft.demo.model.ProductNotFoundException;
import com.testcraft.demo.model.StockLevel;
import com.testcraft.demo.model.StockObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryManagementServiceTest {

    private InventoryManagementService inventoryManagementService;

    @Mock
    private StockObserver stockObserver;

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
        assertEquals(1, inventoryManagementService.products.size());
        assertEquals(product, inventoryManagementService.products.get("P008"));
        assertEquals(1, inventoryManagementService.stockLevels.size());
        assertEquals(0, inventoryManagementService.stockLevels.get("P008").getQuantity());
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
        // Arrange
        String productId = "P999";

        // Act and Assert
        assertThrows(ProductNotFoundException.class, () -> inventoryManagementService.getProduct(productId));
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
        verify(stockObserver, times(1)).notifyReorder(productId);
    }

    @Test
    public void testUpdateStockMultipleObservers() {
        // Arrange
        StockObserver observer2 = mock(StockObserver.class);
        inventoryManagementService.observers.add(observer2);

        // Act
        inventoryManagementService.updateStock("P001", 20);

        // Assert
        verify(stockObserver, times(1)).notifyLowStock("P001");
        verify(observer2, times(1)).notifyLowStock("P001");
    }
}