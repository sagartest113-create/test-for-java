package com.testcraft.demo.dto;

import com.testcraft.demo.model.Cell;

import java.util.List;

public record ShortestPathResponse(
        Long computationId,
        List<Cell> path,
        int totalCost,
        boolean reachable
) {
}
