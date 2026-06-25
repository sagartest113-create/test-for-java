package com.testcraft.demo.dto;

import com.testcraft.demo.model.Cell;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ShortestPathResponseTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("Constructs ShortestPathResponse with valid inputs")
    void testConstructWithValidInputs() {
        Long computationId = 1L;
        List<Cell> path = List.of(new Cell(1, 1), new Cell(2, 2));
        int totalCost = 10;
        boolean reachable = true;

        ShortestPathResponse response = new ShortestPathResponse(computationId, path, totalCost, reachable);

        assertThat(response.computationId()).isEqualTo(computationId);
        assertThat(response.path()).isEqualTo(path);
        assertThat(response.totalCost()).isEqualTo(totalCost);
        assertThat(response.reachable()).isEqualTo(reachable);
    }

    @Test
    @DisplayName("Constructs ShortestPathResponse with null computationId")
    void testConstructWithNullComputationId() {
        assertThrows(NullPointerException.class, () -> new ShortestPathResponse(null, List.of(), 10, true));
    }

    @Test
    @DisplayName("Constructs ShortestPathResponse with null path")
    void testConstructWithNullPath() {
        assertThrows(NullPointerException.class, () -> new ShortestPathResponse(1L, null, 10, true));
    }

    @Test
    @DisplayName("Constructs ShortestPathResponse with null totalCost")
    void testConstructWithNullTotalCost() {
        assertThrows(NullPointerException.class, () -> new ShortestPathResponse(1L, List.of(), null, true));
    }

    @Test
    @DisplayName("Constructs ShortestPathResponse with null reachable")
    void testConstructWithNullReachable() {
        assertThrows(NullPointerException.class, () -> new ShortestPathResponse(1L, List.of(), 10, null));
    }

    @Test
    @DisplayName("Equals method returns true for equal ShortestPathResponse instances")
    void testEqualsEqualInstances() {
        Long computationId = 1L;
        List<Cell> path = List.of(new Cell(1, 1), new Cell(2, 2));
        int totalCost = 10;
        boolean reachable = true;

        ShortestPathResponse response1 = new ShortestPathResponse(computationId, path, totalCost, reachable);
        ShortestPathResponse response2 = new ShortestPathResponse(computationId, path, totalCost, reachable);

        assertThat(response1.equals(response2)).isTrue();
    }

    @Test
    @DisplayName("Equals method returns false for unequal ShortestPathResponse instances")
    void testEqualsUnequalInstances() {
        Long computationId = 1L;
        List<Cell> path = List.of(new Cell(1, 1), new Cell(2, 2));
        int totalCost = 10;
        boolean reachable = true;

        ShortestPathResponse response1 = new ShortestPathResponse(computationId, path, totalCost, reachable);
        ShortestPathResponse response2 = new ShortestPathResponse(2L, path, totalCost, reachable);

        assertThat(response1.equals(response2)).isFalse();
    }

    @Test
    @DisplayName("HashCode method returns equal hash codes for equal ShortestPathResponse instances")
    void testHashCodeEqualInstances() {
        Long computationId = 1L;
        List<Cell> path = List.of(new Cell(1, 1), new Cell(2, 2));
        int totalCost = 10;
        boolean reachable = true;

        ShortestPathResponse response1 = new ShortestPathResponse(computationId, path, totalCost, reachable);
        ShortestPathResponse response2 = new ShortestPathResponse(computationId, path, totalCost, reachable);

        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    @DisplayName("HashCode method returns unequal hash codes for unequal ShortestPathResponse instances")
    void testHashCodeUnequalInstances() {
        Long computationId = 1L;
        List<Cell> path = List.of(new Cell(1, 1), new Cell(2, 2));
        int totalCost = 10;
        boolean reachable = true;

        ShortestPathResponse response1 = new ShortestPathResponse(computationId, path, totalCost, reachable);
        ShortestPathResponse response2 = new ShortestPathResponse(2L, path, totalCost, reachable);

        assertThat(response1.hashCode()).isNotEqualTo(response2.hashCode());
    }

    @Test
    @DisplayName("ToString method returns a valid string representation of ShortestPathResponse")
    void testToString() {
        Long computationId = 1L;
        List<Cell> path = List.of(new Cell(1, 1), new Cell(2, 2));
        int totalCost = 10;
        boolean reachable = true;

        ShortestPathResponse response = new ShortestPathResponse(computationId, path, totalCost, reachable);

        assertThat(response.toString()).contains("ShortestPathResponse[computationId=" + computationId + ", path=" + path + ", totalCost=" + totalCost + ", reachable=" + reachable + "]");
    }

    @Test
    @DisplayName("Validator detects invalid computationId")
    void testValidatorInvalidComputationId() {
        Long computationId = null;
        List<Cell> path = List.of(new Cell(1, 1), new Cell(2, 2));
        int totalCost = 10;
        boolean reachable = true;

        ShortestPathResponse response = new ShortestPathResponse(computationId, path, totalCost, reachable);

        Set<ConstraintViolation<ShortestPathResponse>> violations = validator.validate(response);

        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("Validator detects invalid path")
    void testValidatorInvalidPath() {
        Long computationId = 1L;
        List<Cell> path = null;
        int totalCost = 10;
        boolean reachable = true;

        ShortestPathResponse response = new ShortestPathResponse(computationId, path, totalCost, reachable);

        Set<ConstraintViolation<ShortestPathResponse>> violations = validator.validate(response);

        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("Validator detects invalid totalCost")
    void testValidatorInvalidTotalCost() {
        Long computationId = 1L;
        List<Cell> path = List.of(new Cell(1, 1), new Cell(2, 2));
        int totalCost = -1;
        boolean reachable = true;

        ShortestPathResponse response = new ShortestPathResponse(computationId, path, totalCost, reachable);

        Set<ConstraintViolation<ShortestPathResponse>> violations = validator.validate(response);

        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("Validator detects invalid reachable")
    void testValidatorInvalidReachable() {
        Long computationId = 1L;
        List<Cell> path = List.of(new Cell(1, 1), new Cell(2, 2));
        int totalCost = 10;
        boolean reachable = null;

        ShortestPathResponse response = new ShortestPathResponse(computationId, path, totalCost, reachable);

        Set<ConstraintViolation<ShortestPathResponse>> violations = validator.validate(response);

        assertThat(violations).hasSize(1);
    }
}