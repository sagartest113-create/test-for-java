package com.testcraft.demo.model;

public class ChessBoard {

    private static final int SIZE = 8;

    private static final int[][] KNIGHT_OFFSETS = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
    };
    private static final int[][] STRAIGHT_DIRS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    private static final int[][] DIAGONAL_DIRS = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

    private final ChessPiece[][] squares;

    public ChessBoard(String[][] input) {
        squares = new ChessPiece[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                squares[r][c] = ChessPiece.fromSymbol(input[r][c]);
            }
        }
    }

    private ChessBoard(ChessPiece[][] source) {
        squares = new ChessPiece[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            System.arraycopy(source[r], 0, squares[r], 0, SIZE);
        }
    }

    public ChessPiece getPiece(int row, int col) {
        return squares[row][col];
    }

    public void makeMove(ChessMove move) {
        ChessPiece placed = move.getPromotedTo() != null ? move.getPromotedTo() : move.getPiece();
        squares[move.getToRow()][move.getToCol()] = placed;
        squares[move.getFromRow()][move.getFromCol()] = ChessPiece.EMPTY;
    }

    public void undoMove(ChessMove move) {
        squares[move.getFromRow()][move.getFromCol()] = move.getPiece();
        squares[move.getToRow()][move.getToCol()] = move.getCaptured();
    }

    public int[] findKing(boolean white) {
        ChessPiece target = white ? ChessPiece.WHITE_KING : ChessPiece.BLACK_KING;
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (squares[r][c] == target) {
                    return new int[]{r, c};
                }
            }
        }
        return null;
    }

    public boolean isInCheck(boolean white) {
        int[] kingPos = findKing(white);
        if (kingPos == null) {
            return true;
        }
        return isSquareAttacked(kingPos[0], kingPos[1], !white);
    }

    public boolean isSquareAttacked(int row, int col, boolean byWhite) {
        return isAttackedByKnight(row, col, byWhite)
                || isAttackedByPawn(row, col, byWhite)
                || isAttackedByKing(row, col, byWhite)
                || isAttackedBySlider(row, col, byWhite, STRAIGHT_DIRS, true)
                || isAttackedBySlider(row, col, byWhite, DIAGONAL_DIRS, false);
    }

    private boolean isAttackedByKnight(int row, int col, boolean byWhite) {
        ChessPiece enemyKnight = byWhite ? ChessPiece.WHITE_KNIGHT : ChessPiece.BLACK_KNIGHT;
        for (int[] offset : KNIGHT_OFFSETS) {
            int r = row + offset[0];
            int c = col + offset[1];
            if (isValid(r, c) && squares[r][c] == enemyKnight) {
                return true;
            }
        }
        return false;
    }

    private boolean isAttackedByPawn(int row, int col, boolean byWhite) {
        ChessPiece enemyPawn = byWhite ? ChessPiece.WHITE_PAWN : ChessPiece.BLACK_PAWN;
        int pawnRow = byWhite ? row + 1 : row - 1;
        if (isValid(pawnRow, col - 1) && squares[pawnRow][col - 1] == enemyPawn) {
            return true;
        }
        return isValid(pawnRow, col + 1) && squares[pawnRow][col + 1] == enemyPawn;
    }

    private boolean isAttackedByKing(int row, int col, boolean byWhite) {
        ChessPiece enemyKing = byWhite ? ChessPiece.WHITE_KING : ChessPiece.BLACK_KING;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int r = row + dr;
                int c = col + dc;
                if (isValid(r, c) && squares[r][c] == enemyKing) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isAttackedBySlider(int row, int col, boolean byWhite,
                                       int[][] directions, boolean straight) {
        ChessPiece enemyQueen = byWhite ? ChessPiece.WHITE_QUEEN : ChessPiece.BLACK_QUEEN;
        ChessPiece enemySlider = straight
                ? (byWhite ? ChessPiece.WHITE_ROOK : ChessPiece.BLACK_ROOK)
                : (byWhite ? ChessPiece.WHITE_BISHOP : ChessPiece.BLACK_BISHOP);

        for (int[] dir : directions) {
            for (int i = 1; i < SIZE; i++) {
                int r = row + dir[0] * i;
                int c = col + dir[1] * i;
                if (!isValid(r, c)) break;
                if (!squares[r][c].isEmpty()) {
                    if (squares[r][c] == enemySlider || squares[r][c] == enemyQueen) {
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }

    public static boolean isValid(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    public ChessBoard copy() {
        return new ChessBoard(squares);
    }
}
