package com.testcraft.demo.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BinarySearchRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("Constructs a BinarySearchRequest with valid input")
    void constructsWithValidInput() {
        int[] sortedArray = {1, 2, 3, 4, 5};
        int target = 3;
        BinarySearchRequest request = new BinarySearchRequest(sortedArray, target);
        assertThat(request.sortedArray()).isEqualTo(sortedArray);
        assertThat(request.target()).isEqualTo(target);
    }

    @Test
    @DisplayName("Constructs a BinarySearchRequest with null array")
    void constructsWithNullArray() {
        int[] sortedArray = null;
        int target = 3;
        assertThatThrownBy(() -> new BinarySearchRequest(sortedArray, target))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Array must not be null");
    }

    @Test
    @DisplayName("Constructs a BinarySearchRequest with empty array")
    void constructsWithEmptyArray() {
        int[] sortedArray = {};
        int target = 3;
        assertThatThrownBy(() -> new BinarySearchRequest(sortedArray, target))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Array must have at least one element");
    }

    @Test
    @DisplayName("Constructs a BinarySearchRequest with valid array and target")
    void constructsWithValidArrayAndTarget() {
        int[] sortedArray = {1, 2, 3, 4, 5};
        int target = 3;
        BinarySearchRequest request = new BinarySearchRequest(sortedArray, target);
        assertThat(request).isEqualTo(new BinarySearchRequest(sortedArray, target));
        assertThat(request.hashCode()).isEqualTo(new BinarySearchRequest(sortedArray, target).hashCode());
        assertThat(request.toString()).isEqualTo(new BinarySearchRequest(sortedArray, target).toString());
    }

    @Test
    @DisplayName("Fails validation with null array")
    void failsValidationWithNullArray() {
        int[] sortedArray = null;
        int target = 3;
        var violations = validator.validate(new BinarySearchRequest(sortedArray, target));
        assertThat(violations).hasSize(1);
        var violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("Array must not be null");
    }

    @Test
    @DisplayName("Fails validation with empty array")
    void failsValidationWithEmptyArray() {
        int[] sortedArray = {};
        int target = 3;
        var violations = validator.validate(new BinarySearchRequest(sortedArray, target));
        assertThat(violations).hasSize(1);
        var violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("Array must have at least one element");
    }

    @Test
    @DisplayName("Passes validation with valid array and target")
    void passesValidationWithValidArrayAndTarget() {
        int[] sortedArray = {1, 2, 3, 4, 5};
        int target = 3;
        var violations = validator.validate(new BinarySearchRequest(sortedArray, target));
        assertThat(violations).isEmpty();
    }
}