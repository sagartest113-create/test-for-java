package io.recruitcrm.microservice.candidate.exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ResourceNotFoundExceptionTest {

    @Test
    @DisplayName("Default constructor sets message to null")
    void testDefaultConstructor() {
        ResourceNotFoundException exception = new ResourceNotFoundException();
        Assertions.assertThat(exception.getMessage()).isNull();
    }

    @Test
    @DisplayName("Message constructor sets message correctly")
    void testMessageConstructor() {
        String message = "Custom message";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);
        Assertions.assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("Cause constructor sets cause correctly")
    void testCauseConstructor() {
        RuntimeException cause = new RuntimeException();
        ResourceNotFoundException exception = new ResourceNotFoundException("Custom message", cause);
        Assertions.assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    @DisplayName("Message and cause constructor sets both correctly")
    void testMessageAndCauseConstructor() {
        String message = "Custom message";
        RuntimeException cause = new RuntimeException();
        ResourceNotFoundException exception = new ResourceNotFoundException(message, cause);
        Assertions.assertThat(exception.getMessage()).isEqualTo(message);
        Assertions.assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    @DisplayName("Of method creates correct exception instance")
    void testOfMethod() {
        String resource = "Candidate";
        Object id = 123;
        ResourceNotFoundException exception = ResourceNotFoundException.of(resource, id);
        Assertions.assertThat(exception.getMessage()).isEqualTo(resource + " with id " + id + " not found");
    }
}