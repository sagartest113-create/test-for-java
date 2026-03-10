package com.testcraft.demo.repository;

import com.testcraft.demo.model.SearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.assertAll;
import org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({org.junit.jupiter.api.extension.ExtendWith.class})
public class SearchResultRepositoryTest {

    private SearchResultRepository repository;

    @BeforeEach
    void setup() {
        repository = new SearchResultRepository();
    }

    @Test
    @DisplayName("Save SearchResult with auto-generated ID")
    void saveSearchResult() {
        // Given
        SearchResult result = new SearchResult(0, new int[]{1, 2, 3}, 2, false, 0, 0, new ArrayList<>());

        // When
        SearchResult savedResult = repository.save(result);

        // Then
        assertAll(
                () -> assertNotNull(savedResult.getId()),
                () -> assertEquals(1, savedResult.getId()),
                () -> assertEquals(result.getSortedArray(), savedResult.getSortedArray()),
                () -> assertEquals(result.getTarget(), savedResult.getTarget()),
                () -> assertEquals(result.isFound(), savedResult.isFound()),
                () -> assertEquals(result.getIndex(), savedResult.getIndex()),
                () -> assertEquals(result.getComparisons(), savedResult.getComparisons()),
                () -> assertEquals(result.getSteps(), savedResult.getSteps()),
                () -> assertNotNull(savedResult.getSearchedAt())
        );
    }

    @Test
    @DisplayName("Save multiple SearchResults with auto-generated IDs")
    void saveMultipleSearchResults() {
        // Given
        SearchResult result1 = new SearchResult(0, new int[]{1, 2, 3}, 2, false, 0, 0, new ArrayList<>());
        SearchResult result2 = new SearchResult(0, new int[]{4, 5, 6}, 5, false, 0, 0, new ArrayList<>());

        // When
        SearchResult savedResult1 = repository.save(result1);
        SearchResult savedResult2 = repository.save(result2);

        // Then
        assertAll(
                () -> assertNotNull(savedResult1.getId()),
                () -> assertNotNull(savedResult2.getId()),
                () -> assertEquals(1, savedResult1.getId()),
                () -> assertEquals(2, savedResult2.getId()),
                () -> assertEquals(result1.getSortedArray(), savedResult1.getSortedArray()),
                () -> assertEquals(result2.getSortedArray(), savedResult2.getSortedArray()),
                () -> assertEquals(result1.getTarget(), savedResult1.getTarget()),
                () -> assertEquals(result2.getTarget(), savedResult2.getTarget()),
                () -> assertEquals(result1.isFound(), savedResult1.isFound()),
                () -> assertEquals(result2.isFound(), savedResult2.isFound()),
                () -> assertEquals(result1.getIndex(), savedResult1.getIndex()),
                () -> assertEquals(result2.getIndex(), savedResult2.getIndex()),
                () -> assertEquals(result1.getComparisons(), savedResult1.getComparisons()),
                () -> assertEquals(result2.getComparisons(), savedResult2.getComparisons()),
                () -> assertEquals(result1.getSteps(), savedResult1.getSteps()),
                () -> assertEquals(result2.getSteps(), savedResult2.getSteps()),
                () -> assertNotNull(savedResult1.getSearchedAt()),
                () -> assertNotNull(savedResult2.getSearchedAt())
        );
    }

    @Test
    @DisplayName("Find SearchResult by ID")
    void findById() {
        // Given
        SearchResult result = new SearchResult(0, new int[]{1, 2, 3}, 2, false, 0, 0, new ArrayList<>());
        repository.save(result);

        // When
        Optional<SearchResult> foundResult = repository.findById(result.getId());

        // Then
        assertTrue(foundResult.isPresent());
        assertEquals(result, foundResult.get());
    }

    @Test
    @DisplayName("Find SearchResult by non-existent ID")
    void findByIdNonExistent() {
        // Given
        SearchResult result = new SearchResult(0, new int[]{1, 2, 3}, 2, false, 0, 0, new ArrayList<>());

        // When
        Optional<SearchResult> foundResult = repository.findById(result.getId());

        // Then
        assertFalse(foundResult.isPresent());
    }

    @Test
    @DisplayName("Find all SearchResults")
    void findAll() {
        // Given
        SearchResult result1 = new SearchResult(0, new int[]{1, 2, 3}, 2, false, 0, 0, new ArrayList<>());
        SearchResult result2 = new SearchResult(0, new int[]{4, 5, 6}, 5, false, 0, 0, new ArrayList<>());
        repository.save(result1);
        repository.save(result2);

        // When
        List<SearchResult> allResults = repository.findAll();

        // Then
        assertEquals(2, allResults.size());
        assertTrue(allResults.contains(result1));
        assertTrue(allResults.contains(result2));
    }

    @Test
    @DisplayName("Delete SearchResult by ID")
    void deleteById() {
        // Given
        SearchResult result = new SearchResult(0, new int[]{1, 2, 3}, 2, false, 0, 0, new ArrayList<>());
        repository.save(result);

        // When
        repository.findById(result.getId());
        repository.deleteById(result.getId());

        // Then
        assertFalse(repository.findById(result.getId()).isPresent());
    }

    @Test
    @DisplayName("Delete non-existent SearchResult by ID")
    void deleteByIdNonExistent() {
        // Given
        SearchResult result = new SearchResult(0, new int[]{1, 2, 3}, 2, false, 0, 0, new ArrayList<>());

        // When
        repository.deleteById(result.getId());

        // Then
        assertFalse(repository.findById(result.getId()).isPresent());
    }
}