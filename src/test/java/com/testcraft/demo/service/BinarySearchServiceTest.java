package com.testcraft.demo.service;

import com.testcraft.demo.model.SearchResult;
import com.testcraft.demo.repository.SearchResultRepository;
import com.testcraft.demo.service.BinarySearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BinarySearchServiceTest {

    @Mock
    private SearchResultRepository repository;

    @InjectMocks
    private BinarySearchService binarySearchService;

    private List<SearchResult> results;

    @BeforeEach
    public void setup() {
        results = new ArrayList<>();
        when(repository.save(any(SearchResult.class))).thenAnswer(invocation -> {
            SearchResult result = invocation.getArgument(0);
            results.add(result);
            return result;
        });
    }

    @Test
    public void testSearchFound() {
        // Arrange
        int[] sortedArray = {1, 2, 3, 4, 5};
        int target = 3;
        SearchResult expected = new SearchResult(null, sortedArray, target, true, 2, 1, Arrays.asList("Step 1: mid=2 value=3 → FOUND"));

        // Act
        SearchResult result = binarySearchService.search(sortedArray, target);

        // Assert
        assertNotNull(result);
        assertTrue(result.isFound());
        assertEquals(target, result.getTarget());
        assertEquals(2, result.getFoundIndex());
        assertEquals(1, result.getComparisons());
        assertEquals(expected.getSteps(), result.getSteps());
    }

    @Test
    public void testSearchNotFound() {
        // Arrange
        int[] sortedArray = {1, 2, 3, 4, 5};
        int target = 6;
        SearchResult expected = new SearchResult(null, sortedArray, target, false, -1, 3, Arrays.asList("Step 1: mid=2 value=3 < 6 → search right", "Step 2: mid=3 value=4 < 6 → search right", "Step 3: mid=4 value=5 < 6 → search right", "Target 6 not found after 3 comparisons"));

        // Act
        SearchResult result = binarySearchService.search(sortedArray, target);

        // Assert
        assertNotNull(result);
        assertFalse(result.isFound());
        assertEquals(target, result.getTarget());
        assertEquals(-1, result.getFoundIndex());
        assertEquals(3, result.getComparisons());
        assertEquals(expected.getSteps(), result.getSteps());
    }

    @Test
    public void testSearchEdgeCaseEmptyArray() {
        // Arrange
        int[] sortedArray = {};
        int target = 3;
        SearchResult expected = new SearchResult(null, sortedArray, target, false, -1, 1, Arrays.asList("Target 3 not found after 1 comparisons"));

        // Act
        SearchResult result = binarySearchService.search(sortedArray, target);

        // Assert
        assertNotNull(result);
        assertFalse(result.isFound());
        assertEquals(target, result.getTarget());
        assertEquals(-1, result.getFoundIndex());
        assertEquals(1, result.getComparisons());
        assertEquals(expected.getSteps(), result.getSteps());
    }

    @Test
    public void testSearchEdgeCaseSingleElementArray() {
        // Arrange
        int[] sortedArray = {3};
        int target = 3;
        SearchResult expected = new SearchResult(null, sortedArray, target, true, 0, 1, Arrays.asList("Step 1: mid=0 value=3 → FOUND"));

        // Act
        SearchResult result = binarySearchService.search(sortedArray, target);

        // Assert
        assertNotNull(result);
        assertTrue(result.isFound());
        assertEquals(target, result.getTarget());
        assertEquals(0, result.getFoundIndex());
        assertEquals(1, result.getComparisons());
        assertEquals(expected.getSteps(), result.getSteps());
    }

    @Test
    public void testGetResult() {
        // Arrange
        SearchResult result = new SearchResult(null, new int[]{1, 2, 3}, 3, true, 2, 1, Arrays.asList("Step 1: mid=2 value=3 → FOUND"));
        when(repository.findById(any(Long.class))).thenReturn(Optional.of(result));

        // Act
        Optional<SearchResult> foundResult = binarySearchService.getResult(1L);

        // Assert
        assertTrue(foundResult.isPresent());
        assertEquals(result, foundResult.get());
    }

    @Test
    public void testGetResultNotFound() {
        // Arrange
        when(repository.findById(any(Long.class))).thenReturn(Optional.empty());

        // Act
        Optional<SearchResult> foundResult = binarySearchService.getResult(1L);

        // Assert
        assertFalse(foundResult.isPresent());
    }

    @Test
    public void testGetAllResults() {
        // Arrange
        SearchResult result1 = new SearchResult(null, new int[]{1, 2, 3}, 3, true, 2, 1, Arrays.asList("Step 1: mid=2 value=3 → FOUND"));
        SearchResult result2 = new SearchResult(null, new int[]{4, 5, 6}, 6, true, 2, 1, Arrays.asList("Step 1: mid=2 value=6 → FOUND"));
        when(repository.findAll()).thenReturn(Arrays.asList(result1, result2));

        // Act
        List<SearchResult> allResults = binarySearchService.getAllResults();

        // Assert
        assertNotNull(allResults);
        assertEquals(2, allResults.size());
        assertEquals(result1, allResults.get(0));
        assertEquals(result2, allResults.get(1));
    }
}