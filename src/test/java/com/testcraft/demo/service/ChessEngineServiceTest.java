package com.testcraft.demo.service;

import com.testcraft.demo.model.ChessBoard;
import com.testcraft.demo.model.ChessMove;
import com.testcraft.demo.model.GameAnalysis;
import com.testcraft.demo.repository.GameAnalysisRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChessEngineServiceTest {

    @Mock
    private ChessMoveGenerator moveGenerator;

    @Mock
    private ChessEvaluator evaluator;

    @Mock
    private GameAnalysisRepository repository;

    @InjectMocks
    private ChessEngineService service;

    @Test
    @DisplayName("Analyze position with valid board input")
    void analyzePosition_ValidBoard_Input() {
        String[][] boardInput = new String[8][8];
        for (int i = 0; i < 8; i++) {
            boardInput[i][i] = "r";
        }
        GameAnalysis analysis = service.analyzePosition(boardInput);
        assertThat(analysis).isNotNull();
    }

    @Test
    @DisplayName("Analyze position with empty board input")
    void analyzePosition_EmptyBoard_Input() {
        String[][] boardInput = new String[8][8];
        assertThrows(IllegalArgumentException.class, () -> service.analyzePosition(boardInput));
    }

    @Test
    @DisplayName("Analyze position with null board input")
    void analyzePosition_NullBoard_Input() {
        assertThrows(IllegalArgumentException.class, () -> service.analyzePosition(null));
    }

    @Test
    @DisplayName("Analyze position with invalid board input")
    void analyzePosition_InvalidBoard_Input() {
        String[][] boardInput = new String[8][8];
        boardInput[0][0] = "r";
        boardInput[1][1] = "r";
        assertThrows(IllegalArgumentException.class, () -> service.analyzePosition(boardInput));
    }

    @Test
    @DisplayName("Get analysis with valid id")
    void getAnalysis_ValidId_ReturnsAnalysis() {
        Long id = 1L;
        GameAnalysis analysis = new GameAnalysis();
        when(repository.findById(id)).thenReturn(Optional.of(analysis));
        Optional<GameAnalysis> result = service.getAnalysis(id);
        assertThat(result).isPresent();
        verify(repository).findById(id);
    }

    @Test
    @DisplayName("Get analysis with invalid id")
    void getAnalysis_InvalidId_ReturnsEmptyOptional() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        Optional<GameAnalysis> result = service.getAnalysis(id);
        assertThat(result).isEmpty();
        verify(repository).findById(id);
    }

    @Test
    @DisplayName("Get all analyses")
    void getAllAnalyses_ReturnsAllAnalyses() {
        List<GameAnalysis> analyses = new ArrayList<>();
        when(repository.findAll()).thenReturn(analyses);
        List<GameAnalysis> result = service.getAllAnalyses();
        assertThat(result).isEqualTo(analyses);
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Minimax with valid board and depth")
    void minimax_ValidBoard_Depth() {
        ChessBoard board = new ChessBoard(new String[8][8]);
        int depth = 2;
        int alpha = -100;
        int beta = 100;
        boolean maximizing = true;
        when(moveGenerator.generateLegalMoves(board, maximizing)).thenReturn(List.of(new ChessMove(0, 0, 0, 0, ChessPiece.WHITE_KING, ChessPiece.EMPTY, null)));
        int result = service.minimax(board, depth, alpha, beta, maximizing);
        assertThat(result).isGreaterThanOrEqualTo(-100);
        verify(moveGenerator).generateLegalMoves(board, maximizing);
    }

    @Test
    @DisplayName("Minimax with invalid board and depth")
    void minimax_InvalidBoard_Depth() {
        ChessBoard board = new ChessBoard(new String[8][8]);
        int depth = 2;
        int alpha = -100;
        int beta = 100;
        boolean maximizing = true;
        when(moveGenerator.generateLegalMoves(board, maximizing)).thenReturn(List.of());
        int result = service.minimax(board, depth, alpha, beta, maximizing);
        assertThat(result).isEqualTo(0);
        verify(moveGenerator).generateLegalMoves(board, maximizing);
    }
}