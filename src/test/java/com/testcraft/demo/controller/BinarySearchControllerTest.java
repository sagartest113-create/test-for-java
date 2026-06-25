package com.testcraft.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testcraft.demo.config.TestSecurityConfig;
import com.testcraft.demo.dto.BinarySearchRequest;
import com.testcraft.demo.model.SearchResult;
import com.testcraft.demo.service.BinarySearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BinarySearchController.class)
@Import(TestSecurityConfig.class)
class BinarySearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BinarySearchService searchService;

    // -----------------------------------------------------------------------
    // POST /api/binary-search
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/binary-search — target found returns 201 with found=true")
    void search_targetFound_returns201() throws Exception {
        SearchResult result = new SearchResult(1L, new int[]{1, 3, 5, 7}, 5, true, 2, 2,
                List.of("Step 1: mid=1 value=3 < 5 → search right",
                        "Step 2: mid=2 value=5 → FOUND"));

        when(searchService.search(any(int[].class), eq(5))).thenReturn(result);

        String body = objectMapper.writeValueAsString(new BinarySearchRequest(new int[]{1, 3, 5, 7}, 5));

        mockMvc.perform(post("/api/binary-search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.found").value(true))
                .andExpect(jsonPath("$.index").value(2))
                .andExpect(jsonPath("$.target").value(5))
                .andExpect(jsonPath("$.comparisons").value(2))
                .andExpect(jsonPath("$.steps", hasSize(2)));
    }

    @Test
    @DisplayName("POST /api/binary-search — target not found returns 201 with found=false and index=-1")
    void search_targetNotFound_returns201() throws Exception {
        SearchResult result = new SearchResult(2L, new int[]{1, 3, 5, 7}, 4, false, -1, 3,
                List.of("Step 1: mid=1 value=3 < 4 → search right",
                        "Step 2: mid=2 value=5 > 4 → search left",
                        "Target 4 not found after 3 comparisons"));

        when(searchService.search(any(int[].class), eq(4))).thenReturn(result);

        String body = objectMapper.writeValueAsString(new BinarySearchRequest(new int[]{1, 3, 5, 7}, 4));

        mockMvc.perform(post("/api/binary-search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.found").value(false))
                .andExpect(jsonPath("$.index").value(-1))
                .andExpect(jsonPath("$.target").value(4));
    }

    @Test
    @DisplayName("POST /api/binary-search — single element array, target matches")
    void search_singleElementFound_returns201() throws Exception {
        SearchResult result = new SearchResult(3L, new int[]{42}, 42, true, 0, 1,
                List.of("Step 1: mid=0 value=42 → FOUND"));

        when(searchService.search(any(int[].class), eq(42))).thenReturn(result);

        String body = objectMapper.writeValueAsString(new BinarySearchRequest(new int[]{42}, 42));

        mockMvc.perform(post("/api/binary-search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.found").value(true))
                .andExpect(jsonPath("$.index").value(0))
                .andExpect(jsonPath("$.comparisons").value(1));
    }

    @Test
    @DisplayName("POST /api/binary-search — null array returns 400")
    void search_nullArray_returns400() throws Exception {
        mockMvc.perform(post("/api/binary-search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sortedArray\":null,\"target\":5}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/binary-search — empty array returns 400")
    void search_emptyArray_returns400() throws Exception {
        mockMvc.perform(post("/api/binary-search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sortedArray\":[],\"target\":5}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/binary-search — response includes searchId from service")
    void search_responseContainsSearchId() throws Exception {
        SearchResult result = new SearchResult(99L, new int[]{10, 20, 30}, 20, true, 1, 1,
                List.of("Step 1: mid=1 value=20 → FOUND"));

        when(searchService.search(any(int[].class), eq(20))).thenReturn(result);

        String body = objectMapper.writeValueAsString(new BinarySearchRequest(new int[]{10, 20, 30}, 20));

        mockMvc.perform(post("/api/binary-search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.searchId").value(99));
    }

    // -----------------------------------------------------------------------
    // GET /api/binary-search/{id}
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/binary-search/{id} — existing id returns 200")
    void getResult_existingId_returns200() throws Exception {
        SearchResult result = new SearchResult(1L, new int[]{2, 4, 6}, 4, true, 1, 1,
                List.of("Step 1: mid=1 value=4 → FOUND"));

        when(searchService.getResult(1L)).thenReturn(Optional.of(result));

        mockMvc.perform(get("/api/binary-search/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.searchId").value(1))
                .andExpect(jsonPath("$.found").value(true))
                .andExpect(jsonPath("$.target").value(4));
    }

    @Test
    @DisplayName("GET /api/binary-search/{id} — unknown id returns 404")
    void getResult_unknownId_returns404() throws Exception {
        when(searchService.getResult(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/binary-search/999"))
                .andExpect(status().isNotFound());
    }

    // -----------------------------------------------------------------------
    // GET /api/binary-search
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/binary-search — returns all results as a list")
    void getAllResults_returnsAll() throws Exception {
        SearchResult r1 = new SearchResult(1L, new int[]{1, 2, 3}, 2, true, 1, 1, List.of("Step 1: FOUND"));
        SearchResult r2 = new SearchResult(2L, new int[]{1, 2, 3}, 9, false, -1, 2, List.of("not found"));

        when(searchService.getAllResults()).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/binary-search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].searchId").value(1))
                .andExpect(jsonPath("$[1].searchId").value(2));
    }

    @Test
    @DisplayName("GET /api/binary-search — empty list when no results exist")
    void getAllResults_empty() throws Exception {
        when(searchService.getAllResults()).thenReturn(List.of());

        mockMvc.perform(get("/api/binary-search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
