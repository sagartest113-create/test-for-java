package com.testcraft.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testcraft.demo.config.SecurityConfig;
import com.testcraft.demo.dto.BinarySearchRequest;
import com.testcraft.demo.dto.BinarySearchResponse;
import com.testcraft.demo.model.SearchResult;
import com.testcraft.demo.service.BinarySearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class BinarySearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BinarySearchService binarySearchService;

    @InjectMocks
    private BinarySearchController binarySearchController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Search by POST request with valid input")
    void testSearchPostValidInput() throws Exception {
        BinarySearchRequest request = new BinarySearchRequest(new int[]{1, 2, 3, 4, 5}, 3);
        SearchResult expectedResult = new SearchResult(null, new int[]{1, 2, 3, 4, 5}, 3, true, 2, 1, List.of("Step 1: mid=2 value=3 → FOUND"));
        when(binarySearchService.search(any(int[].class), anyInt())).thenReturn(expectedResult);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/binary-search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.found").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.index").value(2));
    }

    @Test
    @DisplayName("Search by POST request with invalid input (null array)")
    void testSearchPostInvalidInputNullArray() throws Exception {
        BinarySearchRequest request = new BinarySearchRequest(null, 3);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/binary-search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Search by POST request with invalid input (empty array)")
    void testSearchPostInvalidInputEmptyArray() throws Exception {
        BinarySearchRequest request = new BinarySearchRequest(new int[]{}, 3);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/binary-search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Search by POST request with invalid input (target out of range)")
    void testSearchPostInvalidInputTargetOutOfRange() throws Exception {
        BinarySearchRequest request = new BinarySearchRequest(new int[]{1, 2, 3, 4, 5}, 10);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/binary-search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get result by ID")
    void testGetResultById() throws Exception {
        SearchResult expectedResult = new SearchResult(1L, new int[]{1, 2, 3, 4, 5}, 3, true, 2, 1, List.of("Step 1: mid=2 value=3 → FOUND"));
        when(binarySearchService.getResult(anyLong())).thenReturn(Optional.of(expectedResult));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/binary-search/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.found").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.index").value(2));
    }

    @Test
    @DisplayName("Get result by ID not found")
    void testGetResultByIdNotFound() throws Exception {
        when(binarySearchService.getResult(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/binary-search/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get all results")
    void testGetAllResults() throws Exception {
        SearchResult expectedResult1 = new SearchResult(1L, new int[]{1, 2, 3, 4, 5}, 3, true, 2, 1, List.of("Step 1: mid=2 value=3 → FOUND"));
        SearchResult expectedResult2 = new SearchResult(2L, new int[]{1, 2, 3, 4, 5}, 4, true, 3, 1, List.of("Step 1: mid=3 value=4 → FOUND"));
        when(binarySearchService.getAllResults()).thenReturn(List.of(expectedResult1, expectedResult2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/binary-search"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].found").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].index").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].found").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].index").value(3));
    }
}