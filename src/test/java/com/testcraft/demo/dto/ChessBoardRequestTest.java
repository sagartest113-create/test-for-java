package com.testcraft.demo.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ChessBoardRequestTest {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("Constructs ChessBoardRequest with valid board")
    void constructsWithValidBoard() {
        String[][] board = new String[8][8];
        ChessBoardRequest request = new ChessBoardRequest(board);
        assertThat(request.board()).isEqualTo(board);
    }

    @Test
    @DisplayName("Constructs ChessBoardRequest with null board throws NPE")
    void constructsWithNullBoardThrowsNPE() {
        assertThatThrownBy(() -> new ChessBoardRequest(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Board must not be null");
    }

    @Test
    @DisplayName("Constructs ChessBoardRequest with board of size 7 throws ConstraintViolation")
    void constructsWithBoardOfSize7ThrowsConstraintViolation() {
        String[][] board = new String[7][8];
        assertThatThrownBy(() -> new ChessBoardRequest(board))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("Board must have exactly 8 rows");
    }

    @Test
    @DisplayName("Constructs ChessBoardRequest with board of size 9 throws ConstraintViolation")
    void constructsWithBoardOfSize9ThrowsConstraintViolation() {
        String[][] board = new String[9][8];
        assertThatThrownBy(() -> new ChessBoardRequest(board))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("Board must have exactly 8 rows");
    }

    @Test
    @DisplayName("Validates ChessBoardRequest with valid board")
    void validatesWithValidBoard() {
        String[][] board = new String[8][8];
        ChessBoardRequest request = new ChessBoardRequest(board);
        ConstraintViolation<?>[] violations = validator.validate(request).toArray();
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Validates ChessBoardRequest with null board")
    void validatesWithNullBoard() {
        ChessBoardRequest request = new ChessBoardRequest(null);
        ConstraintViolation<?>[] violations = validator.validate(request).toArray();
        assertThat(violations).hasSize(1);
        assertThat(violations[0].getMessage()).isEqualTo("Board must not be null");
    }

    @Test
    @DisplayName("Validates ChessBoardRequest with board of size 7")
    void validatesWithBoardOfSize7() {
        String[][] board = new String[7][8];
        ChessBoardRequest request = new ChessBoardRequest(board);
        ConstraintViolation<?>[] violations = validator.validate(request).toArray();
        assertThat(violations).hasSize(1);
        assertThat(violations[0].getMessage()).isEqualTo("Board must have exactly 8 rows");
    }

    @Test
    @DisplayName("Validates ChessBoardRequest with board of size 9")
    void validatesWithBoardOfSize9() {
        String[][] board = new String[9][8];
        ChessBoardRequest request = new ChessBoardRequest(board);
        ConstraintViolation<?>[] violations = validator.validate(request).toArray();
        assertThat(violations).hasSize(1);
        assertThat(violations[0].getMessage()).isEqualTo("Board must have exactly 8 rows");
    }
}