package com.testcraft.demo;

import com.testcraft.demo.model.*;
import com.testcraft.demo.service.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ECommerceApplication {
    
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║   E-COMMERCE ORDER MANAGEMENT SYSTEM v2.0       ║");
        System.out.println("║   Complex Java Application Demo                 ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");
        
        ECommerceOrchestrator orchestrator = new ECommerceOrchestrator();
        
        try {
            demonstrateCompleteWorkflow(orchestrator);
        } catch (Exception e) {
            System.err.println("Error in application: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\nShutting down services...");
            orchestrator.shutdown();
            System.out.println("Application terminated successfully.");
        }
    }
    
    private static void demonstrateCompleteWorkflow(ECommerceOrchestrator orchestrator) 
            throws Exception {
        
        System.out.println("═══ SCENARIO 1: Simple Order Placement ═══\n");
        
        Map<String, Integer> cart1 = new HashMap<>();
        cart1.put("P003", 2);
        cart1.put("P005", 1);
        
        Order order1 = orchestrator.placeOrder("C001", cart1, PaymentMethod.UPI);
        System.out.println("\n" + order1.getOrderSummary());
        
        Thread.sleep(1000);
        
        System.out.println("\n═══ SCENARIO 2: Bulk Order with Discount ═══\n");
        
        Map<String, Integer> cart2 = new HashMap<>();
        cart2.put("P001", 3);
        cart2.put("P002", 5);
        cart2.put("P006", 2);
        
        Order order2 = orchestrator.placeOrder("C002", cart2, PaymentMethod.CARD);
        System.out.println("\n" + order2.getOrderSummary());
        
        Thread.sleep(1000);
        
        System.out.println("\n═══ SCENARIO 3: COD Order for Regular Customer ═══\n");
        
        Map<String, Integer> cart3 = new HashMap<>();
        cart3.put("P004", 1);
        cart3.put("P007", 5);
        
        Order order3 = orchestrator.placeOrder("C004", cart3, PaymentMethod.COD);
        System.out.println("\n" + order3.getOrderSummary());
        
        Thread.sleep(2000);
        
        System.out.println("\n═══ SCENARIO 4: Product Search ═══\n");
        
        List<Product> searchResults = orchestrator.searchProducts("electronics");
        System.out.println("Search results for 'electronics':");
        searchResults.forEach(p -> System.out.println("  • " + p));
        
        System.out.println("\n═══ SCENARIO 5: Customer Dashboard ═══\n");
        
        Map<String, Object> dashboard = orchestrator.getCustomerDashboard("C001");
        System.out.println("Customer: " + dashboard.get("customer"));
        System.out.println("Total Orders: " + dashboard.get("totalOrders"));
        System.out.println("Lifetime Value: ₹" + dashboard.get("lifetimeValue"));
        System.out.println("Current Tier: " + dashboard.get("tier"));
        System.out.println("Available Discount: " + dashboard.get("discount") + "%");
        
        Thread.sleep(1000);
        
        System.out.println("\n═══ SCENARIO 6: Bulk Order Processing ═══\n");
        
        List<BulkOrderRequest> bulkRequests = new ArrayList<>();
        
        Map<String, Integer> bulk1 = new HashMap<>();
        bulk1.put("P003", 15);
        bulkRequests.add(new BulkOrderRequest("C003", bulk1, PaymentMethod.WALLET));
        
        Map<String, Integer> bulk2 = new HashMap<>();
        bulk2.put("P005", 10);
        bulkRequests.add(new BulkOrderRequest("C003", bulk2, PaymentMethod.NET_BANKING));
        
        orchestrator.processBulkOrders(bulkRequests);
        System.out.println("Bulk orders submitted for processing...");
        
        Thread.sleep(3000);
        
        System.out.println("\n═══ SCENARIO 7: Order Cancellation ═══\n");
        
        Map<String, Integer> cart4 = new HashMap<>();
        cart4.put("P006", 1);
        Order orderToCancel = orchestrator.placeOrder("C001", cart4, PaymentMethod.UPI);
        
        System.out.println("Placed order: " + orderToCancel.getOrderId());
        Thread.sleep(500);
        
        orchestrator.cancelOrder(orderToCancel.getOrderId());
        System.out.println("Order cancelled successfully");
        
        Thread.sleep(2000);
        
        System.out.println("\n═══ SCENARIO 8: Low Stock Trigger ═══\n");
        
        try {
            Map<String, Integer> largeCart = new HashMap<>();
            largeCart.put("P001", 20);
            largeCart.put("P006", 25);
            
            Order largeOrder = orchestrator.placeOrder("C002", largeCart, PaymentMethod.CARD);
            System.out.println("Large order placed: " + largeOrder.getOrderId());
            
        } catch (Exception e) {
            System.out.println("Expected exception for large order: " + e.getMessage());
        }
        
        Thread.sleep(2000);
        
        System.out.println("\n═══ SCENARIO 9: Admin Dashboard ═══\n");
        
        Map<String, Object> adminDashboard = orchestrator.getAdminDashboard();
        System.out.println("Total Orders: " + adminDashboard.get("totalOrders"));
        System.out.println("Total Customers: " + adminDashboard.get("totalCustomers"));
        System.out.println("Total Revenue: ₹" + adminDashboard.get("totalRevenue"));
        System.out.println("Inventory Value: ₹" + adminDashboard.get("inventoryValue"));
        System.out.println("\nOrder Statistics:");
        
        @SuppressWarnings("unchecked")
        Map<OrderStatus, Long> stats = (Map<OrderStatus, Long>) adminDashboard.get("orderStatistics");
        stats.forEach((status, count) -> 
                System.out.println("  " + status + ": " + count));
        
        System.out.println("\nLow Stock Products:");
        @SuppressWarnings("unchecked")
        Map<String, Integer> lowStock = (Map<String, Integer>) adminDashboard.get("lowStockProducts");
        if (lowStock.isEmpty()) {
            System.out.println("  No low stock products");
        } else {
            lowStock.forEach((productId, quantity) -> 
                    System.out.println("  " + productId + ": " + quantity + " units"));
        }
        
        Thread.sleep(2000);
        
        System.out.println("\n═══ SCENARIO 10: Comprehensive Sales Report ═══\n");
        orchestrator.generateSalesReport();
        
        Thread.sleep(2000);
        
        System.out.println("\n═══ SCENARIO 11: Exception Handling Demo ═══\n");
        
        try {
            Map<String, Integer> invalidCart = new HashMap<>();
            invalidCart.put("P999", 1);
            orchestrator.placeOrder("C001", invalidCart, PaymentMethod.UPI);
        } catch (Exception e) {
            System.out.println("✓ Caught expected exception: " + e.getMessage());
        }
        
        try {
            Map<String, Integer> tooManyItems = new HashMap<>();
            tooManyItems.put("P001", 1000);
            orchestrator.placeOrder("C001", tooManyItems, PaymentMethod.CARD);
        } catch (Exception e) {
            System.out.println("✓ Caught expected exception: " + e.getMessage());
        }
        
        try {
            orchestrator.placeOrder("INVALID_CUSTOMER", cart1, PaymentMethod.UPI);
        } catch (Exception e) {
            System.out.println("✓ Caught expected exception: " + e.getMessage());
        }
        
        System.out.println("\n═══ WAITING FOR ASYNC OPERATIONS TO COMPLETE ═══\n");
        System.out.println("Processing orders in background...");
        
        Thread.sleep(5000);
        
        System.out.println("\n═══ FINAL SYSTEM STATE ═══\n");
        orchestrator.generateSalesReport();
        
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║   DEMONSTRATION COMPLETED SUCCESSFULLY!          ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
    }
}
