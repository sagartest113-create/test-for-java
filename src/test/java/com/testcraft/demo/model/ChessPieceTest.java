package com.testcraft.demo.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;

public class ChessPieceTest {

    @Test
    @DisplayName("All enum constants exist")
    void testEnumConstantsExist() {
        assertThat(ChessPiece.values()).isNotNull().hasSize(14);
    }

    @Test
    @DisplayName("Enum constants have correct properties")
    void testEnumConstantsProperties() {
        for (ChessPiece piece : ChessPiece.values()) {
            assertThat(piece.getSymbol()).isNotNull();
            assertThat(piece.getValue()).isGreaterThan(0);
            if (piece == ChessPiece.EMPTY) {
                assertThat(piece.isWhite()).isFalse();
                assertThat(piece.isBlack()).isFalse();
                assertThat(piece.isEmpty()).isTrue();
            } else {
                assertThat(piece.isWhite()).isTrue();
                assertThat(piece.isBlack()).isFalse();
                assertThat(piece.isEmpty()).isFalse();
            }
        }
    }

    @Test
    @DisplayName("fromSymbol returns EMPTY for empty input")
    void testFromSymbol_EmptyInput() {
        assertThat(ChessPiece.fromSymbol(null)).isEqualTo(ChessPiece.EMPTY);
        assertThat(ChessPiece.fromSymbol("")).isEqualTo(ChessPiece.EMPTY);
        assertThat(ChessPiece.fromSymbol(".")).isEqualTo(ChessPiece.EMPTY);
    }

    @Test
    @DisplayName("fromSymbol returns EMPTY for unknown symbol")
    void testFromSymbol_UnknownSymbol() {
        assertThatThrownBy(() -> ChessPiece.fromSymbol("X")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("fromSymbol returns correct piece for known symbol")
    void testFromSymbol_KnownSymbol() {
        for (ChessPiece piece : ChessPiece.values()) {
            if (piece != ChessPiece.EMPTY) {
                assertThat(ChessPiece.fromSymbol(String.valueOf(piece.getSymbol()))).isEqualTo(piece);
            }
        }
    }
}