package com.testcraft.demo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    public void testCustomerConstructorNullId() {
        // Arrange
        String id = null;
        String name = "John Doe";
        String email = "john.doe@example.com";
        String tier = "Premium";

        // Act and Assert
        assertThrows(NullPointerException.class, () -> new Customer(id, name, email, tier));
    }

    @Test
    public void testCustomerConstructorNullName() {
        // Arrange
        String id = "123";
        String name = null;
        String email = "john.doe@example.com";
        String tier = "Premium";

        // Act and Assert
        assertThrows(NullPointerException.class, () -> new Customer(id, name, email, tier));
    }

    @Test
    public void testCustomerConstructorNullEmail() {
        // Arrange
        String id = "123";
        String name = "John Doe";
        String email = null;
        String tier = "Premium";

        // Act and Assert
        assertThrows(NullPointerException.class, () -> new Customer(id, name, email, tier));
    }

    @Test
    public void testCustomerConstructorNullTier() {
        // Arrange
        String id = "123";
        String name = "John Doe";
        String email = "john.doe@example.com";
        String tier = null;

        // Act and Assert
        assertThrows(NullPointerException.class, () -> new Customer(id, name, email, tier));
    }

    @Test
    public void testCustomerGetters() {
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
    public void testCustomerToString() {
        // Arrange
        String id = "123";
        String name = "John Doe";
        String email = "john.doe@example.com";
        String tier = "Premium";

        // Act
        Customer actualCustomer = new Customer(id, name, email, tier);

        // Assert
        assertEquals(String.format("%s (%s) - %s", name, tier, email), actualCustomer.toString());
    }
}