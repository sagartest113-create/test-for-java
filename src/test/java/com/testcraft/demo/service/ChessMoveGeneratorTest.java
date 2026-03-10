package com.testcraft.demo.service;

import com.testcraft.demo.model.ChessBoard;
import com.testcraft.demo.model.ChessMove;
import com.testcraft.demo.model.ChessPiece;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChessMoveGeneratorTest {

    @Mock
    private ChessBoard board;

    @Mock
    private ChessPiece piece;

    @InjectMocks
    private ChessMoveGenerator generator;

    @Test
    @DisplayName("generateLegalMoves returns empty list when board is empty")
    void testGenerateLegalMoves_EmptyBoard() {
        when(board.getPiece(0, 0)).thenReturn(ChessPiece.EMPTY);
        when(board.getPiece(1, 1)).thenReturn(ChessPiece.EMPTY);
        when(board.getPiece(2, 2)).thenReturn(ChessPiece.EMPTY);

        List<ChessMove> moves = generator.generateLegalMoves(board, true);
        assertThat(moves).isEmpty();
    }

    @Test
    @DisplayName("generateLegalMoves returns empty list when no moves are legal")
    void testGenerateLegalMoves_NoLegalMoves() {
        when(board.getPiece(0, 0)).thenReturn(ChessPiece.WHITE_PAWN);
        when(board.getPiece(1, 1)).thenReturn(ChessPiece.BLACK_PAWN);
        when(board.getPiece(2, 2)).thenReturn(ChessPiece.EMPTY);

        List<ChessMove> moves = generator.generateLegalMoves(board, true);
        assertThat(moves).isEmpty();
    }

    @Test
    @DisplayName("generateLegalMoves returns list of legal moves")
    void testGenerateLegalMoves_LegalMoves() {
        when(board.getPiece(0, 0)).thenReturn(ChessPiece.WHITE_PAWN);
        when(board.getPiece(1, 1)).thenReturn(ChessPiece.BLACK_PAWN);
        when(board.getPiece(2, 2)).thenReturn(ChessPiece.EMPTY);

        List<ChessMove> moves = generator.generateLegalMoves(board, true);
        assertThat(moves).isNotEmpty();
    }

    @Test
    @DisplayName("generatePseudoLegalMoves returns empty list when board is empty")
    void testGeneratePseudoLegalMoves_EmptyBoard() {
        when(board.getPiece(0, 0)).thenReturn(ChessPiece.EMPTY);
        when(board.getPiece(1, 1)).thenReturn(ChessPiece.EMPTY);
        when(board.getPiece(2, 2)).thenReturn(ChessPiece.EMPTY);

        List<ChessMove> moves = generator.generatePseudoLegalMoves(board, true);
        assertThat(moves).isEmpty();
    }

    @Test
    @DisplayName("generatePseudoLegalMoves returns empty list when no moves are pseudo-legal")
    void testGeneratePseudoLegalMoves_NoPseudoLegalMoves() {
        when(board.getPiece(0, 0)).thenReturn(ChessPiece.WHITE_PAWN);
        when(board.getPiece(1, 1)).thenReturn(ChessPiece.BLACK_PAWN);
        when(board.getPiece(2, 2)).thenReturn(ChessPiece.EMPTY);

        List<ChessMove> moves = generator.generatePseudoLegalMoves(board, true);
        assertThat(moves).isEmpty();
    }

    @Test
    @DisplayName("generatePseudoLegalMoves returns list of pseudo-legal moves")
    void testGeneratePseudoLegalMoves_PseudoLegalMoves() {
        when(board.getPiece(0, 0)).thenReturn(ChessPiece.WHITE_PAWN);
        when(board.getPiece(1, 1)).thenReturn(ChessPiece.BLACK_PAWN);
        when(board.getPiece(2, 2)).thenReturn(ChessPiece.EMPTY);

        List<ChessMove> moves = generator.generatePseudoLegalMoves(board, true);
        assertThat(moves).isNotEmpty();
    }

    @Test
    @DisplayName("generatePieceMoves generates moves for pawn")
    void testGeneratePieceMoves_Pawn() {
        when(board.getPiece(0, 0)).thenReturn(ChessPiece.WHITE_PAWN);
        when(board.getPiece(1, 1)).thenReturn(ChessPiece.BLACK_PAWN);
        when(board.getPiece(2, 2)).thenReturn(ChessPiece.EMPTY);

        List<ChessMove> moves = new ArrayList<>();
        generator.generatePieceMoves(board, 0, 0, piece, moves);
        assertThat(moves).isNotEmpty();
    }

    @Test
    @DisplayName("generatePieceMoves generates moves for knight")
    void testGeneratePieceMoves_Knight() {
        when(board.getPiece(0, 0)).thenReturn(ChessPiece.WHITE_KNIGHT);
        when(board.getPiece(1, 1)).thenReturn(ChessPiece.BLACK_PAWN);
        when(board.getPiece(2, 2)).thenReturn(ChessPiece.EMPTY);

        List<ChessMove> moves = new ArrayList<>();
        generator.generatePieceMoves(board, 0, 0, piece, moves);
        assertThat(moves).isNotEmpty();
    }

    @Test
    @DisplayName("generatePieceMoves generates moves for bishop")
    void testGeneratePieceMoves_Bishop() {
        when(board.getPiece(0, 0)).thenReturn(ChessPiece.WHITE_BISHOP);
        when(board.getPiece(1, 1)).thenReturn(ChessPiece.BLACK_PAWN);
        when(board.getPiece(2, 2)).thenReturn(ChessPiece.EMPTY);

        List<ChessMove> moves = new ArrayList<>();
        generator.generatePieceMoves(board, 0, 0, piece, moves);
        assertThat(moves).isNotEmpty();
    }

    @Test
    @DisplayName("generatePieceMoves generates moves for rook")
    void testGeneratePieceMoves_Rook() {
        when(board.getPiece(0, 0)).thenReturn(ChessPiece.WHITE_ROOK);
        when(board.getPiece(1, 1)).thenReturn(ChessPiece.BLACK_PAWN);
        when(board.getPiece(2, 2)).thenReturn(ChessPiece.EMPTY);

        List<ChessMove> moves = new ArrayList<>();
        generator.generatePieceMoves(board, 0, 0, piece, moves);
        assertThat(moves).isNotEmpty();
    }

    @Test
    @DisplayName("generatePieceMoves generates moves for queen")
    void testGeneratePieceMoves_Queen() {
        when(board.getPiece(0, 0)).thenReturn(ChessPiece.WHITE_QUEEN);
        when(board.getPiece(1, 1)).thenReturn(ChessPiece.BLACK_PAWN);
        when(board.getPiece(2, 2)).thenReturn(ChessPiece.EMPTY);

        List<ChessMove> moves = new ArrayList<>();
        generator.generatePieceMoves(board, 0, 0, piece, moves);
        assertThat(moves).isNotEmpty();
    }

    @Test
    @DisplayName("generatePieceMoves generates moves for king")
    void testGeneratePieceMoves_King() {
        when(board.getPiece(0, 0)).thenReturn(ChessPiece.WHITE_KING);
        when(board.getPiece(1, 1)).thenReturn(ChessPiece.BLACK_PAWN);
        when(board.getPiece(2, 2)).thenReturn(ChessPiece.EMPTY);

        List<ChessMove> moves = new ArrayList<>();
        generator.generatePieceMoves(board, 0, 0, piece, moves);
        assertThat(moves).isNotEmpty();
    }

    @Test
    @DisplayName("generatePawnMoves generates moves for pawn")
    void testGeneratePawnMoves() {
        when(board.getPiece(0, 0)).thenReturn(ChessPiece.WHITE_PAWN);
        when(board.getPiece(1, 1)).thenReturn(ChessPiece.BLACK_PAWN);
        when(board.getPiece(2, 2)).thenReturn(ChessPiece.EMPTY);

        List<ChessMove> moves = new ArrayList<>();
        generator.generatePawnMoves(board, 0, 0, piece, moves);
        assertThat(moves).isNotEmpty();
    }

    @Test
    @DisplayName("generateLeapMoves generates moves for knight")
    void testGenerateLeapMoves() {
        when(board.getPiece(0, 0)).thenReturn(ChessPiece.WHITE_KNIGHT);
        when(board.getPiece(1, 1)).thenReturn(ChessPiece.BLACK_PAWN);
        when(board.getPiece(2, 2)).thenReturn(ChessPiece.EMPTY);

        List<ChessMove> moves = new ArrayList<>();
        generator.generateLeapMoves(board, 0, 0, piece, ChessMoveGenerator.KNIGHT_OFFSETS, moves);
        assertThat(moves).isNotEmpty();
    }

    @Test
    @DisplayName("generateSlidingMoves generates moves for bishop")
    void testGenerateSlidingMoves_Bishop() {
        when(board.getPiece(0, 0)).thenReturn(ChessPiece.WHITE_BISHOP);
        when(board.getPiece(1, 1)).thenReturn(ChessPiece.BLACK_PAWN);
        when(board.getPiece(2, 2)).thenReturn(ChessPiece.EMPTY);

        List<ChessMove> moves = new ArrayList<>();
        generator.generateSlidingMoves(board, 0, 0, piece, ChessMoveGenerator.DIAGONAL, moves);
        assertThat(moves).isNotEmpty();
    }

    @Test
    @DisplayName("generateSlidingMoves generates moves for rook")
    void testGenerateSlidingMoves_Rook() {
        when(board.getPiece(0, 0)).thenReturn(ChessPiece.WHITE_ROOK);
        when(board.getPiece(1, 1)).thenReturn(ChessPiece.BLACK_PAWN);
        when(board.getPiece(2, 2)).thenReturn(ChessPiece.EMPTY);

        List<ChessMove> moves = new ArrayList<>();
        generator.generateSlidingMoves(board, 0, 0, piece, ChessMoveGenerator.STRAIGHT, moves);
        assertThat(moves).isNotEmpty();
    }

    @Test
    @DisplayName("generateSlidingMoves generates moves for queen")
    void testGenerateSlidingMoves_Queen() {
        when(board.getPiece(0, 0)).thenReturn(ChessPiece.WHITE_QUEEN);
        when(board.getPiece(1, 1)).thenReturn(ChessPiece.BLACK_PAWN);
        when(board.getPiece(2, 2)).thenReturn(ChessPiece.EMPTY);

        List<ChessMove> moves = new ArrayList<>();
        generator.generateSlidingMoves(board, 0, 0, piece, ChessMoveGenerator.STRAIGHT, moves);
        assertThat(moves).isNotEmpty();
        generator.generateSlidingMoves(board, 0, 0, piece, ChessMoveGenerator.DIAGONAL, moves);
        assertThat(moves).isNotEmpty();
    }

    @Test
    @DisplayName("generateKingMoves generates moves for king")
    void testGenerateKingMoves() {
        when(board.getPiece(0, 0)).thenReturn(ChessPiece.WHITE_KING);
        when(board.getPiece(1, 1)).thenReturn(ChessPiece.BLACK_PAWN);
        when(board.getPiece(2, 2)).thenReturn(ChessPiece.EMPTY);

        List<ChessMove> moves = new ArrayList<>();
        generator.generateKingMoves(board, 0, 0, piece, moves);
        assertThat(moves).isNotEmpty();
    }

    @Test
    @DisplayName("addPawnMove adds pawn move with promotion")
    void testAddPawnMove_Promotion() {
        ChessMove move = new ChessMove(0, 0, 1, 1, ChessPiece.WHITE_PAWN, ChessPiece.BLACK_PAWN, ChessPiece.WHITE_QUEEN);
        List<ChessMove> moves = new ArrayList<>();
        generator.addPawnMove(moves, 0, 0, 1, 1, ChessPiece.WHITE_PAWN, ChessPiece.BLACK_PAWN, 0, ChessPiece.WHITE_QUEEN);
        assertThat(moves).containsExactly(move);
    }

    @Test
    @DisplayName("addPawnMove adds pawn move without promotion")
    void testAddPawnMove_NoPromotion() {
        ChessMove move = new ChessMove(0, 0, 1, 1, ChessPiece.WHITE_PAWN, ChessPiece.BLACK_PAWN, null);
        List<ChessMove> moves = new ArrayList<>();
        generator.addPawnMove(moves, 0, 0, 1, 1, ChessPiece.WHITE_PAWN, ChessPiece.BLACK_PAWN, 0, ChessPiece.WHITE_QUEEN);
        assertThat(moves).containsExactly(move);
    }
}