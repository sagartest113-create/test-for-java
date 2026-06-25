package com.testcraft.demo.controller;

import com.testcraft.demo.dto.ChessBoardRequest;
import com.testcraft.demo.dto.ChessMoveResponse;
import com.testcraft.demo.model.GameAnalysis;
import com.testcraft.demo.service.ChessEngineService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chess")
public class ChessController {

    private final ChessEngineService engineService;

    public ChessController(ChessEngineService engineService) {
        this.engineService = engineService;
    }

    @PostMapping("/next-move")
    public ResponseEntity<ChessMoveResponse> getNextMove(@Valid @RequestBody ChessBoardRequest request) {
        GameAnalysis result = engineService.analyzePosition(request.board());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @GetMapping("/analysis/{id}")
    public ResponseEntity<ChessMoveResponse> getAnalysis(@PathVariable Long id) {
        return engineService.getAnalysis(id)
                .map(a -> ResponseEntity.ok(toResponse(a)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/analysis")
    public List<ChessMoveResponse> getAllAnalyses() {
        return engineService.getAllAnalyses().stream()
                .map(this::toResponse)
                .toList();
    }

    private ChessMoveResponse toResponse(GameAnalysis analysis) {
        return new ChessMoveResponse(
                analysis.getId(),
                analysis.getBestMoveFrom(),
                analysis.getBestMoveTo(),
                analysis.getPieceMoved(),
                analysis.getPieceCaptured(),
                analysis.getEvaluationScore(),
                analysis.getWinProbability(),
                analysis.isWinnable(),
                analysis.getMessage()
        );
    }
}
