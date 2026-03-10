package com.testcraft.demo.service;

import com.testcraft.demo.model.PaymentMethod;
import java.util.Map;

public class BulkOrderRequest {
    private final String customerId;
    private final Map<String, Integer> productQuantities;
    private final PaymentMethod paymentMethod;
    
    public BulkOrderRequest(String customerId, Map<String, Integer> productQuantities, 
                          PaymentMethod paymentMethod) {
        this.customerId = customerId;
        this.productQuantities = productQuantities;
        this.paymentMethod = paymentMethod;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public Map<String, Integer> getProductQuantities() {
        return productQuantities;
    }
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
}
