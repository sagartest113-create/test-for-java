package com.testcraft.demo.controller;

import com.testcraft.demo.dto.GridRequest;
import com.testcraft.demo.dto.ShortestPathResponse;
import com.testcraft.demo.model.PathComputation;
import com.testcraft.demo.service.ShortestPathService;
import jakarta.validation.Valid;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ShortestPathControllerTest {

    @Mock
    private ShortestPathService shortestPathService;

    @InjectMocks
    private ShortestPathController shortestPathController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(shortestPathController).build();
    }

    @Test
    public void testComputeShortestPath() throws Exception {
        PathComputation computation = new PathComputation();
        computation.setId(1L);
        computation.setPath("path");
        computation.setTotalCost(10.0);
        computation.setReachable(true);

        when(shortestPathService.computeShortestPath(any(GridRequest.class))).thenReturn(computation);

        GridRequest request = new GridRequest();
        request.setGrid("grid");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/shortest-path")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.path").value("path"))
                .andExpect(jsonPath("$.totalCost").value(10.0))
                .andExpect(jsonPath("$.reachable").value(true));
    }

    @Test
    public void testGetComputation() throws Exception {
        PathComputation computation = new PathComputation();
        computation.setId(1L);
        computation.setPath("path");
        computation.setTotalCost(10.0);
        computation.setReachable(true);

        when(shortestPathService.getComputation(1L)).thenReturn(computation);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/shortest-path/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.path").value("path"))
                .andExpect(jsonPath("$.totalCost").value(10.0))
                .andExpect(jsonPath("$.reachable").value(true));
    }

    @Test
    public void testGetAllComputations() throws Exception {
        PathComputation computation1 = new PathComputation();
        computation1.setId(1L);
        computation1.setPath("path1");
        computation1.setTotalCost(10.0);
        computation1.setReachable(true);

        PathComputation computation2 = new PathComputation();
        computation2.setId(2L);
        computation2.setPath("path2");
        computation2.setTotalCost(20.0);
        computation2.setReachable(true);

        when(shortestPathService.getAllComputations()).thenReturn(List.of(computation1, computation2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/shortest-path"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].path").value("path1"))
                .andExpect(jsonPath("$[0].totalCost").value(10.0))
                .andExpect(jsonPath("$[0].reachable").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].path").value("path2"))
                .andExpect(jsonPath("$[1].totalCost").value(20.0))
                .andExpect(jsonPath("$[1].reachable").value(true));
    }

    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}