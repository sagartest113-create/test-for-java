package com.testcraft.demo.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.assertj.core.api.Assertions;

import java.util.Set;

public class ChessMoveResponseTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("Constructs a ChessMoveResponse with valid data")
    void testConstructWithValidData() {
        ChessMoveResponse response = new ChessMoveResponse(
                1L,
                "e2",
                "e4",
                "King",
                "Pawn",
                100,
                0.5,
                true,
                "Checkmate"
        );
        Assertions.assertThat(response.analysisId()).isEqualTo(1L);
        Assertions.assertThat(response.from()).isEqualTo("e2");
        Assertions.assertThat(response.to()).isEqualTo("e4");
        Assertions.assertThat(response.piece()).isEqualTo("King");
        Assertions.assertThat(response.captured()).isEqualTo("Pawn");
        Assertions.assertThat(response.evaluationScore()).isEqualTo(100);
        Assertions.assertThat(response.winProbability()).isEqualTo(0.5);
        Assertions.assertThat(response.winnable()).isEqualTo(true);
        Assertions.assertThat(response.message()).isEqualTo("Checkmate");
    }

    @Test
    @DisplayName("Constructs a ChessMoveResponse with null analysisId")
    void testConstructWithNullAnalysisId() {
        Assertions.assertThatThrownBy(() -> new ChessMoveResponse(null, "e2", "e4", "King", "Pawn", 100, 0.5, true, "Checkmate"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Constructs a ChessMoveResponse with empty from")
    void testConstructWithEmptyFrom() {
        Assertions.assertThatThrownBy(() -> new ChessMoveResponse(1L, "", "e4", "King", "Pawn", 100, 0.5, true, "Checkmate"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Constructs a ChessMoveResponse with empty to")
    void testConstructWithEmptyTo() {
        Assertions.assertThatThrownBy(() -> new ChessMoveResponse(1L, "e2", "", "King", "Pawn", 100, 0.5, true, "Checkmate"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Constructs a ChessMoveResponse with empty piece")
    void testConstructWithEmptyPiece() {
        Assertions.assertThatThrownBy(() -> new ChessMoveResponse(1L, "e2", "e4", "", "Pawn", 100, 0.5, true, "Checkmate"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Constructs a ChessMoveResponse with empty captured")
    void testConstructWithEmptyCaptured() {
        Assertions.assertThatThrownBy(() -> new ChessMoveResponse(1L, "e2", "e4", "King", "", 100, 0.5, true, "Checkmate"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Constructs a ChessMoveResponse with negative evaluationScore")
    void testConstructWithNegativeEvaluationScore() {
        Assertions.assertThatThrownBy(() -> new ChessMoveResponse(1L, "e2", "e4", "King", "Pawn", -100, 0.5, true, "Checkmate"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Constructs a ChessMoveResponse with winProbability greater than 1")
    void testConstructWithWinProbabilityGreaterThan1() {
        Assertions.assertThatThrownBy(() -> new ChessMoveResponse(1L, "e2", "e4", "King", "Pawn", 100, 1.1, true, "Checkmate"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Constructs a ChessMoveResponse with winProbability less than 0")
    void testConstructWithWinProbabilityLessThan0() {
        Assertions.assertThatThrownBy(() -> new ChessMoveResponse(1L, "e2", "e4", "King", "Pawn", 100, -0.1, true, "Checkmate"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Constructs a ChessMoveResponse with winnable false and winProbability not 0")
    void testConstructWithWinnableFalseAndWinProbabilityNot0() {
        Assertions.assertThatThrownBy(() -> new ChessMoveResponse(1L, "e2", "e4", "King", "Pawn", 100, 0.5, false, "Checkmate"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Constructs a ChessMoveResponse with message null")
    void testConstructWithNullMessage() {
        ChessMoveResponse response = new ChessMoveResponse(
                1L,
                "e2",
                "e4",
                "King",
                "Pawn",
                100,
                0.5,
                true,
                null
        );
        Assertions.assertThat(response.message()).isNull();
    }

    @Test
    @DisplayName("Validates a ChessMoveResponse with valid data")
    void testValidateWithValidData() {
        ChessMoveResponse response = new ChessMoveResponse(
                1L,
                "e2",
                "e4",
                "King",
                "Pawn",
                100,
                0.5,
                true,
                "Checkmate"
        );
        Set<ConstraintViolation<ChessMoveResponse>> violations = validator.validate(response);
        Assertions.assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Validates a ChessMoveResponse with null analysisId")
    void testValidateWithNullAnalysisId() {
        Set<ConstraintViolation<ChessMoveResponse>> violations = validator.validate(new ChessMoveResponse(null, "e2", "e4", "King", "Pawn", 100, 0.5, true, "Checkmate"));
        Assertions.assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("Validates a ChessMoveResponse with empty from")
    void testValidateWithEmptyFrom() {
        Set<ConstraintViolation<ChessMoveResponse>> violations = validator.validate(new ChessMoveResponse(1L, "", "e4", "King", "Pawn", 100, 0.5, true, "Checkmate"));
        Assertions.assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("Validates a ChessMoveResponse with empty to")
    void testValidateWithEmptyTo() {
        Set<ConstraintViolation<ChessMoveResponse>> violations = validator.validate(new ChessMoveResponse(1L, "e2", "", "King", "Pawn", 100, 0.5, true, "Checkmate"));
        Assertions.assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("Validates a ChessMoveResponse with empty piece")
    void testValidateWithEmptyPiece() {
        Set<ConstraintViolation<ChessMoveResponse>> violations = validator.validate(new ChessMoveResponse(1L, "e2", "e4", "", "Pawn", 100, 0.5, true, "Checkmate"));
        Assertions.assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("Validates a ChessMoveResponse with empty captured")
    void testValidateWithEmptyCaptured() {
        Set<ConstraintViolation<ChessMoveResponse>> violations = validator.validate(new ChessMoveResponse(1L, "e2", "e4", "King", "", 100, 0.5, true, "Checkmate"));
        Assertions.assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("Validates a ChessMoveResponse with negative evaluationScore")
    void testValidateWithNegativeEvaluationScore() {
        Set<ConstraintViolation<ChessMoveResponse>> violations = validator.validate(new ChessMoveResponse(1L, "e2", "e4", "King", "Pawn", -100, 0.5, true, "Checkmate"));
        Assertions.assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("Validates a ChessMoveResponse with winProbability greater than 1")
    void testValidateWithWinProbabilityGreaterThan1() {
        Set<ConstraintViolation<ChessMoveResponse>> violations = validator.validate(new ChessMoveResponse(1L, "e2", "e4", "King", "Pawn", 100, 1.1, true, "Checkmate"));
        Assertions.assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("Validates a ChessMoveResponse with winProbability less than 0")
    void testValidateWithWinProbabilityLessThan0() {
        Set<ConstraintViolation<ChessMoveResponse>> violations = validator.validate(new ChessMoveResponse(1L, "e2", "e4", "King", "Pawn", 100, -0.1, true, "Checkmate"));
        Assertions.assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("Validates a ChessMoveResponse with winnable false and winProbability not 0")
    void testValidateWithWinnableFalseAndWinProbabilityNot0() {
        Set<ConstraintViolation<ChessMoveResponse>> violations = validator.validate(new ChessMoveResponse(1L, "e2", "e4", "King", "Pawn", 100, 0.5, false, "Checkmate"));
        Assertions.assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("Equals method returns true for two ChessMoveResponse instances with equal data")
    void testEqualsWithEqualData() {
        ChessMoveResponse response1 = new ChessMoveResponse(
                1L,
                "e2",
                "e4",
                "King",
                "Pawn",
                100,
                0.5,
                true,
                "Checkmate"
        );
        ChessMoveResponse response2 = new ChessMoveResponse(
                1L,
                "e2",
                "e4",
                "King",
                "Pawn",
                100,
                0.5,
                true,
                "Checkmate"
        );
        Assertions.assertThat(response1.equals(response2)).isTrue();
    }

    @Test
    @DisplayName("Equals method returns false for two ChessMoveResponse instances with different data")
    void testEqualsWithDifferentData() {
        ChessMoveResponse response1 = new ChessMoveResponse(
                1L,
                "e2",
                "e4",
                "King",
                "Pawn",
                100,
                0.5,
                true,
                "Checkmate"
        );
        ChessMoveResponse response2 = new ChessMoveResponse(
                2L,
                "e2",
                "e4",
                "King",
                "Pawn",
                100,
                0.5,
                true,
                "Checkmate"
        );
        Assertions.assertThat(response1.equals(response2)).isFalse();
    }

    @Test
    @DisplayName("HashCode method returns the same hash code for two ChessMoveResponse instances with equal data")
    void testHashCodeWithEqualData() {
        ChessMoveResponse response1 = new ChessMoveResponse(
                1L,
                "e2",
                "e4",
                "King",
                "Pawn",
                100,
                0.5,
                true,
                "Checkmate"
        );
        ChessMoveResponse response2 = new ChessMoveResponse(
                1L,
                "e2",
                "e4",
                "King",
                "Pawn",
                100,
                0.5,
                true,
                "Checkmate"
        );
        Assertions.assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    @DisplayName("HashCode method returns different hash codes for two ChessMoveResponse instances with different data")
    void testHashCodeWithDifferentData() {
        ChessMoveResponse response1 = new ChessMoveResponse(
                1L,
                "e2",
                "e4",
                "King",
                "Pawn",
                100,
                0.5,
                true,
                "Checkmate"
        );
        ChessMoveResponse response2 = new ChessMoveResponse(
                2L,
                "e2",
                "e4",
                "King",
                "Pawn",
                100,
                0.5,
                true,
                "Checkmate"
        );
        Assertions.assertThat(response1.hashCode()).isNotEqualTo(response2.hashCode());
    }

    @Test
    @DisplayName("ToString method returns a string representation of a ChessMoveResponse instance")
    void testToString() {
        ChessMoveResponse response = new ChessMoveResponse(
                1L,
                "e2",
                "e4",
                "King",
                "Pawn",
                100,
                0.5,
                true,
                "Checkmate"
        );
        Assertions.assertThat(response.toString()).isEqualTo("ChessMoveResponse[analysisId=1, from=e2, to=e4, piece=King, captured=Pawn, evaluationScore=100, winProbability=0.5, winnable=true, message=Checkmate]");
    }
}