// src/test/java/com/testcraft/demo/service/CustomerTest.java

package com.testcraft.demo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerTest {

    @Mock
    private Customer customer;

    @Test
    public void testCustomerConstructor() {
        // Arrange
        String id = "123";
        String name = "John Doe";
        String email = "john.doe@example.com";
        String tier = "Premium";

        // Act
        Customer actualCustomer = new Customer(id, name, email, tier);

        // Assert
        assertEquals(id, actualCustomer.getId());
        assertEquals(name, actualCustomer.getName());
        assertEquals(email, actualCustomer.getEmail());
        assertEquals(tier, actualCustomer.getTier());
    }

    @Test
    public void testCustomerGetters() {
        // Arrange
        String id = "123";
        String name = "John Doe";
        String email = "john.doe@example.com";
        String tier = "Premium";

        Customer actualCustomer = new Customer(id, name, email, tier);

        // Act and Assert
        assertEquals(id, actualCustomer.getId());
        assertEquals(name, actualCustomer.getName());
        assertEquals(email, actualCustomer.getEmail());
        assertEquals(tier, actualCustomer.getTier());
    }

    @Test
    public void testCustomerToString() {
        // Arrange
        String id = "123";
        String name = "John Doe";
        String email = "john.doe@example.com";
        String tier = "Premium";

        Customer actualCustomer = new Customer(id, name, email, tier);

        // Act and Assert
        assertEquals(String.format("%s (%s) - %s", name, tier, email), actualCustomer.toString());
    }

    @Test
    public void testCustomerNullConstructor() {
        // Act and Assert
        assertThrows(NullPointerException.class, () -> new Customer(null, "John Doe", "john.doe@example.com", "Premium"));
    }

    @Test
    public void testCustomerNullNameConstructor() {
        // Act and Assert
        assertThrows(NullPointerException.class, () -> new Customer("123", null, "john.doe@example.com", "Premium"));
    }

    @Test
    public void testCustomerNullEmailConstructor() {
        // Act and Assert
        assertThrows(NullPointerException.class, () -> new Customer("123", "John Doe", null, "Premium"));
    }

    @Test
    public void testCustomerNullTierConstructor() {
        // Act and Assert
        assertThrows(NullPointerException.class, () -> new Customer("123", "John Doe", "john.doe@example.com", null));
    }

    @Test
    public void testCustomerEmptyConstructor() {
        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> new Customer("", "John Doe", "john.doe@example.com", "Premium"));
    }

    @Test
    public void testCustomerEmptyNameConstructor() {
        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> new Customer("123", "", "john.doe@example.com", "Premium"));
    }

    @Test
    public void testCustomerEmptyEmailConstructor() {
        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> new Customer("123", "John Doe", "", "Premium"));
    }

    @Test
    public void testCustomerEmptyTierConstructor() {
        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> new Customer("123", "John Doe", "john.doe@example.com", ""));
    }
}