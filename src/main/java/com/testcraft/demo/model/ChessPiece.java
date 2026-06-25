package com.testcraft.demo.model;

public enum ChessPiece {

    WHITE_KING('K', 20000, true),
    WHITE_QUEEN('Q', 900, true),
    WHITE_ROOK('R', 500, true),
    WHITE_BISHOP('B', 330, true),
    WHITE_KNIGHT('N', 320, true),
    WHITE_PAWN('P', 100, true),
    BLACK_KING('k', 20000, false),
    BLACK_QUEEN('q', 900, false),
    BLACK_ROOK('r', 500, false),
    BLACK_BISHOP('b', 330, false),
    BLACK_KNIGHT('n', 320, false),
    BLACK_PAWN('p', 100, false),
    EMPTY('.', 0, true);

    private final char symbol;
    private final int value;
    private final boolean white;

    ChessPiece(char symbol, int value, boolean white) {
        this.symbol = symbol;
        this.value = value;
        this.white = white;
    }

    public char getSymbol() {
        return symbol;
    }

    public int getValue() {
        return value;
    }

    public boolean isWhite() {
        return this != EMPTY && white;
    }

    public boolean isBlack() {
        return this != EMPTY && !white;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public static ChessPiece fromSymbol(String s) {
        if (s == null || s.isEmpty() || ".".equals(s)) {
            return EMPTY;
        }
        char c = s.charAt(0);
        for (ChessPiece piece : values()) {
            if (piece.symbol == c) {
                return piece;
            }
        }
        throw new IllegalArgumentException("Unknown piece symbol: " + s);
    }
}
