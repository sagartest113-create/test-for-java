package com.testcraft.demo.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameAnalysisTest {

    @Test
    @DisplayName("Default constructor should create a GameAnalysis object with default values")
    void testDefaultConstructor() {
        GameAnalysis gameAnalysis = new GameAnalysis();
        assertThat(gameAnalysis.getId()).isNull();
        assertThat(gameAnalysis.getBoardInput()).isNull();
        assertThat(gameAnalysis.getBestMoveFrom()).isNull();
        assertThat(gameAnalysis.getBestMoveTo()).isNull();
        assertThat(gameAnalysis.getPieceMoved()).isNull();
        assertThat(gameAnalysis.getPieceCaptured()).isNull();
        assertThat(gameAnalysis.getEvaluationScore()).isEqualTo(0);
        assertThat(gameAnalysis.getWinProbability()).isEqualTo(0.0);
        assertThat(gameAnalysis.isWinnable()).isFalse();
        assertThat(gameAnalysis.getMessage()).isNull();
        assertThat(gameAnalysis.getAnalyzedAt()).isNotNull();
    }

    @Test
    @DisplayName("Parameterized constructor should create a GameAnalysis object with given values")
    void testParameterizedConstructor() {
        String[][] boardInput = new String[][]{{"a", "b"}, {"c", "d"}};
        ChessMove bestMove = new ChessMove("e2", "e4", Piece.WHITE_PAWN, null);
        GameAnalysis gameAnalysis = new GameAnalysis(1L, boardInput, bestMove, 100, 0.5, true, "message");
        assertThat(gameAnalysis.getId()).isEqualTo(1L);
        assertThat(gameAnalysis.getBoardInput()).isEqualTo(boardInput);
        assertThat(gameAnalysis.getBestMoveFrom()).isEqualTo("e2");
        assertThat(gameAnalysis.getBestMoveTo()).isEqualTo("e4");
        assertThat(gameAnalysis.getPieceMoved()).isEqualTo("P");
        assertThat(gameAnalysis.getPieceCaptured()).isNull();
        assertThat(gameAnalysis.getEvaluationScore()).isEqualTo(100);
        assertThat(gameAnalysis.getWinProbability()).isEqualTo(0.5);
        assertThat(gameAnalysis.isWinnable()).isTrue();
        assertThat(gameAnalysis.getMessage()).isEqualTo("message");
        assertThat(gameAnalysis.getAnalyzedAt()).isNotNull();
    }

    @Test
    @DisplayName("Getter and setter methods should work correctly")
    void testGetterSetterMethods() {
        GameAnalysis gameAnalysis = new GameAnalysis();
        gameAnalysis.setId(1L);
        gameAnalysis.setBoardInput(new String[][]{{"a", "b"}, {"c", "d"}});
        gameAnalysis.setBestMoveFrom("e2");
        gameAnalysis.setBestMoveTo("e4");
        gameAnalysis.setPieceMoved("P");
        gameAnalysis.setPieceCaptured("N");
        gameAnalysis.setEvaluationScore(100);
        gameAnalysis.setWinProbability(0.5);
        gameAnalysis.setWinnable(true);
        gameAnalysis.setMessage("message");
        gameAnalysis.setAnalyzedAt(LocalDateTime.now());
        assertThat(gameAnalysis.getId()).isEqualTo(1L);
        assertThat(gameAnalysis.getBoardInput()).isEqualTo(new String[][]{{"a", "b"}, {"c", "d"}});
        assertThat(gameAnalysis.getBestMoveFrom()).isEqualTo("e2");
        assertThat(gameAnalysis.getBestMoveTo()).isEqualTo("e4");
        assertThat(gameAnalysis.getPieceMoved()).isEqualTo("P");
        assertThat(gameAnalysis.getPieceCaptured()).isEqualTo("N");
        assertThat(gameAnalysis.getEvaluationScore()).isEqualTo(100);
        assertThat(gameAnalysis.getWinProbability()).isEqualTo(0.5);
        assertThat(gameAnalysis.isWinnable()).isTrue();
        assertThat(gameAnalysis.getMessage()).isEqualTo("message");
        assertThat(gameAnalysis.getAnalyzedAt()).isNotNull();
    }

    @Test
    @DisplayName("Equals method should work correctly")
    void testEqualsMethod() {
        GameAnalysis gameAnalysis1 = new GameAnalysis(1L, new String[][]{{"a", "b"}, {"c", "d"}}, null, 100, 0.5, true, "message");
        GameAnalysis gameAnalysis2 = new GameAnalysis(1L, new String[][]{{"a", "b"}, {"c", "d"}}, null, 100, 0.5, true, "message");
        GameAnalysis gameAnalysis3 = new GameAnalysis(2L, new String[][]{{"a", "b"}, {"c", "d"}}, null, 100, 0.5, true, "message");
        GameAnalysis gameAnalysis4 = new Object();
        assertThat(gameAnalysis1.equals(gameAnalysis2)).isTrue();
        assertThat(gameAnalysis1.equals(gameAnalysis3)).isFalse();
        assertThat(gameAnalysis1.equals(gameAnalysis4)).isFalse();
    }

    @Test
    @DisplayName("HashCode method should work correctly")
    void testHashCodeMethod() {
        GameAnalysis gameAnalysis1 = new GameAnalysis(1L, new String[][]{{"a", "b"}, {"c", "d"}}, null, 100, 0.5, true, "message");
        GameAnalysis gameAnalysis2 = new GameAnalysis(1L, new String[][]{{"a", "b"}, {"c", "d"}}, null, 100, 0.5, true, "message");
        GameAnalysis gameAnalysis3 = new GameAnalysis(2L, new String[][]{{"a", "b"}, {"c", "d"}}, null, 100, 0.5, true, "message");
        assertThat(gameAnalysis1.hashCode()).isEqualTo(gameAnalysis2.hashCode());
        assertThat(gameAnalysis1.hashCode()).isNotEqualTo(gameAnalysis3.hashCode());
    }

    @Test
    @DisplayName("Null id should throw NullPointerException")
    void testNullId() {
        assertThrows(NullPointerException.class, () -> new GameAnalysis(null, new String[][]{{"a", "b"}, {"c", "d"}}, null, 100, 0.5, true, "message"));
    }
}