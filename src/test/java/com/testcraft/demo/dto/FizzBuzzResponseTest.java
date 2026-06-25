package com.testcraft.demo.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class FizzBuzzResponseTest {

    @Test
    @DisplayName("construction and accessors")
    void testConstructionAndAccessors() {
        int n = 10;
        List<String> result = List.of("Fizz", "Buzz", "FizzBuzz");
        FizzBuzzResponse response = new FizzBuzzResponse(n, result);

        assertThat(response.n()).isEqualTo(n);
        assertThat(response.result()).isEqualTo(result);
    }

    @Test
    @DisplayName("equals for same object")
    void testEqualsSameObject() {
        FizzBuzzResponse response = new FizzBuzzResponse(10, List.of("Fizz", "Buzz", "FizzBuzz"));
        assertThat(response).isEqualTo(response);
    }

    @Test
    @DisplayName("equals for different objects with same values")
    void testEqualsDifferentObjectsSameValues() {
        FizzBuzzResponse response1 = new FizzBuzzResponse(10, List.of("Fizz", "Buzz", "FizzBuzz"));
        FizzBuzzResponse response2 = new FizzBuzzResponse(10, List.of("Fizz", "Buzz", "FizzBuzz"));
        assertThat(response1).isEqualTo(response2);
    }

    @Test
    @DisplayName("equals for different objects with different values")
    void testEqualsDifferentObjectsDifferentValues() {
        FizzBuzzResponse response1 = new FizzBuzzResponse(10, List.of("Fizz", "Buzz", "FizzBuzz"));
        FizzBuzzResponse response2 = new FizzBuzzResponse(20, List.of("Fizz", "Buzz"));
        assertThat(response1).isNotEqualTo(response2);
    }

    @Test
    @DisplayName("hashCode for same object")
    void testHashCodeSameObject() {
        FizzBuzzResponse response = new FizzBuzzResponse(10, List.of("Fizz", "Buzz", "FizzBuzz"));
        assertThat(response.hashCode()).isEqualTo(response.hashCode());
    }

    @Test
    @DisplayName("hashCode for different objects with same values")
    void testHashCodeDifferentObjectsSameValues() {
        FizzBuzzResponse response1 = new FizzBuzzResponse(10, List.of("Fizz", "Buzz", "FizzBuzz"));
        FizzBuzzResponse response2 = new FizzBuzzResponse(10, List.of("Fizz", "Buzz", "FizzBuzz"));
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    @DisplayName("hashCode for different objects with different values")
    void testHashCodeDifferentObjectsDifferentValues() {
        FizzBuzzResponse response1 = new FizzBuzzResponse(10, List.of("Fizz", "Buzz", "FizzBuzz"));
        FizzBuzzResponse response2 = new FizzBuzzResponse(20, List.of("Fizz", "Buzz"));
        assertThat(response1.hashCode()).isNotEqualTo(response2.hashCode());
    }

    @Test
    @DisplayName("toString")
    void testToString() {
        FizzBuzzResponse response = new FizzBuzzResponse(10, List.of("Fizz", "Buzz", "FizzBuzz"));
        String expected = "FizzBuzzResponse[n=10, result=[Fizz, Buzz, FizzBuzz]]";
        assertThat(response.toString()).isEqualTo(expected);
    }

    @Test
    @DisplayName("toString with null result")
    void testToStringNullResult() {
        FizzBuzzResponse response = new FizzBuzzResponse(10, null);
        String expected = "FizzBuzzResponse[n=10, result=null]";
        assertThat(response.toString()).isEqualTo(expected);
    }

    @Test
    @DisplayName("toString with empty result")
    void testToStringEmptyResult() {
        FizzBuzzResponse response = new FizzBuzzResponse(10, Collections.emptyList());
        String expected = "FizzBuzzResponse[n=10, result=[]]";
        assertThat(response.toString()).isEqualTo(expected);
    }
}