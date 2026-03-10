package com.testcraft.demo.service;

import com.testcraft.demo.model.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ECommerceOrchestrator implements StockObserver {
    private final OrderManagementService orderService;
    private final InventoryManagementService inventoryService;
    private final Map<String, Customer> customers;
    private final ExecutorService notificationExecutor;
    
    public ECommerceOrchestrator() {
        this.orderService = new OrderManagementService();
        this.inventoryService = new InventoryManagementService();
        this.customers = new ConcurrentHashMap<>();
        this.notificationExecutor = Executors.newFixedThreadPool(3);
        
        inventoryService.addStockObserver(this);
        initializeSampleCustomers();
    }
    
    private void initializeSampleCustomers() {
        customers.put("C001", new Customer("C001", "Rahul Sharma", "rahul@example.com", "Premium"));
        customers.put("C002", new Customer("C002", "Priya Singh", "priya@example.com", "Gold"));
        customers.put("C003", new Customer("C003", "Amit Kumar", "amit@example.com", "Silver"));
        customers.put("C004", new Customer("C004", "Neha Verma", "neha@example.com", "Regular"));
    }
    
    public Order placeOrder(String customerId, Map<String, Integer> productQuantities, 
                           PaymentMethod paymentMethod) throws Exception {
        
        Customer customer = customers.get(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found: " + customerId);
        }
        
        Order.Builder orderBuilder = new Order.Builder(customerId)
                .withPaymentMethod(paymentMethod);
        
        Map<String, Integer> reservedStock = new ConcurrentHashMap<>();
        
        try {
            for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {
                String productId = entry.getKey();
                int quantity = entry.getValue();
                
                Product product = inventoryService.getProduct(productId);
                
                boolean reserved = inventoryService.reserveStock(productId, quantity);
                if (reserved) {
                    reservedStock.put(productId, quantity);
                    
                    OrderItem item = new OrderItem(
                            product.getProductId(),
                            product.getName(),
                            quantity,
                            product.getPrice()
                    );
                    orderBuilder.addItem(item);
                }
            }
            
            double discount = calculateCustomerDiscount(customer);
            if (discount > 0) {
                orderBuilder.withDiscount(discount);
            }
            
            Order order = orderBuilder.build();
            
            order.applyBulkDiscount();
            
            Order createdOrder = orderService.createOrder(order);
            
            sendNotificationAsync(customer, "Order Placed", 
                    "Your order " + createdOrder.getOrderId() + " has been placed successfully!");
            
            orderService.processOrderAsync(createdOrder.getOrderId())
                    .thenAccept(processedOrder -> {
                        for (Map.Entry<String, Integer> entry : reservedStock.entrySet()) {
                            inventoryService.confirmReservation(entry.getKey(), entry.getValue());
                        }
                        
                        sendNotificationAsync(customer, "Order Shipped",
                                "Your order " + processedOrder.getOrderId() + " has been shipped!");
                    })
                    .exceptionally(ex -> {
                        for (Map.Entry<String, Integer> entry : reservedStock.entrySet()) {
                            inventoryService.releaseStock(entry.getKey(), entry.getValue());
                        }
                        
                        sendNotificationAsync(customer, "Order Failed",
                                "Your order processing failed: " + ex.getMessage());
                        return null;
                    });
            
            return createdOrder;
            
        } catch (Exception e) {
            for (Map.Entry<String, Integer> entry : reservedStock.entrySet()) {
                inventoryService.releaseStock(entry.getKey(), entry.getValue());
            }
            throw e;
        }
    }
    
    private double calculateCustomerDiscount(Customer customer) {
        return switch (customer.getTier()) {
            case "Premium" -> 20.0;
            case "Gold" -> 15.0;
            case "Silver" -> 10.0;
            default -> 0.0;
        };
    }
    
    public void cancelOrder(String orderId) throws Exception {
        Order order = orderService.getOrder(orderId);
        
        if (order.getStatus() != OrderStatus.PENDING && 
            order.getStatus() != OrderStatus.CONFIRMED) {
            throw new OrderProcessingException(
                    "Cannot cancel order in " + order.getStatus() + " state");
        }
        
        for (OrderItem item : order.getItems()) {
            inventoryService.releaseStock(item.getProductId(), item.getQuantity());
        }
        
        orderService.cancelOrder(orderId);
        
        Customer customer = customers.get(order.getCustomerId());
        if (customer != null) {
            sendNotificationAsync(customer, "Order Cancelled",
                    "Your order " + orderId + " has been cancelled");
        }
    }
    
    public List<Product> searchProducts(String query) {
        return inventoryService.getAllProducts().stream()
                .filter(p -> p.getName().toLowerCase().contains(query.toLowerCase()) ||
                           p.getCategory().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    public Map<String, Object> getCustomerDashboard(String customerId) throws Exception {
        Customer customer = customers.get(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found");
        }
        
        List<Order> orders = orderService.getCustomerOrders(customerId);
        double lifetimeValue = orderService.calculateCustomerLifetimeValue(customerId);
        
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("customer", customer);
        dashboard.put("totalOrders", orders.size());
        dashboard.put("lifetimeValue", lifetimeValue);
        dashboard.put("recentOrders", orders.stream()
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .limit(5)
                .collect(Collectors.toList()));
        dashboard.put("tier", customer.getTier());
        dashboard.put("discount", calculateCustomerDiscount(customer));
        
        return dashboard;
    }
    
    public Map<String, Object> getAdminDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        dashboard.put("totalOrders", orderService.getTotalOrderCount());
        dashboard.put("orderStatistics", orderService.getOrderStatistics());
        dashboard.put("lowStockProducts", inventoryService.getLowStockProducts());
        dashboard.put("inventoryValue", inventoryService.calculateInventoryValue());
        dashboard.put("inventoryReport", inventoryService.getInventoryReport());
        dashboard.put("totalCustomers", customers.size());
        
        double totalRevenue = customers.values().stream()
                .mapToDouble(c -> orderService.calculateCustomerLifetimeValue(c.getId()))
                .sum();
        dashboard.put("totalRevenue", totalRevenue);
        
        return dashboard;
    }
    
    public void processBulkOrders(List<BulkOrderRequest> requests) {
        List<String> orderIds = new ArrayList<>();
        
        for (BulkOrderRequest request : requests) {
            try {
                Order order = placeOrder(
                        request.getCustomerId(),
                        request.getProductQuantities(),
                        request.getPaymentMethod()
                );
                orderIds.add(order.getOrderId());
            } catch (Exception e) {
                System.err.println("Failed to place bulk order: " + e.getMessage());
            }
        }
        
        if (!orderIds.isEmpty()) {
            orderService.processBulkOrders(orderIds)
                    .thenRun(() -> System.out.println("Bulk orders processing completed"));
        }
    }
    
    private void sendNotificationAsync(Customer customer, String subject, String message) {
        notificationExecutor.submit(() -> {
            try {
                Thread.sleep(500);
                System.out.println("\n--- NOTIFICATION ---");
                System.out.println("To: " + customer.getEmail());
                System.out.println("Subject: " + subject);
                System.out.println("Message: " + message);
                System.out.println("Time: " + LocalDateTime.now());
                System.out.println("-------------------\n");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
    
    @Override
    public void onLowStock(String productId, int quantity) {
        System.out.println("\n*** LOW STOCK ALERT ***");
        System.out.println("Product: " + productId);
        System.out.println("Current Stock: " + quantity);
        System.out.println("Action: Automatic reorder triggered");
        System.out.println("**********************\n");
    }
    
    public void generateSalesReport() {
        System.out.println("\n========== SALES REPORT ==========");
        
        Map<OrderStatus, Long> stats = orderService.getOrderStatistics();
        stats.forEach((status, count) -> 
                System.out.println(status + ": " + count + " orders"));
        
        System.out.println("\nTop Customers by Lifetime Value:");
        customers.values().stream()
                .sorted((c1, c2) -> Double.compare(
                        orderService.calculateCustomerLifetimeValue(c2.getId()),
                        orderService.calculateCustomerLifetimeValue(c1.getId())))
                .limit(5)
                .forEach(customer -> {
                    double ltv = orderService.calculateCustomerLifetimeValue(customer.getId());
                    System.out.println(String.format("%s (%s): ₹%.2f",
                            customer.getName(), customer.getTier(), ltv));
                });
        
        System.out.println("\nInventory Status:");
        System.out.println("Total Value: ₹" + inventoryService.calculateInventoryValue());
        System.out.println("Low Stock Products: " + inventoryService.getLowStockProducts().size());
        
        System.out.println("==================================\n");
    }
    
    public void shutdown() {
        orderService.shutdown();
        notificationExecutor.shutdown();
        try {
            if (!notificationExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                notificationExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            notificationExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
