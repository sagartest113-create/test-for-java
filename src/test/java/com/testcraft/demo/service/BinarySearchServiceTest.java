package com.testcraft.demo.service;

import com.testcraft.demo.model.SearchResult;
import com.testcraft.demo.repository.SearchResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitWebConfig
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

        // Act
        SearchResult result = binarySearchService.search(sortedArray, target);

        // Assert
        assertNotNull(result);
        assertTrue(result.isFound());
        assertEquals(target, result.getTarget());
        assertEquals(1, result.getFoundIndex());
        assertEquals(1, result.getComparisons());
        assertEquals(1, results.size());
        assertEquals(result, results.get(0));
    }

    @Test
    public void testSearchNotFound() {
        // Arrange
        int[] sortedArray = {1, 2, 3, 4, 5};
        int target = 6;

        // Act
        SearchResult result = binarySearchService.search(sortedArray, target);

        // Assert
        assertNotNull(result);
        assertFalse(result.isFound());
        assertEquals(target, result.getTarget());
        assertEquals(-1, result.getFoundIndex());
        assertEquals(3, result.getComparisons());
        assertEquals(1, results.size());
        assertEquals(result, results.get(0));
    }

    @Test
    public void testSearchEmptyArray() {
        // Arrange
        int[] sortedArray = {};
        int target = 3;

        // Act
        SearchResult result = binarySearchService.search(sortedArray, target);

        // Assert
        assertNotNull(result);
        assertFalse(result.isFound());
        assertEquals(target, result.getTarget());
        assertEquals(-1, result.getFoundIndex());
        assertEquals(0, result.getComparisons());
        assertEquals(1, results.size());
        assertEquals(result, results.get(0));
    }

    @Test
    public void testSearchNullArray() {
        // Arrange
        int[] sortedArray = null;
        int target = 3;

        // Act and Assert
        assertThrows(NullPointerException.class, () -> binarySearchService.search(sortedArray, target));
    }

    @Test
    public void testSearchNullTarget() {
        // Arrange
        int[] sortedArray = {1, 2, 3, 4, 5};
        Integer target = null;

        // Act and Assert
        assertThrows(NullPointerException.class, () -> binarySearchService.search(sortedArray, target));
    }

    @Test
    public void testGetResultFound() {
        // Arrange
        SearchResult result = new SearchResult(null, new int[]{1, 2, 3}, 3, true, 1, 1, new ArrayList<>());
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
        SearchResult result1 = new SearchResult(null, new int[]{1, 2, 3}, 3, true, 1, 1, new ArrayList<>());
        SearchResult result2 = new SearchResult(null, new int[]{4, 5, 6}, 6, true, 1, 1, new ArrayList<>());
        when(repository.findAll()).thenReturn(Arrays.asList(result1, result2));

        // Act
        List<SearchResult> allResults = binarySearchService.getAllResults();

        // Assert
        assertEquals(2, allResults.size());
        assertEquals(result1, allResults.get(0));
        assertEquals(result2, allResults.get(1));
    }
}