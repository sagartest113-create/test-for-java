package com.testcraft.demo.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Order {
    private final String orderId;
    private final String customerId;
    private final List<OrderItem> items;
    private OrderStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private PaymentMethod paymentMethod;
    private double discountPercentage;
    private final ReentrantReadWriteLock lock;
    
    private Order(Builder builder) {
        this.orderId = UUID.randomUUID().toString();
        this.customerId = builder.customerId;
        this.items = new ArrayList<>(builder.items);
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.paymentMethod = builder.paymentMethod;
        this.discountPercentage = builder.discountPercentage;
        this.lock = new ReentrantReadWriteLock();
    }
    
    public double calculateSubtotal() {
        lock.readLock().lock();
        try {
            return items.stream()
                    .mapToDouble(OrderItem::getTotalPrice)
                    .sum();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public double calculateDiscount() {
        return calculateSubtotal() * (discountPercentage / 100.0);
    }
    
    public double calculateTax() {
        double subtotal = calculateSubtotal();
        double discount = calculateDiscount();
        return (subtotal - discount) * 0.18;
    }
    
    public double calculateTotal() {
        lock.readLock().lock();
        try {
            double subtotal = calculateSubtotal();
            double discount = calculateDiscount();
            double tax = calculateTax();
            return subtotal - discount + tax;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public synchronized void updateStatus(OrderStatus newStatus) throws InvalidOrderStateException {
        lock.writeLock().lock();
        try {
            if (!isValidStatusTransition(this.status, newStatus)) {
                throw new InvalidOrderStateException(
                    String.format("Cannot transition from %s to %s", this.status, newStatus)
                );
            }
            this.status = newStatus;
            this.updatedAt = LocalDateTime.now();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    private boolean isValidStatusTransition(OrderStatus current, OrderStatus next) {
        return switch (current) {
            case PENDING -> next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED;
            case CONFIRMED -> next == OrderStatus.PROCESSING || next == OrderStatus.CANCELLED;
            case PROCESSING -> next == OrderStatus.SHIPPED || next == OrderStatus.CANCELLED;
            case SHIPPED -> next == OrderStatus.DELIVERED || next == OrderStatus.RETURNED;
            case DELIVERED -> next == OrderStatus.RETURNED;
            case CANCELLED, RETURNED, REFUNDED -> false;
        };
    }
    
    public void addItem(OrderItem item) throws InvalidOrderStateException {
        lock.writeLock().lock();
        try {
            if (status != OrderStatus.PENDING) {
                throw new InvalidOrderStateException("Cannot add items to non-pending order");
            }
            items.add(item);
            updatedAt = LocalDateTime.now();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public boolean removeItem(String productId) throws InvalidOrderStateException {
        lock.writeLock().lock();
        try {
            if (status != OrderStatus.PENDING) {
                throw new InvalidOrderStateException("Cannot remove items from non-pending order");
            }
            boolean removed = items.removeIf(item -> item.getProductId().equals(productId));
            if (removed) {
                updatedAt = LocalDateTime.now();
            }
            return removed;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public void applyBulkDiscount() {
        lock.writeLock().lock();
        try {
            int totalItems = items.stream()
                    .mapToInt(OrderItem::getQuantity)
                    .sum();
            
            if (totalItems >= 10) {
                discountPercentage = Math.max(discountPercentage, 15.0);
            } else if (totalItems >= 5) {
                discountPercentage = Math.max(discountPercentage, 10.0);
            }
            updatedAt = LocalDateTime.now();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public String getOrderSummary() {
        lock.readLock().lock();
        try {
            StringBuilder summary = new StringBuilder();
            summary.append("Order ID: ").append(orderId).append("\n");
            summary.append("Customer ID: ").append(customerId).append("\n");
            summary.append("Status: ").append(status).append("\n");
            summary.append("Payment Method: ").append(paymentMethod).append("\n");
            summary.append("Items:\n");
            
            for (OrderItem item : items) {
                summary.append(String.format("  - %s x%d @ ₹%.2f = ₹%.2f\n",
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice()));
            }
            
            summary.append(String.format("\nSubtotal: ₹%.2f\n", calculateSubtotal()));
            if (discountPercentage > 0) {
                summary.append(String.format("Discount (%.1f%%): -₹%.2f\n", 
                        discountPercentage, calculateDiscount()));
            }
            summary.append(String.format("Tax (18%%): ₹%.2f\n", calculateTax()));
            summary.append(String.format("Total: ₹%.2f\n", calculateTotal()));
            
            return summary.toString();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public List<OrderItem> getItems() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(items);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public OrderStatus getStatus() {
        lock.readLock().lock();
        try {
            return status;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        lock.readLock().lock();
        try {
            return updatedAt;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public double getDiscountPercentage() {
        lock.readLock().lock();
        try {
            return discountPercentage;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public static class Builder {
        private final String customerId;
        private final List<OrderItem> items;
        private PaymentMethod paymentMethod;
        private double discountPercentage;
        
        public Builder(String customerId) {
            if (customerId == null || customerId.trim().isEmpty()) {
                throw new IllegalArgumentException("Customer ID cannot be null or empty");
            }
            this.customerId = customerId;
            this.items = new ArrayList<>();
            this.paymentMethod = PaymentMethod.CARD;
            this.discountPercentage = 0.0;
        }
        
        public Builder addItem(OrderItem item) {
            if (item == null) {
                throw new IllegalArgumentException("OrderItem cannot be null");
            }
            this.items.add(item);
            return this;
        }
        
        public Builder withPaymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }
        
        public Builder withDiscount(double discountPercentage) {
            if (discountPercentage < 0 || discountPercentage > 100) {
                throw new IllegalArgumentException("Discount must be between 0 and 100");
            }
            this.discountPercentage = discountPercentage;
            return this;
        }
        
        public Order build() {
            if (items.isEmpty()) {
                throw new IllegalStateException("Order must have at least one item");
            }
            return new Order(this);
        }
    }
}
