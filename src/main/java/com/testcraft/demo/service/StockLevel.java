package com.testcraft.demo.service;

public class StockLevel {
    private final String productId;
    private int quantity;
    private int reservedQuantity;
    
    public StockLevel(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.reservedQuantity = 0;
    }
    
    public synchronized void reserve(int amount) {
        if (amount > getAvailableQuantity()) {
            throw new IllegalStateException("Cannot reserve more than available quantity");
        }
        reservedQuantity += amount;
    }
    
    public synchronized void release(int amount) {
        reservedQuantity = Math.max(0, reservedQuantity - amount);
    }
    
    public synchronized void confirm(int amount) {
        if (amount > reservedQuantity) {
            throw new IllegalStateException("Cannot confirm more than reserved quantity");
        }
        quantity -= amount;
        reservedQuantity -= amount;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public synchronized void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public int getReservedQuantity() {
        return reservedQuantity;
    }
    
    public int getAvailableQuantity() {
        return quantity - reservedQuantity;
    }
}
