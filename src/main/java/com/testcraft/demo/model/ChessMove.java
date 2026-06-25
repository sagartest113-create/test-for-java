package com.testcraft.demo.model;

import java.util.Objects;

public class ChessMove {

    private final int fromRow;
    private final int fromCol;
    private final int toRow;
    private final int toCol;
    private final ChessPiece piece;
    private final ChessPiece captured;
    private final ChessPiece promotedTo;

    public ChessMove(int fromRow, int fromCol, int toRow, int toCol,
                     ChessPiece piece, ChessPiece captured, ChessPiece promotedTo) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.piece = piece;
        this.captured = captured;
        this.promotedTo = promotedTo;
    }

    public int getFromRow() {
        return fromRow;
    }

    public int getFromCol() {
        return fromCol;
    }

    public int getToRow() {
        return toRow;
    }

    public int getToCol() {
        return toCol;
    }

    public ChessPiece getPiece() {
        return piece;
    }

    public ChessPiece getCaptured() {
        return captured;
    }

    public ChessPiece getPromotedTo() {
        return promotedTo;
    }

    public String getFromAlgebraic() {
        return toAlgebraic(fromRow, fromCol);
    }

    public String getToAlgebraic() {
        return toAlgebraic(toRow, toCol);
    }

    private static String toAlgebraic(int row, int col) {
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMove that = (ChessMove) o;
        return fromRow == that.fromRow && fromCol == that.fromCol
                && toRow == that.toRow && toCol == that.toCol
                && piece == that.piece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromRow, fromCol, toRow, toCol, piece);
    }

    @Override
    public String toString() {
        String move = getFromAlgebraic() + getToAlgebraic();
        if (promotedTo != null) {
            move += "=" + promotedTo.getSymbol();
        }
        return move;
    }
}
