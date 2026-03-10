package com.testcraft.demo.service;

import com.testcraft.demo.model.ChessBoard;
import com.testcraft.demo.model.ChessMove;
import com.testcraft.demo.model.GameAnalysis;
import com.testcraft.demo.repository.GameAnalysisRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChessEngineService {

    private static final int SEARCH_DEPTH = 4;
    private static final int WIN_THRESHOLD = -500;

    private final ChessMoveGenerator moveGenerator;
    private final ChessEvaluator evaluator;
    private final GameAnalysisRepository repository;

    public ChessEngineService(ChessMoveGenerator moveGenerator,
                              ChessEvaluator evaluator,
                              GameAnalysisRepository repository) {
        this.moveGenerator = moveGenerator;
        this.evaluator = evaluator;
        this.repository = repository;
    }

    public GameAnalysis analyzePosition(String[][] boardInput) {
        validateBoard(boardInput);
        ChessBoard board = new ChessBoard(boardInput);
        List<ChessMove> legalMoves = moveGenerator.generateLegalMoves(board, true);

        if (legalMoves.isEmpty()) {
            return saveNoMoveResult(boardInput, board.isInCheck(true));
        }

        ChessMove bestMove = null;
        int bestEval = Integer.MIN_VALUE;

        for (ChessMove move : legalMoves) {
            board.makeMove(move);
            int eval = minimax(board, SEARCH_DEPTH - 1,
                    Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            board.undoMove(move);

            if (eval > bestEval) {
                bestEval = eval;
                bestMove = move;
            }
        }

        double winProb = sigmoid(bestEval);
        boolean winnable = bestEval > WIN_THRESHOLD;
        String message = winnable ? "Best move found" : "Position is losing — win unlikely";

        GameAnalysis analysis = new GameAnalysis(
                null, boardInput, bestMove, bestEval, winProb, winnable, message);
        return repository.save(analysis);
    }

    public Optional<GameAnalysis> getAnalysis(Long id) {
        return repository.findById(id);
    }

    public List<GameAnalysis> getAllAnalyses() {
        return repository.findAll();
    }

    private int minimax(ChessBoard board, int depth, int alpha, int beta, boolean maximizing) {
        if (depth == 0) {
            return evaluator.evaluate(board);
        }

        List<ChessMove> moves = moveGenerator.generateLegalMoves(board, maximizing);

        if (moves.isEmpty()) {
            if (board.isInCheck(maximizing)) {
                return maximizing
                        ? -ChessEvaluator.CHECKMATE_SCORE + (SEARCH_DEPTH - depth)
                        : ChessEvaluator.CHECKMATE_SCORE - (SEARCH_DEPTH - depth);
            }
            return 0;
        }

        if (maximizing) {
            return maximise(board, depth, alpha, beta, moves);
        } else {
            return minimise(board, depth, alpha, beta, moves);
        }
    }

    private int maximise(ChessBoard board, int depth, int alpha, int beta, List<ChessMove> moves) {
        int maxEval = Integer.MIN_VALUE;
        for (ChessMove move : moves) {
            board.makeMove(move);
            int eval = minimax(board, depth - 1, alpha, beta, false);
            board.undoMove(move);
            maxEval = Math.max(maxEval, eval);
            alpha = Math.max(alpha, eval);
            if (beta <= alpha) {
                break;
            }
        }
        return maxEval;
    }

    private int minimise(ChessBoard board, int depth, int alpha, int beta, List<ChessMove> moves) {
        int minEval = Integer.MAX_VALUE;
        for (ChessMove move : moves) {
            board.makeMove(move);
            int eval = minimax(board, depth - 1, alpha, beta, true);
            board.undoMove(move);
            minEval = Math.min(minEval, eval);
            beta = Math.min(beta, eval);
            if (beta <= alpha) {
                break;
            }
        }
        return minEval;
    }

    private double sigmoid(int centipawns) {
        return 1.0 / (1.0 + Math.pow(10, -centipawns / 400.0));
    }

    private GameAnalysis saveNoMoveResult(String[][] boardInput, boolean inCheck) {
        String message = inCheck ? "White is in checkmate" : "Stalemate — draw";
        int score = inCheck ? -ChessEvaluator.CHECKMATE_SCORE : 0;
        double prob = inCheck ? 0.0 : 0.5;
        GameAnalysis analysis = new GameAnalysis(
                null, boardInput, null, score, prob, false, message);
        return repository.save(analysis);
    }

    private void validateBoard(String[][] board) {
        if (board == null || board.length != 8) {
            throw new IllegalArgumentException("Board must be an 8x8 array");
        }
        for (int i = 0; i < 8; i++) {
            if (board[i] == null || board[i].length != 8) {
                throw new IllegalArgumentException("Each row must have exactly 8 columns");
            }
        }
    }
}
