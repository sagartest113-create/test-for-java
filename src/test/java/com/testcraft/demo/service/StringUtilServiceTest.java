package com.testcraft.demo.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StringUtilServiceTest {

    private final StringUtilService stringUtilService = new StringUtilService();

    @Test
    @DisplayName("isPalindrome returns true for palindrome string")
    void isPalindrome_PalindromeString_ReturnsTrue() {
        // Given
        String input = "A man, a plan, a canal: Panama";

        // When
        boolean result = stringUtilService.isPalindrome(input);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isPalindrome returns false for non-palindrome string")
    void isPalindrome_NonPalindromeString_ReturnsFalse() {
        // Given
        String input = "Hello World";

        // When
        boolean result = stringUtilService.isPalindrome(input);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isPalindrome throws exception for null input")
    void isPalindrome_NullInput_ThrowsException() {
        // Given
        String input = null;

        // When and Then
        assertThatThrownBy(() -> stringUtilService.isPalindrome(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Input must not be null");
    }

    @Test
    @DisplayName("reverseWords reverses words in a sentence")
    void reverseWords_Sentence_ReturnsReversedWords() {
        // Given
        String input = "Hello World";

        // When
        String result = stringUtilService.reverseWords(input);

        // Then
        assertThat(result).isEqualTo("World Hello");
    }

    @Test
    @DisplayName("reverseWords returns empty string for empty input")
    void reverseWords_EmptyInput_ReturnsEmptyString() {
        // Given
        String input = "";

        // When
        String result = stringUtilService.reverseWords(input);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("reverseWords throws exception for null input")
    void reverseWords_NullInput_ThrowsException() {
        // Given
        String input = null;

        // When and Then
        assertThatThrownBy(() -> stringUtilService.reverseWords(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Input must not be null");
    }

    @Test
    @DisplayName("charFrequency returns frequency map of characters in a string")
    void charFrequency_String_ReturnsFrequencyMap() {
        // Given
        String input = "Hello World";

        // When
        Map<Character, Integer> result = stringUtilService.charFrequency(input);

        // Then
        Map<Character, Integer> expected = new HashMap<>();
        expected.put('h', 1);
        expected.put('e', 1);
        expected.put('l', 3);
        expected.put('o', 2);
        expected.put('w', 1);
        expected.put('r', 1);
        expected.put('d', 1);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("charFrequency returns empty map for empty input")
    void charFrequency_EmptyInput_ReturnsEmptyMap() {
        // Given
        String input = "";

        // When
        Map<Character, Integer> result = stringUtilService.charFrequency(input);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("charFrequency throws exception for null input")
    void charFrequency_NullInput_ThrowsException() {
        // Given
        String input = null;

        // When and Then
        assertThatThrownBy(() -> stringUtilService.charFrequency(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Input must not be null");
    }

    @Test
    @DisplayName("longestCommonPrefix returns common prefix for multiple strings")
    void longestCommonPrefix_MultipleStrings_ReturnsCommonPrefix() {
        // Given
        String[] input = {"Hello World", "Hello Universe", "Hello Galaxy"};

        // When
        String result = stringUtilService.longestCommonPrefix(input);

        // Then
        assertThat(result).isEqualTo("Hello ");
    }

    @Test
    @DisplayName("longestCommonPrefix returns empty string for no common prefix")
    void longestCommonPrefix_NoCommonPrefix_ReturnsEmptyString() {
        // Given
        String[] input = {"Hello World", "Universe Galaxy", "Foo Bar"};

        // When
        String result = stringUtilService.longestCommonPrefix(input);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("longestCommonPrefix returns empty string for empty input")
    void longestCommonPrefix_EmptyInput_ReturnsEmptyString() {
        // Given
        String[] input = {};

        // When
        String result = stringUtilService.longestCommonPrefix(input);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("longestCommonPrefix returns empty string for null input")
    void longestCommonPrefix_NullInput_ReturnsEmptyString() {
        // Given
        String[] input = null;

        // When
        String result = stringUtilService.longestCommonPrefix(input);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("camelToSnake converts camelCase to snake_case")
    void camelToSnake_CamelCaseString_ReturnsSnakeCaseString() {
        // Given
        String input = "myVariableName";

        // When
        String result = stringUtilService.camelToSnake(input);

        // Then
        assertThat(result).isEqualTo("my_variable_name");
    }

    @Test
    @DisplayName("camelToSnake returns empty string for empty input")
    void camelToSnake_EmptyInput_ReturnsEmptyString() {
        // Given
        String input = "";

        // When
        String result = stringUtilService.camelToSnake(input);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("camelToSnake throws exception for null input")
    void camelToSnake_NullInput_ThrowsException() {
        // Given
        String input = null;

        // When and Then
        assertThatThrownBy(() -> stringUtilService.camelToSnake(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Input must not be null");
    }

    @Test
    @DisplayName("camelToSnake handles multiple consecutive uppercase letters")
    void camelToSnake_MultipleConsecutiveUppercaseLetters_ReturnsCorrectString() {
        // Given
        String input = "myVariableName123";

        // When
        String result = stringUtilService.camelToSnake(input);

        // Then
        assertThat(result).isEqualTo("my_variable_name_123");
    }

    @Test
    @DisplayName("camelToSnake handles multiple consecutive uppercase letters at the end")
    void camelToSnake_MultipleConsecutiveUppercaseLettersAtEnd_ReturnsCorrectString() {
        // Given
        String input = "myVariableName12345";

        // When
        String result = stringUtilService.camelToSnake(input);

        // Then
        assertThat(result).isEqualTo("my_variable_name_12345");
    }

    @Test
    @DisplayName("camelToSnake handles multiple consecutive uppercase letters at the start")
    void camelToSnake_MultipleConsecutiveUppercaseLettersAtStart_ReturnsCorrectString() {
        // Given
        String input = "1234567890myVariableName";

        // When
        String result = stringUtilService.camelToSnake(input);

        // Then
        assertThat(result).isEqualTo("1234567890_my_variable_name");
    }

    @Test
    @DisplayName("camelToSnake handles multiple consecutive uppercase letters at the start and end")
    void camelToSnake_MultipleConsecutiveUppercaseLettersAtStartAndEnd_ReturnsCorrectString() {
        // Given
        String input = "1234567890myVariableName12345";

        // When
        String result = stringUtilService.camelToSnake(input);

        // Then
        assertThat(result).isEqualTo("1234567890_my_variable_name_12345");
    }
}