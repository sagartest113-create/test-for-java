package com.testcraft.demo.controller;

import com.testcraft.demo.dto.PrefixRequest;
import com.testcraft.demo.service.StringUtilService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StringUtilController.class)
public class StringUtilControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StringUtilService stringUtilService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("GET /api/strings/palindrome returns 200 with palindrome result")
    void testIsPalindrome() throws Exception {
        // given
        String input = "racecar";
        boolean result = true;

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/strings/palindrome")
                .param("input", input))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.input").value(input))
                .andExpect(jsonPath("$.palindrome").value(result));
    }

    @Test
    @DisplayName("GET /api/strings/palindrome returns 400 with invalid input")
    void testIsPalindromeInvalidInput() throws Exception {
        // given
        String input = null;

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/strings/palindrome")
                .param("input", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Input must not be null"));
    }

    @Test
    @DisplayName("GET /api/strings/reverse-words returns 200 with reversed words")
    void testReverseWords() throws Exception {
        // given
        String sentence = "hello world";
        String result = "world hello";

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/strings/reverse-words")
                .param("sentence", sentence))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.input").value(sentence))
                .andExpect(jsonPath("$.reversed").value(result));
    }

    @Test
    @DisplayName("GET /api/strings/reverse-words returns 400 with invalid input")
    void testReverseWordsInvalidInput() throws Exception {
        // given
        String sentence = null;

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/strings/reverse-words")
                .param("sentence", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Input must not be null"));
    }

    @Test
    @DisplayName("GET /api/strings/char-frequency returns 200 with character frequency")
    void testCharFrequency() throws Exception {
        // given
        String input = "hello";
        Map<Character, Integer> freq = Map.of('h', 1, 'e', 1, 'l', 2, 'o', 1);

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/strings/char-frequency")
                .param("input", input))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.input").value(input))
                .andExpect(jsonPath("$.frequency").isMap());
    }

    @Test
    @DisplayName("GET /api/strings/char-frequency returns 400 with invalid input")
    void testCharFrequencyInvalidInput() throws Exception {
        // given
        String input = null;

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/strings/char-frequency")
                .param("input", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Input must not be null"));
    }

    @Test
    @DisplayName("POST /api/strings/longest-common-prefix returns 200 with longest common prefix")
    void testLongestCommonPrefix() throws Exception {
        // given
        PrefixRequest request = new PrefixRequest(List.of("flower", "flow", "flight"));
        String result = "fl";

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/strings/longest-common-prefix")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.words").isArray())
                .andExpect(jsonPath("$.prefix").value(result));
    }

    @Test
    @DisplayName("GET /api/strings/camel-to-snake returns 200 with snake case")
    void testCamelToSnake() throws Exception {
        // given
        String input = "myVariableName";
        String result = "my_variable_name";

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/strings/camel-to-snake")
                .param("input", input))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.input").value(input))
                .andExpect(jsonPath("$.snake_case").value(result));
    }

    @Test
    @DisplayName("GET /api/strings/camel-to-snake returns 400 with invalid input")
    void testCamelToSnakeInvalidInput() throws Exception {
        // given
        String input = null;

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/strings/camel-to-snake")
                .param("input", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Input must not be null"));
    }
}