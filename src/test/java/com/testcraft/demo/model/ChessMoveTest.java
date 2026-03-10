package com.testcraft.demo.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChessMoveTest {

    @Test
    @DisplayName("Construct ChessMove with all parameters")
    void testConstructChessMove() {
        ChessPiece piece = new ChessPiece();
        ChessPiece captured = new ChessPiece();
        ChessPiece promotedTo = new ChessPiece();
        ChessMove move = new ChessMove(1, 2, 3, 4, piece, captured, promotedTo);
        assertThat(move.getFromRow()).isEqualTo(1);
        assertThat(move.getFromCol()).isEqualTo(2);
        assertThat(move.getToRow()).isEqualTo(3);
        assertThat(move.getToCol()).isEqualTo(4);
        assertThat(move.getPiece()).isEqualTo(piece);
        assertThat(move.getCaptured()).isEqualTo(captured);
        assertThat(move.getPromotedTo()).isEqualTo(promotedTo);
    }

    @Test
    @DisplayName("Construct ChessMove with null parameters")
    void testConstructChessMoveWithNull() {
        assertThrows(NullPointerException.class, () -> new ChessMove(1, 2, 3, 4, null, null, null));
        assertThrows(NullPointerException.class, () -> new ChessMove(1, 2, 3, 4, new ChessPiece(), null, null));
        assertThrows(NullPointerException.class, () -> new ChessMove(1, 2, 3, 4, new ChessPiece(), new ChessPiece(), null));
    }

    @Test
    @DisplayName("Getters return correct values")
    void testGetters() {
        ChessPiece piece = new ChessPiece();
        ChessPiece captured = new ChessPiece();
        ChessPiece promotedTo = new ChessPiece();
        ChessMove move = new ChessMove(1, 2, 3, 4, piece, captured, promotedTo);
        assertThat(move.getFromRow()).isEqualTo(1);
        assertThat(move.getFromCol()).isEqualTo(2);
        assertThat(move.getToRow()).isEqualTo(3);
        assertThat(move.getToCol()).isEqualTo(4);
        assertThat(move.getPiece()).isEqualTo(piece);
        assertThat(move.getCaptured()).isEqualTo(captured);
        assertThat(move.getPromotedTo()).isEqualTo(promotedTo);
    }

    @Test
    @DisplayName("Equals returns true for equal objects")
    void testEqualsEqualObjects() {
        ChessPiece piece = new ChessPiece();
        ChessPiece captured = new ChessPiece();
        ChessPiece promotedTo = new ChessPiece();
        ChessMove move1 = new ChessMove(1, 2, 3, 4, piece, captured, promotedTo);
        ChessMove move2 = new ChessMove(1, 2, 3, 4, piece, captured, promotedTo);
        assertThat(move1.equals(move2)).isTrue();
    }

    @Test
    @DisplayName("Equals returns false for unequal objects")
    void testEqualsUnequalObjects() {
        ChessPiece piece = new ChessPiece();
        ChessPiece captured = new ChessPiece();
        ChessPiece promotedTo = new ChessPiece();
        ChessMove move1 = new ChessMove(1, 2, 3, 4, piece, captured, promotedTo);
        ChessMove move2 = new ChessMove(5, 6, 7, 8, piece, captured, promotedTo);
        assertThat(move1.equals(move2)).isFalse();
    }

    @Test
    @DisplayName("Equals returns false for null object")
    void testEqualsNullObject() {
        ChessPiece piece = new ChessPiece();
        ChessPiece captured = new ChessPiece();
        ChessPiece promotedTo = new ChessPiece();
        ChessMove move = new ChessMove(1, 2, 3, 4, piece, captured, promotedTo);
        assertThat(move.equals(null)).isFalse();
    }

    @Test
    @DisplayName("Equals returns false for different class")
    void testEqualsDifferentClass() {
        ChessPiece piece = new ChessPiece();
        ChessPiece captured = new ChessPiece();
        ChessPiece promotedTo = new ChessPiece();
        ChessMove move = new ChessMove(1, 2, 3, 4, piece, captured, promotedTo);
        assertThat(move.equals("")).isFalse();
    }

    @Test
    @DisplayName("HashCode returns correct value")
    void testHashCode() {
        ChessPiece piece = new ChessPiece();
        ChessPiece captured = new ChessPiece();
        ChessPiece promotedTo = new ChessPiece();
        ChessMove move = new ChessMove(1, 2, 3, 4, piece, captured, promotedTo);
        assertThat(move.hashCode()).isEqualTo(Objects.hash(1, 2, 3, 4, piece));
    }

    @Test
    @DisplayName("ToString returns correct string")
    void testToString() {
        ChessPiece piece = new ChessPiece();
        ChessPiece captured = new ChessPiece();
        ChessPiece promotedTo = new ChessPiece();
        ChessMove move = new ChessMove(1, 2, 3, 4, piece, captured, promotedTo);
        assertThat(move.toString()).isEqualTo("a2c4");
    }
}