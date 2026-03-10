package com.testcraft.demo.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InventoryManagementService {
    private final ConcurrentHashMap<String, Product> products;
    private final ConcurrentHashMap<String, StockLevel> stockLevels;
    private final List<StockObserver> observers;
    
    private static final int LOW_STOCK_THRESHOLD = 10;
    private static final int REORDER_QUANTITY = 50;
    
    public InventoryManagementService() {
        this.products = new ConcurrentHashMap<>();
        this.stockLevels = new ConcurrentHashMap<>();
        this.observers = new ArrayList<>();
        
        initializeSampleProducts();
    }
    
    private void initializeSampleProducts() {
        addProduct(new Product("P001", "Laptop", "Electronics", 45000.0));
        addProduct(new Product("P002", "Smartphone", "Electronics", 25000.0));
        addProduct(new Product("P003", "Headphones", "Electronics", 2500.0));
        addProduct(new Product("P004", "Keyboard", "Accessories", 1500.0));
        addProduct(new Product("P005", "Mouse", "Accessories", 800.0));
        addProduct(new Product("P006", "Monitor", "Electronics", 15000.0));
        addProduct(new Product("P007", "USB Cable", "Accessories", 200.0));
        
        updateStock("P001", 25);
        updateStock("P002", 50);
        updateStock("P003", 100);
        updateStock("P004", 75);
        updateStock("P005", 120);
        updateStock("P006", 30);
        updateStock("P007", 200);
    }
    
    public void addProduct(Product product) {
        products.put(product.getProductId(), product);
        stockLevels.putIfAbsent(product.getProductId(), new StockLevel(product.getProductId(), 0));
    }
    
    public Product getProduct(String productId) throws ProductNotFoundException {
        Product product = products.get(productId);
        if (product == null) {
            throw new ProductNotFoundException("Product not found: " + productId);
        }
        return product;
    }
    
    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }
    
    public List<Product> getProductsByCategory(String category) {
        return products.values().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }
    
    public synchronized void updateStock(String productId, int quantity) {
        StockLevel stockLevel = stockLevels.computeIfAbsent(
                productId,
                k -> new StockLevel(productId, 0)
        );
        
        int oldQuantity = stockLevel.getQuantity();
        stockLevel.setQuantity(quantity);
        
        System.out.println(String.format("Stock updated for %s: %d -> %d", 
                productId, oldQuantity, quantity));
        
        if (quantity < LOW_STOCK_THRESHOLD) {
            notifyLowStock(productId, quantity);
        }
    }
    
    public synchronized boolean reserveStock(String productId, int quantity) 
            throws InsufficientStockException, ProductNotFoundException {
        
        if (!products.containsKey(productId)) {
            throw new ProductNotFoundException("Product not found: " + productId);
        }
        
        StockLevel stockLevel = stockLevels.get(productId);
        if (stockLevel == null || stockLevel.getAvailableQuantity() < quantity) {
            throw new InsufficientStockException(
                    String.format("Insufficient stock for product %s. Available: %d, Requested: %d",
                            productId,
                            stockLevel != null ? stockLevel.getAvailableQuantity() : 0,
                            quantity)
            );
        }
        
        stockLevel.reserve(quantity);
        System.out.println(String.format("Reserved %d units of %s", quantity, productId));
        
        if (stockLevel.getAvailableQuantity() < LOW_STOCK_THRESHOLD) {
            autoReorder(productId);
        }
        
        return true;
    }
    
    public synchronized void releaseStock(String productId, int quantity) {
        StockLevel stockLevel = stockLevels.get(productId);
        if (stockLevel != null) {
            stockLevel.release(quantity);
            System.out.println(String.format("Released %d units of %s", quantity, productId));
        }
    }
    
    public synchronized void confirmReservation(String productId, int quantity) {
        StockLevel stockLevel = stockLevels.get(productId);
        if (stockLevel != null) {
            stockLevel.confirm(quantity);
            System.out.println(String.format("Confirmed %d units of %s", quantity, productId));
        }
    }
    
    public int getAvailableStock(String productId) {
        StockLevel stockLevel = stockLevels.get(productId);
        return stockLevel != null ? stockLevel.getAvailableQuantity() : 0;
    }
    
    public Map<String, Integer> getLowStockProducts() {
        return stockLevels.entrySet().stream()
                .filter(entry -> entry.getValue().getAvailableQuantity() < LOW_STOCK_THRESHOLD)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getAvailableQuantity()
                ));
    }
    
    private void autoReorder(String productId) {
        System.out.println(String.format(
                "AUTO REORDER: Product %s has low stock. Ordering %d units",
                productId, REORDER_QUANTITY
        ));
        
        StockLevel stockLevel = stockLevels.get(productId);
        if (stockLevel != null) {
            int newQuantity = stockLevel.getQuantity() + REORDER_QUANTITY;
            stockLevel.setQuantity(newQuantity);
        }
    }
    
    public void addStockObserver(StockObserver observer) {
        observers.add(observer);
    }
    
    private void notifyLowStock(String productId, int quantity) {
        for (StockObserver observer : observers) {
            observer.onLowStock(productId, quantity);
        }
    }
    
    public Map<String, ProductStockInfo> getInventoryReport() {
        Map<String, ProductStockInfo> report = new HashMap<>();
        
        for (Map.Entry<String, Product> entry : products.entrySet()) {
            String productId = entry.getKey();
            Product product = entry.getValue();
            StockLevel stockLevel = stockLevels.get(productId);
            
            if (stockLevel != null) {
                report.put(productId, new ProductStockInfo(
                        product,
                        stockLevel.getQuantity(),
                        stockLevel.getReservedQuantity(),
                        stockLevel.getAvailableQuantity()
                ));
            }
        }
        
        return report;
    }
    
    public double calculateInventoryValue() {
        return products.entrySet().stream()
                .mapToDouble(entry -> {
                    String productId = entry.getKey();
                    Product product = entry.getValue();
                    StockLevel stockLevel = stockLevels.get(productId);
                    int quantity = stockLevel != null ? stockLevel.getQuantity() : 0;
                    return product.getPrice() * quantity;
                })
                .sum();
    }
}
