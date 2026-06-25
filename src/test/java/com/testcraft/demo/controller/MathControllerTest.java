package com.testcraft.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testcraft.demo.dto.FizzBuzzResponse;
import com.testcraft.demo.service.MathService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class MathControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MathService mathService;

    @Test
    @DisplayName("GET /api/math/factorial/{n} - returns n! for valid input")
    void testFactorialValidInput() throws Exception {
        when(mathService.factorial(5)).thenReturn(120L);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/math/factorial/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.n").value(5))
                .andExpect(jsonPath("$.factorial").value(120));
    }

    @Test
    @DisplayName("GET /api/math/factorial/{n} - returns 400 for negative input")
    void testFactorialNegativeInput() throws Exception {
        when(mathService.factorial(anyInt())).thenThrow(new IllegalArgumentException("Input must be non-negative, got: -1"));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/math/factorial/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Input must be non-negative, got: -1"));
    }

    @Test
    @DisplayName("GET /api/math/factorial/{n} - returns 400 for input greater than 20")
    void testFactorialInputGreaterThan20() throws Exception {
        when(mathService.factorial(anyInt())).thenThrow(new IllegalArgumentException("Input must be <= 20 to avoid overflow, got: 21"));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/math/factorial/21"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Input must be <= 20 to avoid overflow, got: 21"));
    }

    @Test
    @DisplayName("GET /api/math/prime/{n} - returns true for prime number")
    void testIsPrimeTrue() throws Exception {
        when(mathService.isPrime(7)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/math/prime/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.n").value(7))
                .andExpect(jsonPath("$.prime").value(true));
    }

    @Test
    @DisplayName("GET /api/math/prime/{n} - returns false for non-prime number")
    void testIsPrimeFalse() throws Exception {
        when(mathService.isPrime(4)).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/math/prime/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.n").value(4))
                .andExpect(jsonPath("$.prime").value(false));
    }

    @Test
    @DisplayName("GET /api/math/prime/{n} - returns 400 for negative input")
    void testIsPrimeNegativeInput() throws Exception {
        when(mathService.isPrime(anyInt())).thenThrow(new IllegalArgumentException("Input must be non-negative, got: -1"));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/math/prime/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Input must be non-negative, got: -1"));
    }

    @Test
    @DisplayName("GET /api/math/fizzbuzz/{n} - returns FizzBuzz list for valid input")
    void testFizzBuzzValidInput() throws Exception {
        List<String> fizzBuzzList = List.of("1", "2", "Fizz", "4", "Buzz");
        when(mathService.fizzBuzz(5)).thenReturn(fizzBuzzList);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/math/fizzbuzz/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.n").value(5))
                .andExpect(jsonPath("$.result").value(fizzBuzzList));
    }

    @Test
    @DisplayName("GET /api/math/fizzbuzz/{n} - returns 400 for input less than 1")
    void testFizzBuzzInputLessThan1() throws Exception {
        when(mathService.fizzBuzz(anyInt())).thenThrow(new IllegalArgumentException("n must be >= 1, got: 0"));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/math/fizzbuzz/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("n must be >= 1, got: 0"));
    }

    @Test
    @DisplayName("GET /api/math/fizzbuzz/{n} - returns 400 for input greater than 10000")
    void testFizzBuzzInputGreaterThan10000() throws Exception {
        when(mathService.fizzBuzz(anyInt())).thenThrow(new IllegalArgumentException("n must be <= 10000, got: 10001"));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/math/fizzbuzz/10001"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("n must be <= 10000, got: 10001"));
    }

    @Test
    @DisplayName("GET /api/math/fibonacci/{n} - returns nth Fibonacci number for valid input")
    void testFibonacciValidInput() throws Exception {
        when(mathService.fibonacci(5)).thenReturn(5L);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/math/fibonacci/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.n").value(5))
                .andExpect(jsonPath("$.fibonacci").value(5));
    }

    @Test
    @DisplayName("GET /api/math/fibonacci/{n} - returns 400 for negative input")
    void testFibonacciNegativeInput() throws Exception {
        when(mathService.fibonacci(anyInt())).thenThrow(new IllegalArgumentException("n must be non-negative, got: -1"));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/math/fibonacci/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("n must be non-negative, got: -1"));
    }

    @Test
    @DisplayName("GET /api/math/fibonacci/{n} - returns 400 for input greater than 85")
    void testFibonacciInputGreaterThan85() throws Exception {
        when(mathService.fibonacci(anyInt())).thenThrow(new IllegalArgumentException("n must be <= 85 to avoid overflow, got: 86"));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/math/fibonacci/86"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("n must be <= 85 to avoid overflow, got: 86"));
    }

    @Test
    @DisplayName("GET /api/math/gcd - returns gcd of two numbers")
    void testGcd() throws Exception {
        when(mathService.gcd(12, 15)).thenReturn(3);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/math/gcd?a=12&b=15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.a").value(12))
                .andExpect(jsonPath("$.b").value(15))
                .andExpect(jsonPath("$.gcd").value(3));
    }

    @Test
    @DisplayName("GET /api/math/gcd - returns 400 for negative input")
    void testGcdNegativeInput() throws Exception {
        when(mathService.gcd(anyInt(), anyInt())).thenThrow(new IllegalArgumentException("Inputs must be non-negative"));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/math/gcd?a=-1&b=15"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Inputs must be non-negative"));
    }

    @Test
    @DisplayName("GET /api/math/factorial/{n} - returns 200 for valid input with authentication")
    void testFactorialValidInputWithAuth() throws Exception {
        when(mathService.factorial(5)).thenReturn(120L);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/math/factorial/5").with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.n").value(5))
                .andExpect(jsonPath("$.factorial").value(120));
    }

    @Test
    @DisplayName("GET /api/math/fizzbuzz/{n} - returns FizzBuzz list for valid input with authentication")
    void testFizzBuzzValidInputWithAuth() throws Exception {
        List<String> fizzBuzzList = List.of("1", "2", "Fizz", "4", "Buzz");
        when(mathService.fizzBuzz(5)).thenReturn(fizzBuzzList);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/math/fizzbuzz/5").with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.n").value(5))
                .andExpect(jsonPath("$.result").value(fizzBuzzList));
    }

    @Test
    @DisplayName("GET /api/math/gcd - returns gcd of two numbers with authentication")
    void testGcdWithAuth() throws Exception {
        when(mathService.gcd(12, 15)).thenReturn(3);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/math/gcd?a=12&b=15").with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.a").value(12))
                .andExpect(jsonPath("$.b").value(15))
                .andExpect(jsonPath("$.gcd").value(3));
    }
}