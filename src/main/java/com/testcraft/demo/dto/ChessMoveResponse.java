package com.testcraft.demo.dto;

public record ChessMoveResponse(
        Long analysisId,
        String from,
        String to,
        String piece,
        String captured,
        int evaluationScore,
        double winProbability,
        boolean winnable,
        String message
) {
}
