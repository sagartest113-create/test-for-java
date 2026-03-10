package com.testcraft.demo.controller;

import com.testcraft.demo.dto.BinarySearchRequest;
import com.testcraft.demo.dto.BinarySearchResponse;
import com.testcraft.demo.model.SearchResult;
import com.testcraft.demo.service.BinarySearchService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/binary-search")
public class BinarySearchController {

    private final BinarySearchService searchService;

    public BinarySearchController(BinarySearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping
    public ResponseEntity<BinarySearchResponse> search(@Valid @RequestBody BinarySearchRequest request) {
        SearchResult result = searchService.search(request.sortedArray(), request.target());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BinarySearchResponse> getResult(@PathVariable Long id) {
        return searchService.getResult(id)
                .map(r -> ResponseEntity.ok(toResponse(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<BinarySearchResponse> getAllResults() {
        return searchService.getAllResults().stream()
                .map(this::toResponse)
                .toList();
    }

    private BinarySearchResponse toResponse(SearchResult result) {
        return new BinarySearchResponse(
                result.getId(),
                result.getTarget(),
                result.isFound(),
                result.getIndex(),
                result.getComparisons(),
                result.getSteps()
        );
    }
}
