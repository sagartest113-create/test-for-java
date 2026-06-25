package com.testcraft.demo.service;

import com.testcraft.demo.model.SearchResult;
import com.testcraft.demo.repository.SearchResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BinarySearchServiceTest {

    @Mock
    private SearchResultRepository repository;

    @InjectMocks
    private BinarySearchService binarySearchService;

    @Test
    @DisplayName("Search for a target in a sorted array")
    void searchTarget() {
        int[] sortedArray = {1, 3, 5, 7, 9};
        int target = 5;
        SearchResult expected = new SearchResult(null, sortedArray, target, true, 2, 3, Arrays.asList("Step 1: mid=0 value=1 < 5 → search right", "Step 2: mid=1 value=3 < 5 → search right", "Step 3: mid=2 value=5 = 5 → FOUND"));

        when(repository.save(expected)).thenReturn(expected);
        SearchResult result = binarySearchService.search(sortedArray, target);

        assertThat(result).isEqualTo(expected);
        verify(repository).save(expected);
    }

    @Test
    @DisplayName("Search for a target not in a sorted array")
    void searchNotTarget() {
        int[] sortedArray = {1, 3, 5, 7, 9};
        int target = 6;
        SearchResult expected = new SearchResult(null, sortedArray, target, false, -1, 3, Arrays.asList("Step 1: mid=0 value=1 < 6 → search right", "Step 2: mid=1 value=3 < 6 → search right", "Step 3: mid=2 value=5 > 6 → search left"));

        when(repository.save(expected)).thenReturn(expected);
        SearchResult result = binarySearchService.search(sortedArray, target);

        assertThat(result).isEqualTo(expected);
        verify(repository).save(expected);
    }

    @Test
    @DisplayName("Search for a target in an empty array")
    void searchEmptyArray() {
        int[] sortedArray = {};
        int target = 5;
        SearchResult expected = new SearchResult(null, sortedArray, target, false, -1, 0, Arrays.asList("Target 5 not found after 0 comparisons"));

        when(repository.save(expected)).thenReturn(expected);
        SearchResult result = binarySearchService.search(sortedArray, target);

        assertThat(result).isEqualTo(expected);
        verify(repository).save(expected);
    }

    @Test
    @DisplayName("Search for a null target")
    void searchNullTarget() {
        int[] sortedArray = {1, 3, 5, 7, 9};
        Integer target = null;
        SearchResult expected = new SearchResult(null, sortedArray, 0, false, -1, 0, Arrays.asList("Target null not found after 0 comparisons"));

        when(repository.save(expected)).thenReturn(expected);
        SearchResult result = binarySearchService.search(sortedArray, target);

        assertThat(result).isEqualTo(expected);
        verify(repository).save(expected);
    }

    @Test
    @DisplayName("Get result by id")
    void getResultById() {
        Long id = 1L;
        SearchResult result = new SearchResult(id, new int[]{1, 3, 5, 7, 9}, 5, true, 2, 3, Arrays.asList("Step 1: mid=0 value=1 < 5 → search right", "Step 2: mid=1 value=3 < 5 → search right", "Step 3: mid=2 value=5 = 5 → FOUND"));

        when(repository.findById(id)).thenReturn(Optional.of(result));
        Optional<SearchResult> resultOptional = binarySearchService.getResult(id);

        assertThat(resultOptional).contains(result);
    }

    @Test
    @DisplayName("Get all results")
    void getAllResults() {
        List<SearchResult> results = new ArrayList<>();
        results.add(new SearchResult(1L, new int[]{1, 3, 5, 7, 9}, 5, true, 2, 3, Arrays.asList("Step 1: mid=0 value=1 < 5 → search right", "Step 2: mid=1 value=3 < 5 → search right", "Step 3: mid=2 value=5 = 5 → FOUND")));
        results.add(new SearchResult(2L, new int[]{1, 3, 5, 7, 9}, 6, false, -1, 3, Arrays.asList("Step 1: mid=0 value=1 < 6 → search right", "Step 2: mid=1 value=3 < 6 → search right", "Step 3: mid=2 value=5 > 6 → search left")));

        when(repository.findAll()).thenReturn(results);
        List<SearchResult> allResults = binarySearchService.getAllResults();

        assertThat(allResults).isEqualTo(results);
    }
}