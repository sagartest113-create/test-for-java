package com.testcraft.demo.service;

public class Customer {
    private final String id;
    private final String name;
    private final String email;
    private final String tier;
    
    public Customer(String id, String name, String email, String tier) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.tier = tier;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getTier() {
        return tier;
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s) - %s", name, tier, email);
    }
}
