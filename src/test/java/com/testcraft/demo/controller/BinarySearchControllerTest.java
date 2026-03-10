package com.testcraft.demo.controller;

import com.testcraft.demo.dto.BinarySearchRequest;
import com.testcraft.demo.dto.BinarySearchResponse;
import com.testcraft.demo.model.SearchResult;
import com.testcraft.demo.service.BinarySearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BinarySearchControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BinarySearchService searchService;

    @InjectMocks
    private BinarySearchController binarySearchController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(binarySearchController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testSearchSuccess() throws Exception {
        // Arrange
        BinarySearchRequest request = new BinarySearchRequest();
        request.setSortedArray(new int[]{1, 2, 3, 4, 5});
        request.setTarget(3);

        SearchResult result = new SearchResult();
        result.setId(1L);
        result.setTarget(3);
        result.setFound(true);
        result.setIndex(2);
        result.setComparisons(2);
        result.setSteps(2);

        when(searchService.search(any(int[].class), any(Integer.class))).thenReturn(result);

        // Act
        mockMvc.perform(post("/api/binary-search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.target").value(3))
                .andExpect(jsonPath("$.found").value(true))
                .andExpect(jsonPath("$.index").value(2))
                .andExpect(jsonPath("$.comparisons").value(2))
                .andExpect(jsonPath("$.steps").value(2));
    }

    @Test
    public void testSearchFailure() throws Exception {
        // Arrange
        BinarySearchRequest request = new BinarySearchRequest();
        request.setSortedArray(new int[]{1, 2, 3, 4, 5});
        request.setTarget(6);

        SearchResult result = new SearchResult();
        result.setId(1L);
        result.setTarget(3);
        result.setFound(true);
        result.setIndex(2);
        result.setComparisons(2);
        result.setSteps(2);

        when(searchService.search(any(int[].class), any(Integer.class))).thenReturn(result);

        // Act
        mockMvc.perform(post("/api/binary-search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.target").value(3))
                .andExpect(jsonPath("$.found").value(true))
                .andExpect(jsonPath("$.index").value(2))
                .andExpect(jsonPath("$.comparisons").value(2))
                .andExpect(jsonPath("$.steps").value(2));
    }

    @Test
    public void testSearchEdgeCaseEmptyArray() throws Exception {
        // Arrange
        BinarySearchRequest request = new BinarySearchRequest();
        request.setSortedArray(new int[]{});
        request.setTarget(3);

        SearchResult result = new SearchResult();
        result.setId(1L);
        result.setTarget(3);
        result.setFound(false);
        result.setIndex(-1);
        result.setComparisons(0);
        result.setSteps(0);

        when(searchService.search(any(int[].class), any(Integer.class))).thenReturn(result);

        // Act
        mockMvc.perform(post("/api/binary-search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.target").value(3))
                .andExpect(jsonPath("$.found").value(false))
                .andExpect(jsonPath("$.index").value(-1))
                .andExpect(jsonPath("$.comparisons").value(0))
                .andExpect(jsonPath("$.steps").value(0));
    }

    @Test
    public void testSearchEdgeCaseNullArray() throws Exception {
        // Arrange
        BinarySearchRequest request = new BinarySearchRequest();
        request.setSortedArray(null);
        request.setTarget(3);

        SearchResult result = new SearchResult();
        result.setId(1L);
        result.setTarget(3);
        result.setFound(false);
        result.setIndex(-1);
        result.setComparisons(0);
        result.setSteps(0);

        when(searchService.search(any(int[].class), any(Integer.class))).thenReturn(result);

        // Act
        mockMvc.perform(post("/api/binary-search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.target").value(3))
                .andExpect(jsonPath("$.found").value(false))
                .andExpect(jsonPath("$.index").value(-1))
                .andExpect(jsonPath("$.comparisons").value(0))
                .andExpect(jsonPath("$.steps").value(0));
    }

    @Test
    public void testGetResultSuccess() throws Exception {
        // Arrange
        SearchResult result = new SearchResult();
        result.setId(1L);
        result.setTarget(3);
        result.setFound(true);
        result.setIndex(2);
        result.setComparisons(2);
        result.setSteps(2);

        when(searchService.getResult(any(Long.class))).thenReturn(Optional.of(result));

        // Act
        mockMvc.perform(get("/api/binary-search/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.target").value(3))
                .andExpect(jsonPath("$.found").value(true))
                .andExpect(jsonPath("$.index").value(2))
                .andExpect(jsonPath("$.comparisons").value(2))
                .andExpect(jsonPath("$.steps").value(2));
    }

    @Test
    public void testGetResultFailure() throws Exception {
        // Arrange
        when(searchService.getResult(any(Long.class))).thenReturn(Optional.empty());

        // Act
        mockMvc.perform(get("/api/binary-search/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllResultsSuccess() throws Exception {
        // Arrange
        SearchResult result1 = new SearchResult();
        result1.setId(1L);
        result1.setTarget(3);
        result1.setFound(true);
        result1.setIndex(2);
        result1.setComparisons(2);
        result1.setSteps(2);

        SearchResult result2 = new SearchResult();
        result2.setId(2L);
        result2.setTarget(4);
        result2.setFound(true);
        result2.setIndex(3);
        result2.setComparisons(3);
        result2.setSteps(3);

        when(searchService.getAllResults()).thenReturn(List.of(result1, result2));

        // Act
        mockMvc.perform(get("/api/binary-search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].target").value(3))
                .andExpect(jsonPath("$[0].found").value(true))
                .andExpect(jsonPath("$[0].index").value(2))
                .andExpect(jsonPath("$[0].comparisons").value(2))
                .andExpect(jsonPath("$[0].steps").value(2))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].target").value(4))
                .andExpect(jsonPath("$[1].found").value(true))
                .andExpect(jsonPath("$[1].index").value(3))
                .andExpect(jsonPath("$[1].comparisons").value(3))
                .andExpect(jsonPath("$[1].steps").value(3));
    }

    @Test
    public void testGetAllResultsFailure() throws Exception {
        // Arrange
        when(searchService.getAllResults()).thenReturn(List.of());

        // Act
        mockMvc.perform(get("/api/binary-search"))
                .andExpect(status().isOk());
    }
}