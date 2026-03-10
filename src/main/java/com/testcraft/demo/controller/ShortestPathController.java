package com.testcraft.demo.controller;

import com.testcraft.demo.dto.GridRequest;
import com.testcraft.demo.dto.ShortestPathResponse;
import com.testcraft.demo.model.PathComputation;
import com.testcraft.demo.service.ShortestPathService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shortest-path")
public class ShortestPathController {

    private final ShortestPathService shortestPathService;

    public ShortestPathController(ShortestPathService shortestPathService) {
        this.shortestPathService = shortestPathService;
    }

    @PostMapping
    public ResponseEntity<ShortestPathResponse> computeShortestPath(@Valid @RequestBody GridRequest request) {
        PathComputation result = shortestPathService.computeShortestPath(request.grid());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShortestPathResponse> getComputation(@PathVariable Long id) {
        return shortestPathService.getComputation(id)
                .map(c -> ResponseEntity.ok(toResponse(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ShortestPathResponse> getAllComputations() {
        return shortestPathService.getAllComputations().stream()
                .map(this::toResponse)
                .toList();
    }

    private ShortestPathResponse toResponse(PathComputation computation) {
        return new ShortestPathResponse(
                computation.getId(),
                computation.getPath(),
                computation.getTotalCost(),
                computation.isReachable()
        );
    }
}
