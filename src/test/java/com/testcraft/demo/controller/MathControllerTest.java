package com.testcraft.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testcraft.demo.dto.FizzBuzzResponse;
import com.testcraft.demo.service.MathService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MathControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @WithMockUser
    @Test
    void testFactorial_HappyPath() throws Exception {
        // Arrange
        int n = 5;
        String url = "/api/math/factorial/" + n;

        // Act & Assert
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.n").value(n))
                .andExpect(jsonPath("$.factorial").value(120));
    }

    @WithMockUser
    @Test
    void testFactorial_BadRequest() throws Exception {
        // Arrange
        int n = -5;
        String url = "/api/math/factorial/" + n;

        // Act & Assert
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Input must be non-negative, got: " + n));
    }

    @WithMockUser
    @Test
    void testIsPrime_HappyPath() throws Exception {
        // Arrange
        int n = 7;
        String url = "/api/math/prime/" + n;

        // Act & Assert
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.n").value(n))
                .andExpect(jsonPath("$.prime").value(true));
    }

    @WithMockUser
    @Test
    void testIsPrime_BadRequest() throws Exception {
        // Arrange
        int n = -7;
        String url = "/api/math/prime/" + n;

        // Act & Assert
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Input must be non-negative, got: " + n));
    }

    @WithMockUser
    @Test
    void testFizzBuzz_HappyPath() throws Exception {
        // Arrange
        int n = 15;
        String url = "/api/math/fizzbuzz/" + n;

        // Act & Assert
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.n").value(n))
                .andExpect(jsonPath("$.result", hasSize(15)));
    }

    @WithMockUser
    @Test
    void testFizzBuzz_BadRequest() throws Exception {
        // Arrange
        int n = -15;
        String url = "/api/math/fizzbuzz/" + n;

        // Act & Assert
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("n must be >= 1, got: " + n));
    }

    @WithMockUser
    @Test
    void testFibonacci_HappyPath() throws Exception {
        // Arrange
        int n = 10;
        String url = "/api/math/fibonacci/" + n;

        // Act & Assert
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.n").value(n))
                .andExpect(jsonPath("$.fibonacci").value(55));
    }

    @WithMockUser
    @Test
    void testFibonacci_BadRequest() throws Exception {
        // Arrange
        int n = -10;
        String url = "/api/math/fibonacci/" + n;

        // Act & Assert
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("n must be non-negative, got: " + n));
    }

    @WithMockUser
    @Test
    void testGCD_HappyPath() throws Exception {
        // Arrange
        int a = 10;
        int b = 20;
        String url = "/api/math/gcd?a=" + a + "&b=" + b;

        // Act & Assert
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.a").value(a))
                .andExpect(jsonPath("$.b").value(b))
                .andExpect(jsonPath("$.gcd").value(10));
    }

    @WithMockUser
    @Test
    void testGCD_BadRequest() throws Exception {
        // Arrange
        int a = -10;
        int b = 20;
        String url = "/api/math/gcd?a=" + a + "&b=" + b;

        // Act & Assert
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Inputs must be non-negative"));
    }

    @WithMockUser
    @Test
    void testFizzBuzz_LargeInput_HappyPath() throws Exception {
        // Arrange
        int n = 1000000;
        String url = "/api/math/fizzbuzz/" + n;

        // Act & Assert
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.n").value(n));
    }

    @WithMockUser
    @Test
    void testFibonacci_LargeInput_HappyPath() throws Exception {
        // Arrange
        int n = 1000000;
        String url = "/api/math/fibonacci/" + n;

        // Act & Assert
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.n").value(n));
    }

    @WithMockUser
    @Test
    void testGCD_LargeInput_HappyPath() throws Exception {
        // Arrange
        int a = 1000000;
        int b = 2000000;
        String url = "/api/math/gcd?a=" + a + "&b=" + b;

        // Act & Assert
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.a").value(a))
                .andExpect(jsonPath("$.b").value(b));
    }
}