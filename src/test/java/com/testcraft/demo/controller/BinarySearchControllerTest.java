package com.testcraft.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testcraft.demo.config.SecurityConfig;
import com.testcraft.demo.dto.BinarySearchRequest;
import com.testcraft.demo.dto.BinarySearchResponse;
import com.testcraft.demo.model.SearchResult;
import com.testcraft.demo.service.BinarySearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class BinarySearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BinarySearchService binarySearchService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new BinarySearchController(binarySearchService)).build();
    }

    @Test
    @DisplayName("POST /api/binary-search: valid input")
    public void testSearchValidInput() throws Exception {
        BinarySearchRequest request = new BinarySearchRequest(new int[]{1, 2, 3, 4, 5}, 3);
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/binary-search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.found").value(true))
                .andExpect(jsonPath("$.index").value(2));
    }

    @Test
    @DisplayName("POST /api/binary-search: invalid input")
    public void testSearchInvalidInput() throws Exception {
        BinarySearchRequest request = new BinarySearchRequest(new int[]{1, 2, 3, 4, 5}, 6);
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/binary-search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.found").value(false));
    }

    @Test
    @DisplayName("POST /api/binary-search: empty array")
    public void testSearchEmptyArray() throws Exception {
        BinarySearchRequest request = new BinarySearchRequest(new int[]{}, 3);
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/binary-search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.found").value(false));
    }

    @Test
    @DisplayName("GET /api/binary-search/{id}: valid id")
    public void testGetResultValidId() throws Exception {
        SearchResult result = binarySearchService.search(new int[]{1, 2, 3, 4, 5}, 3);
        mockMvc.perform(get("/api/binary-search/" + result.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.found").value(true))
                .andExpect(jsonPath("$.index").value(2));
    }

    @Test
    @DisplayName("GET /api/binary-search/{id}: invalid id")
    public void testGetResultInvalidId() throws Exception {
        mockMvc.perform(get("/api/binary-search/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/binary-search: valid results")
    public void testGetAllResultsValidResults() throws Exception {
        binarySearchService.search(new int[]{1, 2, 3, 4, 5}, 3);
        mockMvc.perform(get("/api/binary-search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/binary-search: no results")
    public void testGetAllResultsNoResults() throws Exception {
        mockMvc.perform(get("/api/binary-search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}