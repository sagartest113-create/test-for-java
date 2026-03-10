package com.testcraft.demo.service;

import com.testcraft.demo.model.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class OrderManagementService {
    private final ConcurrentHashMap<String, Order> orders;
    private final ConcurrentHashMap<String, List<Order>> customerOrders;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutor;
    private final AtomicInteger orderCounter;
    private final BlockingQueue<OrderProcessingTask> processingQueue;
    
    private static final int MAX_CONCURRENT_PROCESSING = 5;
    private static final int PROCESSING_DELAY_SECONDS = 2;
    
    public OrderManagementService() {
        this.orders = new ConcurrentHashMap<>();
        this.customerOrders = new ConcurrentHashMap<>();
        this.executorService = Executors.newFixedThreadPool(MAX_CONCURRENT_PROCESSING);
        this.scheduledExecutor = Executors.newScheduledThreadPool(2);
        this.orderCounter = new AtomicInteger(0);
        this.processingQueue = new LinkedBlockingQueue<>(100);
        
        startBackgroundProcessing();
        startAutoShipmentScheduler();
    }
    
    public Order createOrder(Order order) throws OrderProcessingException {
        if (order == null) {
            throw new OrderProcessingException("Order cannot be null");
        }
        
        validateOrder(order);
        
        orders.put(order.getOrderId(), order);
        customerOrders.computeIfAbsent(order.getCustomerId(), k -> new CopyOnWriteArrayList<>())
                     .add(order);
        
        orderCounter.incrementAndGet();
        
        System.out.println("Order created: " + order.getOrderId());
        return order;
    }
    
    public CompletableFuture<Order> processOrderAsync(String orderId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Order order = orders.get(orderId);
                if (order == null) {
                    throw new OrderProcessingException("Order not found: " + orderId);
                }
                
                if (order.getStatus() != OrderStatus.PENDING) {
                    throw new OrderProcessingException("Order is not in PENDING state");
                }
                
                order.updateStatus(OrderStatus.CONFIRMED);
                System.out.println("Order confirmed: " + orderId);
                
                Thread.sleep(1000);
                
                order.updateStatus(OrderStatus.PROCESSING);
                System.out.println("Order processing: " + orderId);
                
                boolean paymentSuccess = processPayment(order);
                if (!paymentSuccess) {
                    order.updateStatus(OrderStatus.CANCELLED);
                    throw new OrderProcessingException("Payment failed for order: " + orderId);
                }
                
                Thread.sleep(PROCESSING_DELAY_SECONDS * 1000);
                
                order.updateStatus(OrderStatus.SHIPPED);
                System.out.println("Order shipped: " + orderId);
                
                return order;
                
            } catch (InvalidOrderStateException | InterruptedException e) {
                throw new RuntimeException("Failed to process order: " + e.getMessage(), e);
            } catch (OrderProcessingException e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }
    
    private boolean processPayment(Order order) {
        System.out.println("Processing payment for order: " + order.getOrderId());
        System.out.println("Payment method: " + order.getPaymentMethod());
        System.out.println("Total amount: ₹" + order.calculateTotal());
        
        if (order.getPaymentMethod() == PaymentMethod.COD) {
            return true;
        }
        
        double random = Math.random();
        boolean success = random > 0.1;
        
        if (success) {
            System.out.println("Payment successful for order: " + order.getOrderId());
        } else {
            System.out.println("Payment failed for order: " + order.getOrderId());
        }
        
        return success;
    }
    
    private void validateOrder(Order order) throws OrderProcessingException {
        if (order.getItems().isEmpty()) {
            throw new OrderProcessingException("Order must have at least one item");
        }
        
        if (order.calculateTotal() <= 0) {
            throw new OrderProcessingException("Order total must be positive");
        }
        
        if (order.getCustomerId() == null || order.getCustomerId().trim().isEmpty()) {
            throw new OrderProcessingException("Customer ID is required");
        }
    }
    
    public Order getOrder(String orderId) throws OrderProcessingException {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new OrderProcessingException("Order not found: " + orderId);
        }
        return order;
    }
    
    public List<Order> getCustomerOrders(String customerId) {
        return customerOrders.getOrDefault(customerId, new ArrayList<>());
    }
    
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orders.values().stream()
                .filter(order -> order.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    public Map<OrderStatus, Long> getOrderStatistics() {
        return orders.values().stream()
                .collect(Collectors.groupingBy(
                        Order::getStatus,
                        Collectors.counting()
                ));
    }
    
    public double calculateCustomerLifetimeValue(String customerId) {
        List<Order> customerOrderList = customerOrders.getOrDefault(customerId, new ArrayList<>());
        return customerOrderList.stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(Order::calculateTotal)
                .sum();
    }
    
    public void cancelOrder(String orderId) throws OrderProcessingException, InvalidOrderStateException {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new OrderProcessingException("Order not found: " + orderId);
        }
        
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new OrderProcessingException("Cannot cancel order in " + order.getStatus() + " state");
        }
        
        order.updateStatus(OrderStatus.CANCELLED);
        System.out.println("Order cancelled: " + orderId);
    }
    
    public CompletableFuture<Void> processBulkOrders(List<String> orderIds) {
        List<CompletableFuture<Order>> futures = orderIds.stream()
                .map(this::processOrderAsync)
                .collect(Collectors.toList());
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    System.out.println("All " + orderIds.size() + " orders processed");
                });
    }
    
    private void startBackgroundProcessing() {
        executorService.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    OrderProcessingTask task = processingQueue.take();
                    processOrderAsync(task.getOrderId())
                            .exceptionally(ex -> {
                                System.err.println("Error processing order: " + ex.getMessage());
                                return null;
                            });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }
    
    private void startAutoShipmentScheduler() {
        scheduledExecutor.scheduleAtFixedRate(() -> {
            List<Order> processingOrders = getOrdersByStatus(OrderStatus.PROCESSING);
            
            for (Order order : processingOrders) {
                try {
                    long minutesSinceUpdate = java.time.Duration.between(
                            order.getUpdatedAt(),
                            java.time.LocalDateTime.now()
                    ).toMinutes();
                    
                    if (minutesSinceUpdate >= 5) {
                        order.updateStatus(OrderStatus.SHIPPED);
                        System.out.println("Auto-shipped order: " + order.getOrderId());
                    }
                } catch (InvalidOrderStateException e) {
                    System.err.println("Failed to auto-ship order: " + e.getMessage());
                }
            }
        }, 1, 1, TimeUnit.MINUTES);
    }
    
    public void applyPromoCode(String orderId, String promoCode) 
            throws OrderProcessingException, InvalidOrderStateException {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new OrderProcessingException("Order not found: " + orderId);
        }
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderProcessingException("Promo code can only be applied to pending orders");
        }
        
        double discount = switch (promoCode.toUpperCase()) {
            case "SAVE10" -> 10.0;
            case "SAVE20" -> 20.0;
            case "FIRST50" -> 50.0;
            case "WELCOME25" -> 25.0;
            default -> throw new OrderProcessingException("Invalid promo code: " + promoCode);
        };
        
        Order.Builder builder = new Order.Builder(order.getCustomerId())
                .withPaymentMethod(order.getPaymentMethod())
                .withDiscount(discount);
        
        for (OrderItem item : order.getItems()) {
            builder.addItem(item);
        }
        
        Order newOrder = builder.build();
        orders.put(orderId, newOrder);
        
        System.out.println("Promo code " + promoCode + " applied to order: " + orderId);
    }
    
    public int getTotalOrderCount() {
        return orderCounter.get();
    }
    
    public void shutdown() {
        executorService.shutdown();
        scheduledExecutor.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            if (!scheduledExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduledExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            scheduledExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    private static class OrderProcessingTask {
        private final String orderId;
        
        public OrderProcessingTask(String orderId) {
            this.orderId = orderId;
        }
        
        public String getOrderId() {
            return orderId;
        }
    }
}
