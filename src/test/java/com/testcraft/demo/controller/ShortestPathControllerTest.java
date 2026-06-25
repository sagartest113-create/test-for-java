package com.testcraft.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testcraft.demo.config.SecurityConfig;
import com.testcraft.demo.dto.GridRequest;
import com.testcraft.demo.dto.ShortestPathResponse;
import com.testcraft.demo.model.PathComputation;
import com.testcraft.demo.service.ShortestPathService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class ShortestPathControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShortestPathService shortestPathService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("Compute shortest path with valid grid")
    public void computeShortestPath_ValidGrid() throws Exception {
        GridRequest request = new GridRequest(
                new int[][]{
                        {1, 2, 3, 4, 5, 6, 7, 8, 9},
                        {10, 11, 12, 13, 14, 15, 16, 17, 18},
                        {19, 20, 21, 22, 23, 24, 25, 26, 27},
                        {28, 29, 30, 31, 32, 33, 34, 35, 36},
                        {37, 38, 39, 40, 41, 42, 43, 44, 45},
                        {46, 47, 48, 49, 50, 51, 52, 53, 54},
                        {55, 56, 57, 58, 59, 60, 61, 62, 63},
                        {64, 65, 66, 67, 68, 69, 70, 71, 72},
                        {73, 74, 75, 76, 77, 78, 79, 80, 81}
                }
        );

        mockMvc.perform(post("/api/shortest-path")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.path").exists())
                .andExpect(jsonPath("$.totalCost").exists())
                .andExpect(jsonPath("$.reachable").exists());
    }

    @Test
    @DisplayName("Compute shortest path with invalid grid")
    public void computeShortestPath_InvalidGrid() throws Exception {
        GridRequest request = new GridRequest(
                new int[][]{
                        {1, 2, 3, 4, 5, 6, 7, 8},
                        {10, 11, 12, 13, 14, 15, 16, 17, 18},
                        {19, 20, 21, 22, 23, 24, 25, 26, 27},
                        {28, 29, 30, 31, 32, 33, 34, 35, 36},
                        {37, 38, 39, 40, 41, 42, 43, 44, 45},
                        {46, 47, 48, 49, 50, 51, 52, 53, 54},
                        {55, 56, 57, 58, 59, 60, 61, 62, 63},
                        {64, 65, 66, 67, 68, 69, 70, 71, 72},
                        {73, 74, 75, 76, 77, 78, 79, 80}
                }
        );

        mockMvc.perform(post("/api/shortest-path")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get computation by id")
    public void getComputation_ValidId() throws Exception {
        PathComputation computation = new PathComputation(1L, new int[][]{
                {1, 2, 3, 4, 5, 6, 7, 8, 9},
                {10, 11, 12, 13, 14, 15, 16, 17, 18},
                {19, 20, 21, 22, 23, 24, 25, 26, 27},
                {28, 29, 30, 31, 32, 33, 34, 35, 36},
                {37, 38, 39, 40, 41, 42, 43, 44, 45},
                {46, 47, 48, 49, 50, 51, 52, 53, 54},
                {55, 56, 57, 58, 59, 60, 61, 62, 63},
                {64, 65, 66, 67, 68, 69, 70, 71, 72},
                {73, 74, 75, 76, 77, 78, 79, 80, 81}
        }, new List<>(), 0, true);

        mockMvc.perform(get("/api/shortest-path/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.path").exists())
                .andExpect(jsonPath("$.totalCost").value(0))
                .andExpect(jsonPath("$.reachable").value(true));
    }

    @Test
    @DisplayName("Get computation by id not found")
    public void getComputation_NotFound() throws Exception {
        mockMvc.perform(get("/api/shortest-path/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get all computations")
    public void getAllComputations() throws Exception {
        mockMvc.perform(get("/api/shortest-path"))
                .andExpect(status().isOk());
    }
}