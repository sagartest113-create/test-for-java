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
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class BinarySearchControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BinarySearchService binarySearchService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Search with valid input")
    public void searchValidInput() throws Exception {
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
    @DisplayName("Search with invalid input (null array)")
    public void searchInvalidInputNullArray() throws Exception {
        BinarySearchRequest request = new BinarySearchRequest(null, 3);
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/binary-search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Search with invalid input (empty array)")
    public void searchInvalidInputEmptyArray() throws Exception {
        BinarySearchRequest request = new BinarySearchRequest(new int[]{}, 3);
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/binary-search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Search with invalid input (target not found)")
    public void searchInvalidInputTargetNotFound() throws Exception {
        BinarySearchRequest request = new BinarySearchRequest(new int[]{1, 2, 3, 4, 5}, 6);
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/binary-search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.found").value(false));
    }

    @Test
    @DisplayName("Get result by id")
    public void getResultById() throws Exception {
        SearchResult result = binarySearchService.search(new int[]{1, 2, 3, 4, 5}, 3);
        mockMvc.perform(get("/api/binary-search/{id}", result.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.found").value(true));
    }

    @Test
    @DisplayName("Get result by id (not found)")
    public void getResultByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/binary-search/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get all results")
    public void getAllResults() throws Exception {
        SearchResult result1 = binarySearchService.search(new int[]{1, 2, 3, 4, 5}, 3);
        SearchResult result2 = binarySearchService.search(new int[]{1, 2, 3, 4, 5}, 4);
        mockMvc.perform(get("/api/binary-search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].found").value(true))
                .andExpect(jsonPath("$.[1].found").value(true));
    }
}