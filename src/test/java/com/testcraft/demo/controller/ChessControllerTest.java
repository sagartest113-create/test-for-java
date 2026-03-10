package com.testcraft.demo.controller;

import com.testcraft.demo.dto.ChessBoardRequest;
import com.testcraft.demo.dto.ChessMoveResponse;
import com.testcraft.demo.model.GameAnalysis;
import com.testcraft.demo.service.ChessEngineService;
import jakarta.validation.Valid;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ChessControllerTest {

    @Mock
    private ChessEngineService engineService;

    @InjectMocks
    private ChessController controller;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void getNextMove() throws Exception {
        GameAnalysis analysis = new GameAnalysis();
        analysis.setId(1L);
        analysis.setBestMoveFrom("A2");
        analysis.setBestMoveTo("A4");
        analysis.setPieceMoved("Pawn");
        analysis.setPieceCaptured("None");
        analysis.setEvaluationScore(1.0);
        analysis.setWinProbability(0.5);
        analysis.setWinnable(true);
        analysis.setMessage("Test message");

        when(engineService.analyzePosition(any(ChessBoardRequest.class))).thenReturn(analysis);

        ChessBoardRequest request = new ChessBoardRequest();
        request.setBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");

        mockMvc.perform(post("/api/chess/next-move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bestMoveFrom").value("A2"))
                .andExpect(jsonPath("$.bestMoveTo").value("A4"))
                .andExpect(jsonPath("$.pieceMoved").value("Pawn"))
                .andExpect(jsonPath("$.pieceCaptured").value("None"))
                .andExpect(jsonPath("$.evaluationScore").value(1.0))
                .andExpect(jsonPath("$.winProbability").value(0.5))
                .andExpect(jsonPath("$.winnable").value(true))
                .andExpect(jsonPath("$.message").value("Test message"));
    }

    @Test
    public void getAnalysis() throws Exception {
        GameAnalysis analysis = new GameAnalysis();
        analysis.setId(1L);
        analysis.setBestMoveFrom("A2");
        analysis.setBestMoveTo("A4");
        analysis.setPieceMoved("Pawn");
        analysis.setPieceCaptured("None");
        analysis.setEvaluationScore(1.0);
        analysis.setWinProbability(0.5);
        analysis.setWinnable(true);
        analysis.setMessage("Test message");

        when(engineService.getAnalysis(1L)).thenReturn(analysis);

        mockMvc.perform(get("/api/chess/analysis/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bestMoveFrom").value("A2"))
                .andExpect(jsonPath("$.bestMoveTo").value("A4"))
                .andExpect(jsonPath("$.pieceMoved").value("Pawn"))
                .andExpect(jsonPath("$.pieceCaptured").value("None"))
                .andExpect(jsonPath("$.evaluationScore").value(1.0))
                .andExpect(jsonPath("$.winProbability").value(0.5))
                .andExpect(jsonPath("$.winnable").value(true))
                .andExpect(jsonPath("$.message").value("Test message"));
    }

    @Test
    public void getAllAnalyses() throws Exception {
        GameAnalysis analysis1 = new GameAnalysis();
        analysis1.setId(1L);
        analysis1.setBestMoveFrom("A2");
        analysis1.setBestMoveTo("A4");
        analysis1.setPieceMoved("Pawn");
        analysis1.setPieceCaptured("None");
        analysis1.setEvaluationScore(1.0);
        analysis1.setWinProbability(0.5);
        analysis1.setWinnable(true);
        analysis1.setMessage("Test message");

        GameAnalysis analysis2 = new GameAnalysis();
        analysis2.setId(2L);
        analysis2.setBestMoveFrom("B2");
        analysis2.setBestMoveTo("B4");
        analysis2.setPieceMoved("Pawn");
        analysis2.setPieceCaptured("None");
        analysis2.setEvaluationScore(2.0);
        analysis2.setWinProbability(0.6);
        analysis2.setWinnable(true);
        analysis2.setMessage("Test message");

        when(engineService.getAllAnalyses()).thenReturn(List.of(analysis1, analysis2));

        mockMvc.perform(get("/api/chess/analysis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].bestMoveFrom").value("A2"))
                .andExpect(jsonPath("$[0].bestMoveTo").value("A4"))
                .andExpect(jsonPath("$[0].pieceMoved").value("Pawn"))
                .andExpect(jsonPath("$[0].pieceCaptured").value("None"))
                .andExpect(jsonPath("$[0].evaluationScore").value(1.0))
                .andExpect(jsonPath("$[0].winProbability").value(0.5))
                .andExpect(jsonPath("$[0].winnable").value(true))
                .andExpect(jsonPath("$[0].message").value("Test message"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].bestMoveFrom").value("B2"))
                .andExpect(jsonPath("$[1].bestMoveTo").value("B4"))
                .andExpect(jsonPath("$[1].pieceMoved").value("Pawn"))
                .andExpect(jsonPath("$[1].pieceCaptured").value("None"))
                .andExpect(jsonPath("$[1].evaluationScore").value(2.0))
                .andExpect(jsonPath("$[1].winProbability").value(0.6))
                .andExpect(jsonPath("$[1].winnable").value(true))
                .andExpect(jsonPath("$[1].message").value("Test message"));
    }

    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}