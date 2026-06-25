package com.testcraft.demo.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GridRequestTest {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("Constructs a valid GridRequest with a 9x9 grid")
    void testValidGridRequest() {
        int[][] grid = new int[9][9];
        GridRequest request = new GridRequest(grid);
        assertThat(request.grid()).isEqualTo(grid);
    }

    @Test
    @DisplayName("Constructs a valid GridRequest with a 9x9 grid and accessor methods")
    void testValidGridRequestAccessorMethods() {
        int[][] grid = new int[9][9];
        GridRequest request = new GridRequest(grid);
        assertThat(request.grid()).isEqualTo(grid);
        assertThat(request.grid().length).isEqualTo(9);
        assertThat(request.grid()[0].length).isEqualTo(9);
    }

    @Test
    @DisplayName("Throws an exception when constructing a GridRequest with a null grid")
    void testNullGridRequest() {
        assertThatThrownBy(() -> new GridRequest(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Grid must not be null");
    }

    @Test
    @DisplayName("Throws an exception when constructing a GridRequest with a grid that is not 9x9")
    void testInvalidGridRequestSize() {
        int[][] grid = new int[10][9];
        assertThatThrownBy(() -> new GridRequest(grid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Grid must have exactly 9 rows");
    }

    @Test
    @DisplayName("Validates a GridRequest with a 9x9 grid")
    void testValidGridRequestValidation() {
        int[][] grid = new int[9][9];
        GridRequest request = new GridRequest(grid);
        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    @DisplayName("Fails validation when constructing a GridRequest with a null grid")
    void testNullGridRequestValidation() {
        GridRequest request = new GridRequest(null);
        assertThat(validator.validate(request)).isNotEmpty();
        ConstraintViolation<?> violation = validator.validate(request).iterator().next();
        assertThat(violation.getMessage()).isEqualTo("Grid must not be null");
    }

    @Test
    @DisplayName("Fails validation when constructing a GridRequest with a grid that is not 9x9")
    void testInvalidGridRequestSizeValidation() {
        int[][] grid = new int[10][9];
        GridRequest request = new GridRequest(grid);
        assertThat(validator.validate(request)).isNotEmpty();
        ConstraintViolation<?> violation = validator.validate(request).iterator().next();
        assertThat(violation.getMessage()).isEqualTo("Grid must have exactly 9 rows");
    }

    @Test
    @DisplayName("Equals returns true for two equal GridRequests")
    void testEquals() {
        int[][] grid1 = new int[9][9];
        int[][] grid2 = new int[9][9];
        GridRequest request1 = new GridRequest(grid1);
        GridRequest request2 = new GridRequest(grid2);
        assertThat(request1.equals(request2)).isTrue();
    }

    @Test
    @DisplayName("Equals returns false for two unequal GridRequests")
    void testEqualsUnequal() {
        int[][] grid1 = new int[9][9];
        int[][] grid2 = new int[8][9];
        GridRequest request1 = new GridRequest(grid1);
        GridRequest request2 = new GridRequest(grid2);
        assertThat(request1.equals(request2)).isFalse();
    }

    @Test
    @DisplayName("HashCode returns the same value for two equal GridRequests")
    void testHashCode() {
        int[][] grid1 = new int[9][9];
        int[][] grid2 = new int[9][9];
        GridRequest request1 = new GridRequest(grid1);
        GridRequest request2 = new GridRequest(grid2);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    @DisplayName("HashCode returns different values for two unequal GridRequests")
    void testHashCodeUnequal() {
        int[][] grid1 = new int[9][9];
        int[][] grid2 = new int[8][9];
        GridRequest request1 = new GridRequest(grid1);
        GridRequest request2 = new GridRequest(grid2);
        assertThat(request1.hashCode()).isNotEqualTo(request2.hashCode());
    }

    @Test
    @DisplayName("ToString returns a string representation of the GridRequest")
    void testToString() {
        int[][] grid = new int[9][9];
        GridRequest request = new GridRequest(grid);
        assertThat(request.toString()).isNotNull();
    }
}