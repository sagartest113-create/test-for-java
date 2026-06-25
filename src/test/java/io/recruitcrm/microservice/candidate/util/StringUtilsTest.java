package io.recruitcrm.microservice.candidate.util;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StringUtilsTest {

    @Test
    @DisplayName("isBlank returns true for null input")
    void testIsBlankNull() {
        assertThat(StringUtils.isBlank(null)).isTrue();
    }

    @Test
    @DisplayName("isBlank returns true for whitespace-only input")
    void testIsBlankWhitespaceOnly() {
        String value = "   ";
        assertThat(StringUtils.isBlank(value)).isTrue();
    }

    @Test
    @DisplayName("isBlank returns false for non-blank input")
    void testIsBlankNonBlank() {
        String value = "Hello, World!";
        assertThat(StringUtils.isBlank(value)).isFalse();
    }

    @Test
    @DisplayName("isNotBlank returns true for non-blank input")
    void testIsNotBlankNonBlank() {
        String value = "Hello, World!";
        assertThat(StringUtils.isNotBlank(value)).isTrue();
    }

    @Test
    @DisplayName("isNotBlank returns false for blank input")
    void testIsNotBlankBlank() {
        String value = "";
        assertThat(StringUtils.isNotBlank(value)).isFalse();
    }

    @Test
    @DisplayName("defaultIfBlank returns fallback when input is blank")
    void testDefaultIfBlankBlankInput() {
        String value = "";
        String fallback = "Hello, World!";
        assertThat(StringUtils.defaultIfBlank(value, fallback)).isEqualTo(fallback);
    }

    @Test
    @DisplayName("defaultIfBlank returns input when it's non-blank")
    void testDefaultIfBlankNonBlankInput() {
        String value = "Hello, World!";
        String fallback = "Fallback";
        assertThat(StringUtils.defaultIfBlank(value, fallback)).isEqualTo(value);
    }

    @Test
    @DisplayName("truncate throws IllegalArgumentException for negative maxLength")
    void testTruncateNegativeMaxLength() {
        String value = "Hello, World!";
        int maxLength = -1;
        assertThatThrownBy(() -> StringUtils.truncate(value, maxLength))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("maxLength must not be negative");
    }

    @Test
    @DisplayName("truncate returns null for null input")
    void testTruncateNullInput() {
        String value = null;
        int maxLength = 10;
        assertThat(StringUtils.truncate(value, maxLength)).isNull();
    }

    @Test
    @DisplayName("truncate returns unchanged input when it's short enough")
    void testTruncateShortEnough() {
        String value = "Hello";
        int maxLength = 10;
        assertThat(StringUtils.truncate(value, maxLength)).isEqualTo(value);
    }

    @Test
    @DisplayName("truncate appends ellipsis to truncated input")
    void testTruncateEllipsisAppended() {
        String value = "Hello, World!";
        int maxLength = 5;
        assertThat(StringUtils.truncate(value, maxLength)).isEqualTo("Hel...");
    }

    @Test
    @DisplayName("truncate returns empty string for maxLength of zero")
    void testTruncateMaxLengthZero() {
        String value = "Hello, World!";
        int maxLength = 0;
        assertThat(StringUtils.truncate(value, maxLength)).isEmpty();
    }

    @Test
    @DisplayName("capitalize leaves blank input unchanged")
    void testCapitalizeBlankInput() {
        String value = "";
        assertThat(StringUtils.capitalize(value)).isEqualTo(value);
    }

    @Test
    @DisplayName("capitalize capitalizes the first character of non-blank input")
    void testCapitalizeNonBlankInput() {
        String value = "hello";
        assertThat(StringUtils.capitalize(value)).isEqualTo("Hello");
    }
}