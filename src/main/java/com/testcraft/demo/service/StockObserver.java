package com.testcraft.demo.service;

public interface StockObserver {
    void onLowStock(String productId, int quantity);
}
