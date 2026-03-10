package com.testcraft.demo;

import com.testcraft.demo.model.*;
import com.testcraft.demo.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ECommerceApplicationTest {

    @Mock
    private ECommerceService service;

    @Mock
    private ECommerceOrchestrator orchestrator;

    @InjectMocks
    private ECommerceApplication application;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testPlaceOrder() throws Exception {
        // Arrange
        Map<String, Integer> cart = new HashMap<>();
        cart.put("P003", 2);
        cart.put("P005", 1);

        when(orchestrator.placeOrder(any(), any(), any())).thenReturn(new Order("ORDER_ID", cart));

        // Act
        mockMvc.perform(post("/place-order")
                .contentType("application/json")
                .content("{\"customerId\":\"C001\",\"cart\":{\"P003\":2,\"P005\":1},\"paymentMethod\":\"UPI\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("ORDER_ID"));
    }

    @Test
    public void testPlaceOrderInvalidCart() {
        // Arrange
        Map<String, Integer> cart = new HashMap<>();
        cart.put("P999", 1);

        // Act and Assert
        assertThrows(Exception.class, () -> application.demonstrateCompleteWorkflow(orchestrator));
    }

    @Test
    public void testPlaceOrderTooManyItems() {
        // Arrange
        Map<String, Integer> cart = new HashMap<>();
        cart.put("P001", 1000);

        // Act and Assert
        assertThrows(Exception.class, () -> application.demonstrateCompleteWorkflow(orchestrator));
    }

    @Test
    public void testPlaceOrderInvalidCustomer() {
        // Act and Assert
        assertThrows(Exception.class, () -> application.demonstrateCompleteWorkflow(orchestrator));
    }
}