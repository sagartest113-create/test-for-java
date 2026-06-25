package com.testcraft.demo.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChessBoardTest {

    @Test
    @DisplayName("Constructs ChessBoard from input")
    void testConstructFromInput() {
        String[][] input = {
                {"r", "n", "b", "q", "k", "b", "n", "r"},
                {"p", "p", "p", "p", "p", "p", "p", "p"},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"P", "P", "P", "P", "P", "P", "P", "P"},
                {"R", "N", "B", "Q", "K", "B", "N", "R"}
        };
        ChessBoard board = new ChessBoard(input);
        assertThat(board.getPiece(0, 0)).isEqualTo(ChessPiece.WHITE_ROOK);
        assertThat(board.getPiece(0, 1)).isEqualTo(ChessPiece.WHITE_KNIGHT);
        assertThat(board.getPiece(0, 7)).isEqualTo(ChessPiece.WHITE_ROOK);
    }

    @Test
    @DisplayName("Constructs ChessBoard from source")
    void testConstructFromSource() {
        ChessPiece[][] source = new ChessPiece[8][8];
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                source[r][c] = ChessPiece.WHITE_ROOK;
            }
        }
        ChessBoard board = new ChessBoard(source);
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                assertThat(board.getPiece(r, c)).isEqualTo(ChessPiece.WHITE_ROOK);
            }
        }
    }

    @Test
    @DisplayName("Makes move with promotion")
    void testMakeMoveWithPromotion() {
        ChessBoard board = new ChessBoard(new String[][]{{"r", "n", "b", "q", "k", "b", "n", "r"},
                {"p", "p", "p", "p", "p", "p", "p", "p"},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"P", "P", "P", "P", "P", "P", "P", "P"},
                {"R", "N", "B", "Q", "K", "B", "N", "R"}});
        ChessMove move = new ChessMove(0, 0, 3, 3, ChessPiece.WHITE_PAWN, ChessPiece.WHITE_QUEEN);
        board.makeMove(move);
        assertThat(board.getPiece(0, 0)).isEqualTo(ChessPiece.EMPTY);
        assertThat(board.getPiece(3, 3)).isEqualTo(ChessPiece.WHITE_QUEEN);
    }

    @Test
    @DisplayName("Undoes move")
    void testUndoMove() {
        ChessBoard board = new ChessBoard(new String[][]{{"r", "n", "b", "q", "k", "b", "n", "r"},
                {"p", "p", "p", "p", "p", "p", "p", "p"},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"P", "P", "P", "P", "P", "P", "P", "P"},
                {"R", "N", "B", "Q", "K", "B", "N", "R"}});
        ChessMove move = new ChessMove(0, 0, 3, 3, ChessPiece.WHITE_PAWN, ChessPiece.EMPTY);
        board.makeMove(move);
        board.undoMove(move);
        assertThat(board.getPiece(0, 0)).isEqualTo(ChessPiece.WHITE_PAWN);
        assertThat(board.getPiece(3, 3)).isEqualTo(ChessPiece.EMPTY);
    }

    @Test
    @DisplayName("Finds king")
    void testFindKing() {
        ChessBoard board = new ChessBoard(new String[][]{{"r", "n", "b", "q", "k", "b", "n", "r"},
                {"p", "p", "p", "p", "p", "p", "p", "p"},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"P", "P", "P", "P", "P", "P", "P", "P"},
                {"R", "N", "B", "Q", "K", "B", "N", "R"}});
        int[] kingPos = board.findKing(true);
        assertThat(kingPos).isEqualTo(new int[]{4, 4});
    }

    @Test
    @DisplayName("Is in check")
    void testIsInCheck() {
        ChessBoard board = new ChessBoard(new String[][]{{"r", "n", "b", "q", "k", "b", "n", "r"},
                {"p", "p", "p", "p", "p", "p", "p", "p"},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"P", "P", "P", "P", "P", "P", "P", "P"},
                {"R", "N", "B", "Q", "K", "B", "N", "R"}});
        assertThat(board.isInCheck(true)).isFalse();
        board.makeMove(new ChessMove(0, 0, 4, 4, ChessPiece.WHITE_PAWN, ChessPiece.EMPTY));
        assertThat(board.isInCheck(true)).isTrue();
    }

    @Test
    @DisplayName("Is square attacked")
    void testIsSquareAttacked() {
        ChessBoard board = new ChessBoard(new String[][]{{"r", "n", "b", "q", "k", "b", "n", "r"},
                {"p", "p", "p", "p", "p", "p", "p", "p"},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"P", "P", "P", "P", "P", "P", "P", "P"},
                {"R", "N", "B", "Q", "K", "B", "N", "R"}});
        assertThat(board.isSquareAttacked(4, 4, true)).isFalse();
        board.makeMove(new ChessMove(0, 0, 4, 4, ChessPiece.WHITE_PAWN, ChessPiece.EMPTY));
        assertThat(board.isSquareAttacked(4, 4, true)).isTrue();
    }

    @Test
    @DisplayName("Equals")
    void testEquals() {
        ChessBoard board1 = new ChessBoard(new String[][]{{"r", "n", "b", "q", "k", "b", "n", "r"},
                {"p", "p", "p", "p", "p", "p", "p", "p"},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"P", "P", "P", "P", "P", "P", "P", "P"},
                {"R", "N", "B", "Q", "K", "B", "N", "R"}});
        ChessBoard board2 = new ChessBoard(new String[][]{{"r", "n", "b", "q", "k", "b", "n", "r"},
                {"p", "p", "p", "p", "p", "p", "p", "p"},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"P", "P", "P", "P", "P", "P", "P", "P"},
                {"R", "N", "B", "Q", "K", "B", "N", "R"}});
        assertThat(board1).isEqualTo(board2);
    }

    @Test
    @DisplayName("Not equals")
    void testNotEquals() {
        ChessBoard board1 = new ChessBoard(new String[][]{{"r", "n", "b", "q", "k", "b", "n", "r"},
                {"p", "p", "p", "p", "p", "p", "p", "p"},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"P", "P", "P", "P", "P", "P", "P", "P"},
                {"R", "N", "B", "Q", "K", "B", "N", "R"}});
        ChessBoard board2 = new ChessBoard(new String[][]{{"r", "n", "b", "q", "k", "b", "n", "r"},
                {"p", "p", "p", "p", "p", "p", "p", "p"},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"P", "P", "P", "P", "P", "P", "P", "P"},
                {"R", "N", "B", "Q", "K", "B", "N", "Q"}});
        assertThat(board1).isNotEqualTo(board2);
    }

    @Test
    @DisplayName("Equals null")
    void testEqualsNull() {
        ChessBoard board = new ChessBoard(new String[][]{{"r", "n", "b", "q", "k", "b", "n", "r"},
                {"p", "p", "p", "p", "p", "p", "p", "p"},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"P", "P", "P", "P", "P", "P", "P", "P"},
                {"R", "N", "B", "Q", "K", "B", "N", "R"}});
        assertThat(board).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Not equals different class")
    void testNotEqualsDifferentClass() {
        ChessBoard board = new ChessBoard(new String[][]{{"r", "n", "b", "q", "k", "b", "n", "r"},
                {"p", "p", "p", "p", "p", "p", "p", "p"},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"P", "P", "P", "P", "P", "P", "P", "P"},
                {"R", "N", "B", "Q", "K", "B", "N", "R"}});
        assertThat(board).isNotEqualTo("not a board");
    }

    @Test
    @DisplayName("Copy")
    void testCopy() {
        ChessBoard board = new ChessBoard(new String[][]{{"r", "n", "b", "q", "k", "b", "n", "r"},
                {"p", "p", "p", "p", "p", "p", "p", "p"},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"P", "P", "P", "P", "P", "P", "P", "P"},
                {"R", "N", "B", "Q", "K", "B", "N", "R"}});
        ChessBoard copy = board.copy();
        assertThat(copy).isEqualTo(board);
    }

    @Test
    @DisplayName("Invalid row")
    void testInvalidRow() {
        ChessBoard board = new ChessBoard(new String[][]{{"r", "n", "b", "q", "k", "b", "n", "r"},
                {"p", "p", "p", "p", "p", "p", "p", "p"},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"P", "P", "P", "P", "P", "P", "P", "P"},
                {"R", "N", "B", "Q", "K", "B", "N", "R"}});
        assertThrows(IndexOutOfBoundsException.class, () -> board.getPiece(-1, 0));
    }

    @Test
    @DisplayName("Invalid column")
    void testInvalidColumn() {
        ChessBoard board = new ChessBoard(new String[][]{{"r", "n", "b", "q", "k", "b", "n", "r"},
                {"p", "p", "p", "p", "p", "p", "p", "p"},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"P", "P", "P", "P", "P", "P", "P", "P"},
                {"R", "N", "B", "Q", "K", "B", "N", "R"}});
        assertThrows(IndexOutOfBoundsException.class, () -> board.getPiece(0, -1));
    }

    @Test
    @DisplayName("Invalid row and column")
    void testInvalidRowAndColumn() {
        ChessBoard board = new ChessBoard(new String[][]{{"r", "n", "b", "q", "k", "b", "n", "r"},
                {"p", "p", "p", "p", "p", "p", "p", "p"},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"", "", "", "", "", "", "", ""},
                {"P", "P", "P", "P", "P", "P", "P", "P"},
                {"R", "N", "B", "Q", "K", "B", "N", "R"}});
        assertThrows(IndexOutOfBoundsException.class, () -> board.getPiece(-1, -1));
    }
}