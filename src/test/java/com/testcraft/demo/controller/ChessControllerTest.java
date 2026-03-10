package com.testcraft.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testcraft.demo.config.SecurityConfig;
import com.testcraft.demo.dto.ChessBoardRequest;
import com.testcraft.demo.dto.ChessMoveResponse;
import com.testcraft.demo.model.GameAnalysis;
import com.testcraft.demo.service.ChessEngineService;
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
@Import(SecurityConfig.class)
public class ChessControllerTest {

    private static final String BASE_URL = "/api/chess";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChessEngineService engineService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ChessController(engineService)).build();
    }

    @Test
    @DisplayName("Get next move with valid input")
    public void getNextMove_ValidInput_Returns201() throws Exception {
        // Arrange
        String[][] board = new String[][]{
                {"rnbqkbnr", "pppppppp", ".......", ".......", ".......", ".......", ".......", "PPPPPPPP"},
                {".......", ".......", ".......", ".......", ".......", ".......", ".......", "......."},
                {".......", ".......", ".......", ".......", ".......", ".......", ".......", "......."},
                {".......", ".......", ".......", ".......", ".......", ".......", ".......", "......."},
                {".......", ".......", ".......", ".......", ".......", ".......", ".......", "......."},
                {".......", ".......", ".......", ".......", ".......", ".......", ".......", "......."},
                {".......", ".......", ".......", ".......", ".......", ".......", ".......", "......."},
                {"RNBQKBNR", "pppppppp", ".......", ".......", ".......", ".......", ".......", "pppppppp"}
        };

        // Act
        mockMvc.perform(post(BASE_URL + "/next-move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ChessBoardRequest(board))))
                .andExpect(status().isCreated());

        // Assert
        mockMvc.perform(get(BASE_URL + "/analysis/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get next move with invalid input")
    public void getNextMove_InvalidInput_Returns400() throws Exception {
        // Arrange
        String[][] board = new String[][]{
                {"rnbqkbnr", "pppppppp", ".......", ".......", ".......", ".......", ".......", "PPPPPPPP"},
                {".......", ".......", ".......", ".......", ".......", ".......", ".......", "......."},
                {".......", ".......", ".......", ".......", ".......", ".......", ".......", "......."},
                {".......", ".......", ".......", ".......", ".......", ".......", ".......", "......."},
                {".......", ".......", ".......", ".......", ".......", ".......", ".......", "......."},
                {".......", ".......", ".......", ".......", ".......", ".......", ".......", "......."},
                {".......", ".......", ".......", ".......", ".......", ".......", ".......", "......."},
                {"RNBQKBNR", "pppppppp", ".......", ".......", ".......", ".......", ".......", "pppppppp"}
        };

        // Act
        mockMvc.perform(post(BASE_URL + "/next-move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ChessBoardRequest(board))))
                .andExpect(status().isBadRequest());

        // Assert
        mockMvc.perform(get(BASE_URL + "/analysis/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get analysis by id with valid input")
    public void getAnalysis_ValidInput_Returns200() throws Exception {
        // Arrange
        String[][] board = new String[][]{
                {"rnbqkbnr", "pppppppp", ".......", ".......", ".......", ".......", ".......", "PPPPPPPP"},
                {".......", ".......", ".......", ".......", ".......", ".......", ".......", "......."},
                {".......", ".......", ".......", ".......", ".......", ".......", ".......", "......."},
                {".......", ".......", ".......", ".......", ".......", ".......", ".......", "......."},
                {".......", ".......", ".......", ".......", ".......", ".......", ".......", "......."},
                {".......", ".......", ".......", ".......", ".......", ".......", ".......", "......."},
                {".......", ".......", ".......", ".......", ".......", ".......", ".......", "......."},
                {"RNBQKBNR", "pppppppp", ".......", ".......", ".......", ".......", ".......", "pppppppp"}
        };

        // Act
        mockMvc.perform(post(BASE_URL + "/next-move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ChessBoardRequest(board))))
                .andExpect(status().isCreated());

        // Assert
        mockMvc.perform(get(BASE_URL + "/analysis/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get analysis by id with invalid input")
    public void getAnalysis_InvalidInput_Returns404() throws Exception {
        // Act
        mockMvc.perform(get(BASE_URL + "/analysis/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get all analyses")
    public void getAllAnalyses_Returns200() throws Exception {
        // Act
        mockMvc.perform(get(BASE_URL + "/analysis"))
                .andExpect(status().isOk());
    }
}