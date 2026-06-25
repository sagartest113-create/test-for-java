package com.testcraft.demo.service;

import com.testcraft.demo.model.ChessBoard;
import com.testcraft.demo.model.ChessMove;
import com.testcraft.demo.model.ChessPiece;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChessMoveGenerator {

    private static final int SIZE = 8;

    private static final int[][] KNIGHT_OFFSETS = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
    };
    private static final int[][] STRAIGHT = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    private static final int[][] DIAGONAL = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

    public List<ChessMove> generateLegalMoves(ChessBoard board, boolean white) {
        List<ChessMove> pseudoLegal = generatePseudoLegalMoves(board, white);
        List<ChessMove> legal = new ArrayList<>();

        for (ChessMove move : pseudoLegal) {
            board.makeMove(move);
            if (!board.isInCheck(white)) {
                legal.add(move);
            }
            board.undoMove(move);
        }
        return legal;
    }

    private List<ChessMove> generatePseudoLegalMoves(ChessBoard board, boolean white) {
        List<ChessMove> moves = new ArrayList<>();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                ChessPiece piece = board.getPiece(r, c);
                if (piece.isEmpty() || piece.isWhite() != white) {
                    continue;
                }
                generatePieceMoves(board, r, c, piece, moves);
            }
        }
        return moves;
    }

    private void generatePieceMoves(ChessBoard board, int row, int col,
                                    ChessPiece piece, List<ChessMove> moves) {
        switch (piece) {
            case WHITE_PAWN, BLACK_PAWN -> generatePawnMoves(board, row, col, piece, moves);
            case WHITE_KNIGHT, BLACK_KNIGHT -> generateLeapMoves(board, row, col, piece, KNIGHT_OFFSETS, moves);
            case WHITE_BISHOP, BLACK_BISHOP -> generateSlidingMoves(board, row, col, piece, DIAGONAL, moves);
            case WHITE_ROOK, BLACK_ROOK -> generateSlidingMoves(board, row, col, piece, STRAIGHT, moves);
            case WHITE_QUEEN, BLACK_QUEEN -> {
                generateSlidingMoves(board, row, col, piece, STRAIGHT, moves);
                generateSlidingMoves(board, row, col, piece, DIAGONAL, moves);
            }
            case WHITE_KING, BLACK_KING -> generateKingMoves(board, row, col, piece, moves);
            default -> { /* EMPTY */ }
        }
    }

    private void generatePawnMoves(ChessBoard board, int row, int col,
                                   ChessPiece piece, List<ChessMove> moves) {
        boolean white = piece.isWhite();
        int dir = white ? -1 : 1;
        int startRow = white ? 6 : 1;
        int promoRow = white ? 0 : 7;
        ChessPiece promoQueen = white ? ChessPiece.WHITE_QUEEN : ChessPiece.BLACK_QUEEN;

        int fwdRow = row + dir;
        if (ChessBoard.isValid(fwdRow, col) && board.getPiece(fwdRow, col).isEmpty()) {
            addPawnMove(moves, row, col, fwdRow, col, piece, ChessPiece.EMPTY, promoRow, promoQueen);

            int fwd2Row = row + 2 * dir;
            if (row == startRow && board.getPiece(fwd2Row, col).isEmpty()) {
                moves.add(new ChessMove(row, col, fwd2Row, col, piece, ChessPiece.EMPTY, null));
            }
        }

        for (int dc : new int[]{-1, 1}) {
            int nc = col + dc;
            if (!ChessBoard.isValid(fwdRow, nc)) {
                continue;
            }
            ChessPiece target = board.getPiece(fwdRow, nc);
            if (!target.isEmpty() && target.isWhite() != white) {
                addPawnMove(moves, row, col, fwdRow, nc, piece, target, promoRow, promoQueen);
            }
        }
    }

    private void addPawnMove(List<ChessMove> moves, int fromRow, int fromCol,
                             int toRow, int toCol, ChessPiece piece,
                             ChessPiece captured, int promoRow, ChessPiece promoQueen) {
        if (toRow == promoRow) {
            moves.add(new ChessMove(fromRow, fromCol, toRow, toCol, piece, captured, promoQueen));
        } else {
            moves.add(new ChessMove(fromRow, fromCol, toRow, toCol, piece, captured, null));
        }
    }

    private void generateLeapMoves(ChessBoard board, int row, int col,
                                   ChessPiece piece, int[][] offsets, List<ChessMove> moves) {
        boolean white = piece.isWhite();
        for (int[] offset : offsets) {
            int nr = row + offset[0];
            int nc = col + offset[1];
            if (!ChessBoard.isValid(nr, nc)) {
                continue;
            }
            ChessPiece target = board.getPiece(nr, nc);
            if (target.isEmpty() || target.isWhite() != white) {
                moves.add(new ChessMove(row, col, nr, nc, piece, target, null));
            }
        }
    }

    private void generateSlidingMoves(ChessBoard board, int row, int col,
                                      ChessPiece piece, int[][] directions, List<ChessMove> moves) {
        boolean white = piece.isWhite();
        for (int[] dir : directions) {
            for (int i = 1; i < SIZE; i++) {
                int nr = row + dir[0] * i;
                int nc = col + dir[1] * i;
                if (!ChessBoard.isValid(nr, nc)) {
                    break;
                }
                ChessPiece target = board.getPiece(nr, nc);
                if (target.isEmpty()) {
                    moves.add(new ChessMove(row, col, nr, nc, piece, ChessPiece.EMPTY, null));
                } else {
                    if (target.isWhite() != white) {
                        moves.add(new ChessMove(row, col, nr, nc, piece, target, null));
                    }
                    break;
                }
            }
        }
    }

    private void generateKingMoves(ChessBoard board, int row, int col,
                                   ChessPiece piece, List<ChessMove> moves) {
        boolean white = piece.isWhite();
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int nr = row + dr;
                int nc = col + dc;
                if (!ChessBoard.isValid(nr, nc)) {
                    continue;
                }
                ChessPiece target = board.getPiece(nr, nc);
                if (target.isEmpty() || target.isWhite() != white) {
                    moves.add(new ChessMove(row, col, nr, nc, piece, target, null));
                }
            }
        }
    }
}
