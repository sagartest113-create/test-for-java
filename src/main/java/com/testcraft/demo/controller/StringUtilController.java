package com.testcraft.demo.controller;

import com.testcraft.demo.dto.PrefixRequest;
import com.testcraft.demo.service.StringUtilService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Endpoints exposing string utility operations.
 * Rich in edge cases — great for TestCraft AI test generation.
 */
@RestController
@RequestMapping("/api/strings")
public class StringUtilController {

    private final StringUtilService stringUtilService;

    public StringUtilController(StringUtilService stringUtilService) {
        this.stringUtilService = stringUtilService;
    }

    /**
     * GET /api/strings/palindrome?input=racecar
     * Returns whether the input is a palindrome.
     */
    @GetMapping("/palindrome")
    public ResponseEntity<Map<String, Object>> isPalindrome(@RequestParam String input) {
        try {
            boolean result = stringUtilService.isPalindrome(input);
            return ResponseEntity.ok(Map.of("input", input, "palindrome", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/strings/reverse-words?sentence=hello+world
     * Returns the words in reverse order.
     */
    @GetMapping("/reverse-words")
    public ResponseEntity<Map<String, Object>> reverseWords(@RequestParam String sentence) {
        try {
            String result = stringUtilService.reverseWords(sentence);
            return ResponseEntity.ok(Map.of("input", sentence, "reversed", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/strings/char-frequency?input=hello
     * Returns character frequency map (spaces excluded).
     */
    @GetMapping("/char-frequency")
    public ResponseEntity<Map<String, Object>> charFrequency(@RequestParam String input) {
        try {
            Map<Character, Integer> freq = stringUtilService.charFrequency(input);
            // Convert char keys to String for JSON compatibility
            Map<String, Integer> jsonFriendly = new java.util.LinkedHashMap<>();
            freq.forEach((k, v) -> jsonFriendly.put(String.valueOf(k), v));
            return ResponseEntity.ok(Map.of("input", input, "frequency", jsonFriendly));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/strings/longest-common-prefix
     * Body: { "words": ["flower","flow","flight"] }
     * Returns the longest common prefix.
     */
    @PostMapping("/longest-common-prefix")
    public ResponseEntity<Map<String, Object>> longestCommonPrefix(
            @Valid @RequestBody PrefixRequest request) {
        String[] arr = request.words().toArray(new String[0]);
        String prefix = stringUtilService.longestCommonPrefix(arr);
        return ResponseEntity.ok(Map.of("words", request.words(), "prefix", prefix));
    }

    /**
     * GET /api/strings/camel-to-snake?input=myVariableName
     * Converts camelCase to snake_case.
     */
    @GetMapping("/camel-to-snake")
    public ResponseEntity<Map<String, Object>> camelToSnake(@RequestParam String input) {
        try {
            String result = stringUtilService.camelToSnake(input);
            return ResponseEntity.ok(Map.of("input", input, "snake_case", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
