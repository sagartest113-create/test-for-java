package com.testcraft.demo.repository;

import com.testcraft.demo.model.SearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringJUnitWebConfig
public class SearchResultRepositoryTest {

    @Mock
    private List<SearchResult> results;

    @Mock
    private AtomicLong idGenerator;

    @InjectMocks
    private SearchResultRepository repository;

    @BeforeEach
    public void setup() {
        when(idGenerator.getAndIncrement()).thenReturn(1L, 2L, 3L);
    }

    @Test
    public void testSave() {
        // Arrange
        SearchResult result = new SearchResult();
        result.setName("Test Result");

        // Act
        SearchResult savedResult = repository.save(result);

        // Assert
        assertNotNull(savedResult);
        assertEquals(1L, savedResult.getId());
        assertTrue(repository.findAll().contains(savedResult));
    }

    @Test
    public void testSaveMultiple() {
        // Arrange
        SearchResult result1 = new SearchResult();
        result1.setName("Test Result 1");

        SearchResult result2 = new SearchResult();
        result2.setName("Test Result 2");

        // Act
        repository.save(result1);
        repository.save(result2);

        // Assert
        assertEquals(2, repository.findAll().size());
        assertTrue(repository.findAll().contains(result1));
        assertTrue(repository.findAll().contains(result2));
    }

    @Test
    public void testFindById() {
        // Arrange
        SearchResult result = new SearchResult();
        result.setName("Test Result");
        result.setId(1L);

        // Act
        when(results.stream().filter(r -> r.getId().equals(1L)).findFirst()).thenReturn(Optional.of(result));

        // Assert
        assertTrue(repository.findById(1L).isPresent());
        assertEquals(result, repository.findById(1L).get());
    }

    @Test
    public void testFindByIdNotFound() {
        // Arrange
        SearchResult result = new SearchResult();
        result.setName("Test Result");
        result.setId(1L);

        // Act
        when(results.stream().filter(r -> r.getId().equals(1L)).findFirst()).thenReturn(Optional.empty());

        // Assert
        assertFalse(repository.findById(1L).isPresent());
    }

    @Test
    public void testFindAll() {
        // Arrange
        SearchResult result1 = new SearchResult();
        result1.setName("Test Result 1");

        SearchResult result2 = new SearchResult();
        result2.setName("Test Result 2");

        // Act
        repository.save(result1);
        repository.save(result2);

        // Assert
        assertEquals(2, repository.findAll().size());
        assertTrue(repository.findAll().contains(result1));
        assertTrue(repository.findAll().contains(result2));
    }

    @Test
    public void testFindAllEmpty() {
        // Act
        List<SearchResult> results = repository.findAll();

        // Assert
        assertTrue(results.isEmpty());
    }
}