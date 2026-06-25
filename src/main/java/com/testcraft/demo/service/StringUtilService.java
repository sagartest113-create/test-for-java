package com.testcraft.demo.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StringUtilService {

    /**
     * Returns true if the string is a palindrome (case-insensitive, ignores non-alphanumeric).
     */
    public boolean isPalindrome(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input must not be null");
        }
        String cleaned = input.toLowerCase().replaceAll("[^a-z0-9]", "");
        int left = 0, right = cleaned.length() - 1;
        while (left < right) {
            if (cleaned.charAt(left) != cleaned.charAt(right)) return false;
            left++;
            right--;
        }
        return true;
    }

    /**
     * Reverses words in a sentence (preserves single-space separation).
     */
    public String reverseWords(String sentence) {
        if (sentence == null) {
            throw new IllegalArgumentException("Input must not be null");
        }
        String trimmed = sentence.trim();
        if (trimmed.isEmpty()) return "";
        String[] words = trimmed.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = words.length - 1; i >= 0; i--) {
            sb.append(words[i]);
            if (i > 0) sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * Returns a frequency map of each character in the string (excludes spaces).
     */
    public Map<Character, Integer> charFrequency(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input must not be null");
        }
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : input.toCharArray()) {
            if (c != ' ') {
                freq.merge(c, 1, Integer::sum);
            }
        }
        return freq;
    }

    /**
     * Returns the longest common prefix of the provided strings.
     * Returns empty string if the array is empty or there is no common prefix.
     */
    public String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) return "";
        String prefix = strs[0];
        for (int i = 1; i < strs.length; i++) {
            while (!strs[i].startsWith(prefix)) {
                prefix = prefix.substring(0, prefix.length() - 1);
                if (prefix.isEmpty()) return "";
            }
        }
        return prefix;
    }

    /**
     * Converts a camelCase string to snake_case.
     * E.g. "myVariableName" → "my_variable_name"
     */
    public String camelToSnake(String camel) {
        if (camel == null) {
            throw new IllegalArgumentException("Input must not be null");
        }
        if (camel.isEmpty()) return "";
        return camel
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2")
                .toLowerCase();
    }
}
