package com.testcraft.demo.service;

public class ProductStockInfo {
    private final Product product;
    private final int totalQuantity;
    private final int reservedQuantity;
    private final int availableQuantity;
    
    public ProductStockInfo(Product product, int totalQuantity, int reservedQuantity, int availableQuantity) {
        this.product = product;
        this.totalQuantity = totalQuantity;
        this.reservedQuantity = reservedQuantity;
        this.availableQuantity = availableQuantity;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public int getTotalQuantity() {
        return totalQuantity;
    }
    
    public int getReservedQuantity() {
        return reservedQuantity;
    }
    
    public int getAvailableQuantity() {
        return availableQuantity;
    }
    
    @Override
    public String toString() {
        return String.format("%s | Total: %d | Reserved: %d | Available: %d",
                product, totalQuantity, reservedQuantity, availableQuantity);
    }
}
