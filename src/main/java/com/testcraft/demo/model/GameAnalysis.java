package com.testcraft.demo.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class GameAnalysis {

    private Long id;
    private String[][] boardInput;
    private String bestMoveFrom;
    private String bestMoveTo;
    private String pieceMoved;
    private String pieceCaptured;
    private int evaluationScore;
    private double winProbability;
    private boolean winnable;
    private String message;
    private LocalDateTime analyzedAt;

    public GameAnalysis() {
    }

    public GameAnalysis(Long id, String[][] boardInput, ChessMove bestMove,
                        int evaluationScore, double winProbability,
                        boolean winnable, String message) {
        this.id = id;
        this.boardInput = boardInput;
        this.evaluationScore = evaluationScore;
        this.winProbability = winProbability;
        this.winnable = winnable;
        this.message = message;
        this.analyzedAt = LocalDateTime.now();

        if (bestMove != null) {
            this.bestMoveFrom = bestMove.getFromAlgebraic();
            this.bestMoveTo = bestMove.getToAlgebraic();
            this.pieceMoved = String.valueOf(bestMove.getPiece().getSymbol());
            this.pieceCaptured = bestMove.getCaptured().isEmpty()
                    ? null
                    : String.valueOf(bestMove.getCaptured().getSymbol());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String[][] getBoardInput() {
        return boardInput;
    }

    public void setBoardInput(String[][] boardInput) {
        this.boardInput = boardInput;
    }

    public String getBestMoveFrom() {
        return bestMoveFrom;
    }

    public void setBestMoveFrom(String bestMoveFrom) {
        this.bestMoveFrom = bestMoveFrom;
    }

    public String getBestMoveTo() {
        return bestMoveTo;
    }

    public void setBestMoveTo(String bestMoveTo) {
        this.bestMoveTo = bestMoveTo;
    }

    public String getPieceMoved() {
        return pieceMoved;
    }

    public void setPieceMoved(String pieceMoved) {
        this.pieceMoved = pieceMoved;
    }

    public String getPieceCaptured() {
        return pieceCaptured;
    }

    public void setPieceCaptured(String pieceCaptured) {
        this.pieceCaptured = pieceCaptured;
    }

    public int getEvaluationScore() {
        return evaluationScore;
    }

    public void setEvaluationScore(int evaluationScore) {
        this.evaluationScore = evaluationScore;
    }

    public double getWinProbability() {
        return winProbability;
    }

    public void setWinProbability(double winProbability) {
        this.winProbability = winProbability;
    }

    public boolean isWinnable() {
        return winnable;
    }

    public void setWinnable(boolean winnable) {
        this.winnable = winnable;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameAnalysis that = (GameAnalysis) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
