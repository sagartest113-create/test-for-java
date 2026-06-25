package com.testcraft.demo.dto;

import com.testcraft.demo.dto.PrefixRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Set;

public class PrefixRequestTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    public static void tearDown() {
        validator = null;
    }

    @Test
    @DisplayName("Test construction with valid input")
    void testConstructionValid() {
        // Given
        List<String> words = List.of("word1", "word2");

        // When
        PrefixRequest request = new PrefixRequest(words);

        // Then
        assertThat(request).isNotNull();
        assertThat(request.words()).isEqualTo(words);
    }

    @Test
    @DisplayName("Test construction with null input")
    void testConstructionNull() {
        // Given
        List<String> words = null;

        // When and Then
        Assertions.assertThatThrownBy(() -> new PrefixRequest(words))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Test equals with same object")
    void testEqualsSameObject() {
        // Given
        List<String> words = List.of("word1", "word2");
        PrefixRequest request = new PrefixRequest(words);

        // When and Then
        assertThat(request).isEqualTo(request);
    }

    @Test
    @DisplayName("Test equals with different objects but same values")
    void testEqualsDifferentObjectsSameValues() {
        // Given
        List<String> words = List.of("word1", "word2");
        PrefixRequest request1 = new PrefixRequest(words);
        PrefixRequest request2 = new PrefixRequest(words);

        // When and Then
        assertThat(request1).isEqualTo(request2);
    }

    @Test
    @DisplayName("Test equals with different objects and different values")
    void testEqualsDifferentObjectsDifferentValues() {
        // Given
        List<String> words1 = List.of("word1", "word2");
        List<String> words2 = List.of("word3", "word4");
        PrefixRequest request1 = new PrefixRequest(words1);
        PrefixRequest request2 = new PrefixRequest(words2);

        // When and Then
        assertThat(request1).isNotEqualTo(request2);
    }

    @Test
    @DisplayName("Test equals with null object")
    void testEqualsNullObject() {
        // Given
        List<String> words = List.of("word1", "word2");
        PrefixRequest request = new PrefixRequest(words);

        // When and Then
        assertThat(request).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Test hashCode with same object")
    void testHashCodeSameObject() {
        // Given
        List<String> words = List.of("word1", "word2");
        PrefixRequest request = new PrefixRequest(words);

        // When and Then
        assertThat(request.hashCode()).isEqualTo(request.hashCode());
    }

    @Test
    @DisplayName("Test hashCode with different objects but same values")
    void testHashCodeDifferentObjectsSameValues() {
        // Given
        List<String> words = List.of("word1", "word2");
        PrefixRequest request1 = new PrefixRequest(words);
        PrefixRequest request2 = new PrefixRequest(words);

        // When and Then
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    @DisplayName("Test hashCode with different objects and different values")
    void testHashCodeDifferentObjectsDifferentValues() {
        // Given
        List<String> words1 = List.of("word1", "word2");
        List<String> words2 = List.of("word3", "word4");
        PrefixRequest request1 = new PrefixRequest(words1);
        PrefixRequest request2 = new PrefixRequest(words2);

        // When and Then
        assertThat(request1.hashCode()).isNotEqualTo(request2.hashCode());
    }

    @Test
    @DisplayName("Test toString")
    void testToString() {
        // Given
        List<String> words = List.of("word1", "word2");
        PrefixRequest request = new PrefixRequest(words);

        // When and Then
        assertThat(request.toString()).contains("PrefixRequest[words=" + words + "]");
    }

    @Test
    @DisplayName("Test validation with valid input")
    void testValidationValid() {
        // Given
        List<String> words = List.of("word1", "word2");
        PrefixRequest request = new PrefixRequest(words);

        // When
        Set<ConstraintViolation<PrefixRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Test validation with null input")
    void testValidationNull() {
        // Given
        PrefixRequest request = null;

        // When and Then
        Assertions.assertThatThrownBy(() -> validator.validate(request))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Test validation with invalid input")
    void testValidationInvalid() {
        // Given
        PrefixRequest request = new PrefixRequest(null);

        // When and Then
        Set<ConstraintViolation<PrefixRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        ConstraintViolation<PrefixRequest> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("words must not be null");
    }
}