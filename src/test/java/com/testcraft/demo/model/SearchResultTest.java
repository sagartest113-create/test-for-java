package com.testcraft.demo.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;

public class SearchResultTest {

    @Test
    @DisplayName("Default constructor should create a SearchResult object with default values")
    void testDefaultConstructor() {
        SearchResult searchResult = new SearchResult();
        assertThat(searchResult.getId()).isNull();
        assertThat(searchResult.getSortedArray()).isNull();
        assertThat(searchResult.getTarget()).isZero();
        assertThat(searchResult.isFound()).isFalse();
        assertThat(searchResult.getIndex()).isZero();
        assertThat(searchResult.getComparisons()).isZero();
        assertThat(searchResult.getSteps()).isEmpty();
        assertThat(searchResult.getSearchedAt()).isNotNull();
    }

    @Test
    @DisplayName("Parameterized constructor should create a SearchResult object with given values")
    void testParameterizedConstructor() {
        int[] sortedArray = {1, 2, 3};
        SearchResult searchResult = new SearchResult(1L, sortedArray, 2, true, 1, 3, List.of("step1", "step2"));
        assertThat(searchResult.getId()).isEqualTo(1L);
        assertThat(searchResult.getSortedArray()).isEqualTo(sortedArray);
        assertThat(searchResult.getTarget()).isEqualTo(2);
        assertThat(searchResult.isFound()).isTrue();
        assertThat(searchResult.getIndex()).isEqualTo(1);
        assertThat(searchResult.getComparisons()).isEqualTo(3);
        assertThat(searchResult.getSteps()).isEqualTo(List.of("step1", "step2"));
        assertThat(searchResult.getSearchedAt()).isNotNull();
    }

    @Test
    @DisplayName("Getter and setter methods should work correctly")
    void testGetterSetterMethods() {
        SearchResult searchResult = new SearchResult();
        searchResult.setId(1L);
        searchResult.setSortedArray(new int[] {1, 2, 3});
        searchResult.setTarget(2);
        searchResult.setFound(true);
        searchResult.setIndex(1);
        searchResult.setComparisons(3);
        searchResult.setSteps(List.of("step1", "step2"));
        searchResult.setSearchedAt(LocalDateTime.now());
        assertThat(searchResult.getId()).isEqualTo(1L);
        assertThat(searchResult.getSortedArray()).isEqualTo(new int[] {1, 2, 3});
        assertThat(searchResult.getTarget()).isEqualTo(2);
        assertThat(searchResult.isFound()).isTrue();
        assertThat(searchResult.getIndex()).isEqualTo(1);
        assertThat(searchResult.getComparisons()).isEqualTo(3);
        assertThat(searchResult.getSteps()).isEqualTo(List.of("step1", "step2"));
        assertThat(searchResult.getSearchedAt()).isNotNull();
    }

    @Test
    @DisplayName("Equals method should return true for equal objects")
    void testEqualsMethodEqualObjects() {
        SearchResult searchResult1 = new SearchResult(1L, new int[] {1, 2, 3}, 2, true, 1, 3, List.of("step1", "step2"));
        SearchResult searchResult2 = new SearchResult(1L, new int[] {1, 2, 3}, 2, true, 1, 3, List.of("step1", "step2"));
        assertThat(searchResult1.equals(searchResult2)).isTrue();
    }

    @Test
    @DisplayName("Equals method should return false for unequal objects")
    void testEqualsMethodUnequalObjects() {
        SearchResult searchResult1 = new SearchResult(1L, new int[] {1, 2, 3}, 2, true, 1, 3, List.of("step1", "step2"));
        SearchResult searchResult2 = new SearchResult(2L, new int[] {1, 2, 3}, 2, true, 1, 3, List.of("step1", "step2"));
        assertThat(searchResult1.equals(searchResult2)).isFalse();
    }

    @Test
    @DisplayName("Equals method should return false for null object")
    void testEqualsMethodNullObject() {
        SearchResult searchResult = new SearchResult(1L, new int[] {1, 2, 3}, 2, true, 1, 3, List.of("step1", "step2"));
        assertThat(searchResult.equals(null)).isFalse();
    }

    @Test
    @DisplayName("Equals method should return false for different class object")
    void testEqualsMethodDifferentClassObject() {
        SearchResult searchResult = new SearchResult(1L, new int[] {1, 2, 3}, 2, true, 1, 3, List.of("step1", "step2"));
        Object obj = new Object();
        assertThat(searchResult.equals(obj)).isFalse();
    }

    @Test
    @DisplayName("HashCode method should return the same hash code for equal objects")
    void testHashCodeMethodEqualObjects() {
        SearchResult searchResult1 = new SearchResult(1L, new int[] {1, 2, 3}, 2, true, 1, 3, List.of("step1", "step2"));
        SearchResult searchResult2 = new SearchResult(1L, new int[] {1, 2, 3}, 2, true, 1, 3, List.of("step1", "step2"));
        assertThat(searchResult1.hashCode()).isEqualTo(searchResult2.hashCode());
    }

    @Test
    @DisplayName("HashCode method should return different hash code for unequal objects")
    void testHashCodeMethodUnequalObjects() {
        SearchResult searchResult1 = new SearchResult(1L, new int[] {1, 2, 3}, 2, true, 1, 3, List.of("step1", "step2"));
        SearchResult searchResult2 = new SearchResult(2L, new int[] {1, 2, 3}, 2, true, 1, 3, List.of("step1", "step2"));
        assertThat(searchResult1.hashCode()).isNotEqualTo(searchResult2.hashCode());
    }
}