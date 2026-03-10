package com.testcraft.demo;

import com.testcraft.demo.model.*;
import com.testcraft.demo.service.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ECommerceApplicationTest {

    @Mock
    private ECommerceOrchestrator orchestrator;

    @InjectMocks
    private ECommerceApplication application;

    @BeforeEach
    void setup() {
        // Initialize mocks before each test
    }

    @Test
    @DisplayName("Demonstrate complete workflow")
    void demonstrateCompleteWorkflow() throws Exception {
        // Arrange
        Map<String, Integer> cart1 = new HashMap<>();
        cart1.put("P003", 2);
        cart1.put("P005", 1);

        // Act
        application.demonstrateCompleteWorkflow(orchestrator);

        // Assert
        verify(orchestrator, times(8)).placeOrder(anyString(), anyMap(), any());
        verify(orchestrator, times(1)).searchProducts(anyString());
        verify(orchestrator, times(1)).getCustomerDashboard(anyString());
        verify(orchestrator, times(1)).processBulkOrders(anyList());
        verify(orchestrator, times(1)).cancelOrder(anyString());
        verify(orchestrator, times(1)).generateSalesReport();
        verify(orchestrator, times(1)).getAdminDashboard();
        verify(orchestrator, times(1)).generateSalesReport();
    }

    @Test
    @DisplayName("Place order with valid cart")
    void placeOrderValidCart() {
        // Arrange
        Map<String, Integer> cart = new HashMap<>();
        cart.put("P003", 2);
        cart.put("P005", 1);

        // Act
        Order order = application.placeOrder("C001", cart, PaymentMethod.UPI);

        // Assert
        assertThat(order).isNotNull();
        assertThat(order.getOrderId()).isNotBlank();
        assertThat(order.getCustomer()).isEqualTo("C001");
        assertThat(order.getProducts()).containsExactlyInAnyOrder(
                new Product("P003", "Product 3", 10.99),
                new Product("P005", "Product 5", 5.99)
        );
    }

    @Test
    @DisplayName("Place order with invalid cart")
    void placeOrderInvalidCart() {
        // Arrange
        Map<String, Integer> cart = new HashMap<>();
        cart.put("P999", 1);

        // Act and Assert
        assertThrows(Exception.class, () -> application.placeOrder("C001", cart, PaymentMethod.UPI));
    }

    @Test
    @DisplayName("Place order with too many items")
    void placeOrderTooManyItems() {
        // Arrange
        Map<String, Integer> cart = new HashMap<>();
        cart.put("P001", 1000);

        // Act and Assert
        assertThrows(Exception.class, () -> application.placeOrder("C001", cart, PaymentMethod.CARD));
    }

    @Test
    @DisplayName("Search products")
    void searchProducts() {
        // Arrange
        String query = "electronics";

        // Act
        List<Product> results = application.searchProducts(query);

        // Assert
        assertThat(results).isNotNull();
        assertThat(results).isNotEmpty();
    }

    @Test
    @DisplayName("Get customer dashboard")
    void getCustomerDashboard() {
        // Arrange
        String customer = "C001";

        // Act
        Map<String, Object> dashboard = application.getCustomerDashboard(customer);

        // Assert
        assertThat(dashboard).isNotNull();
        assertThat(dashboard.get("customer")).isEqualTo(customer);
        assertThat(dashboard.get("totalOrders")).isNotNull();
        assertThat(dashboard.get("lifetimeValue")).isNotNull();
        assertThat(dashboard.get("tier")).isNotNull();
        assertThat(dashboard.get("discount")).isNotNull();
    }

    @Test
    @DisplayName("Process bulk orders")
    void processBulkOrders() {
        // Arrange
        List<BulkOrderRequest> requests = new ArrayList<>();
        Map<String, Integer> bulk1 = new HashMap<>();
        bulk1.put("P003", 15);
        requests.add(new BulkOrderRequest("C003", bulk1, PaymentMethod.WALLET));
        Map<String, Integer> bulk2 = new HashMap<>();
        bulk2.put("P005", 10);
        requests.add(new BulkOrderRequest("C003", bulk2, PaymentMethod.NET_BANKING));

        // Act
        application.processBulkOrders(requests);

        // Assert
        verify(orchestrator, times(1)).processBulkOrders(anyList());
    }

    @Test
    @DisplayName("Cancel order")
    void cancelOrder() {
        // Arrange
        String orderId = "ORDER_001";

        // Act
        application.cancelOrder(orderId);

        // Assert
        verify(orchestrator, times(1)).cancelOrder(anyString());
    }

    @Test
    @DisplayName("Generate sales report")
    void generateSalesReport() {
        // Act
        application.generateSalesReport();

        // Assert
        verify(orchestrator, times(1)).generateSalesReport();
    }

    @Test
    @DisplayName("Get admin dashboard")
    void getAdminDashboard() {
        // Act
        Map<String, Object> dashboard = application.getAdminDashboard();

        // Assert
        assertThat(dashboard).isNotNull();
        assertThat(dashboard.get("totalOrders")).isNotNull();
        assertThat(dashboard.get("totalCustomers")).isNotNull();
        assertThat(dashboard.get("totalRevenue")).isNotNull();
        assertThat(dashboard.get("inventoryValue")).isNotNull();
        assertThat(dashboard.get("orderStatistics")).isNotNull();
        assertThat(dashboard.get("lowStockProducts")).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("paymentMethods")
    @DisplayName("Place order with different payment methods")
    void placeOrderPaymentMethod(PaymentMethod method) {
        // Arrange
        Map<String, Integer> cart = new HashMap<>();
        cart.put("P003", 2);
        cart.put("P005", 1);

        // Act
        Order order = application.placeOrder("C001", cart, method);

        // Assert
        assertThat(order).isNotNull();
        assertThat(order.getOrderId()).isNotBlank();
        assertThat(order.getCustomer()).isEqualTo("C001");
        assertThat(order.getProducts()).containsExactlyInAnyOrder(
                new Product("P003", "Product 3", 10.99),
                new Product("P005", "Product 5", 5.99)
        );
    }

    static Stream<PaymentMethod> paymentMethods() {
        return Stream.of(PaymentMethod.values());
    }
}