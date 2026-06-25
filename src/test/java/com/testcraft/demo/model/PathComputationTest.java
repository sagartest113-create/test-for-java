package com.testcraft.demo.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Assertions;
import org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class PathComputationTest {

    @Test
    @DisplayName("Default constructor should create a valid PathComputation object")
    void testDefaultConstructor() {
        PathComputation pathComputation = new PathComputation();
        Assertions.assertNotNull(pathComputation);
    }

    @Test
    @DisplayName("Parameterized constructor should create a valid PathComputation object")
    void testParameterizedConstructor() {
        int[][] grid = new int[3][3];
        List<Cell> path = new ArrayList<>();
        PathComputation pathComputation = new PathComputation(1L, grid, path, 10, true);
        Assertions.assertNotNull(pathComputation);
        Assertions.assertEquals(1L, pathComputation.getId());
        Assertions.assertEquals(grid, pathComputation.getGrid());
        Assertions.assertEquals(path, pathComputation.getPath());
        Assertions.assertEquals(10, pathComputation.getTotalCost());
        Assertions.assertTrue(pathComputation.isReachable());
        Assertions.assertNotNull(pathComputation.getComputedAt());
    }

    @Test
    @DisplayName("getId should return the correct id")
    void testGetId() {
        PathComputation pathComputation = new PathComputation(1L, new int[3][3], new ArrayList<>(), 10, true);
        Assertions.assertEquals(1L, pathComputation.getId());
    }

    @Test
    @DisplayName("setId should set the correct id")
    void testSetId() {
        PathComputation pathComputation = new PathComputation();
        pathComputation.setId(1L);
        Assertions.assertEquals(1L, pathComputation.getId());
    }

    @Test
    @DisplayName("getGrid should return the correct grid")
    void testGetGrid() {
        int[][] grid = new int[3][3];
        PathComputation pathComputation = new PathComputation(1L, grid, new ArrayList<>(), 10, true);
        Assertions.assertEquals(grid, pathComputation.getGrid());
    }

    @Test
    @DisplayName("setGrid should set the correct grid")
    void testSetGrid() {
        int[][] grid = new int[3][3];
        PathComputation pathComputation = new PathComputation();
        pathComputation.setGrid(grid);
        Assertions.assertEquals(grid, pathComputation.getGrid());
    }

    @Test
    @DisplayName("getPath should return the correct path")
    void testGetPath() {
        List<Cell> path = new ArrayList<>();
        PathComputation pathComputation = new PathComputation(1L, new int[3][3], path, 10, true);
        Assertions.assertEquals(path, pathComputation.getPath());
    }

    @Test
    @DisplayName("setPath should set the correct path")
    void testSetPath() {
        List<Cell> path = new ArrayList<>();
        PathComputation pathComputation = new PathComputation();
        pathComputation.setPath(path);
        Assertions.assertEquals(path, pathComputation.getPath());
    }

    @Test
    @DisplayName("getTotalCost should return the correct total cost")
    void testGetTotalCost() {
        PathComputation pathComputation = new PathComputation(1L, new int[3][3], new ArrayList<>(), 10, true);
        Assertions.assertEquals(10, pathComputation.getTotalCost());
    }

    @Test
    @DisplayName("setTotalCost should set the correct total cost")
    void testSetTotalCost() {
        PathComputation pathComputation = new PathComputation();
        pathComputation.setTotalCost(10);
        Assertions.assertEquals(10, pathComputation.getTotalCost());
    }

    @Test
    @DisplayName("isReachable should return the correct reachable status")
    void testIsReachable() {
        PathComputation pathComputation = new PathComputation(1L, new int[3][3], new ArrayList<>(), 10, true);
        Assertions.assertTrue(pathComputation.isReachable());
    }

    @Test
    @DisplayName("setReachable should set the correct reachable status")
    void testSetReachable() {
        PathComputation pathComputation = new PathComputation();
        pathComputation.setReachable(true);
        Assertions.assertTrue(pathComputation.isReachable());
    }

    @Test
    @DisplayName("getComputedAt should return the correct computed at time")
    void testGetComputedAt() {
        PathComputation pathComputation = new PathComputation(1L, new int[3][3], new ArrayList<>(), 10, true);
        Assertions.assertNotNull(pathComputation.getComputedAt());
    }

    @Test
    @DisplayName("setComputedAt should set the correct computed at time")
    void testSetComputedAt() {
        PathComputation pathComputation = new PathComputation();
        pathComputation.setComputedAt(LocalDateTime.now());
        Assertions.assertNotNull(pathComputation.getComputedAt());
    }

    @Nested
    public class EqualsAndHashCodeTest {

        @Test
        @DisplayName("equals should return true for equal objects")
        void testEqualsEqualObjects() {
            PathComputation pathComputation1 = new PathComputation(1L, new int[3][3], new ArrayList<>(), 10, true);
            PathComputation pathComputation2 = new PathComputation(1L, new int[3][3], new ArrayList<>(), 10, true);
            Assertions.assertTrue(pathComputation1.equals(pathComputation2));
        }

        @Test
        @DisplayName("equals should return false for unequal objects")
        void testEqualsUnequalObjects() {
            PathComputation pathComputation1 = new PathComputation(1L, new int[3][3], new ArrayList<>(), 10, true);
            PathComputation pathComputation2 = new PathComputation(2L, new int[3][3], new ArrayList<>(), 10, true);
            Assertions.assertFalse(pathComputation1.equals(pathComputation2));
        }

        @Test
        @DisplayName("equals should return false for null object")
        void testEqualsNullObject() {
            PathComputation pathComputation = new PathComputation(1L, new int[3][3], new ArrayList<>(), 10, true);
            Assertions.assertFalse(pathComputation.equals(null));
        }

        @Test
        @DisplayName("equals should return false for different class object")
        void testEqualsDifferentClassObject() {
            PathComputation pathComputation = new PathComputation(1L, new int[3][3], new ArrayList<>(), 10, true);
            Object object = new Object();
            Assertions.assertFalse(pathComputation.equals(object));
        }

        @Test
        @DisplayName("hashCode should return the same hash code for equal objects")
        void testHashCodeEqualObjects() {
            PathComputation pathComputation1 = new PathComputation(1L, new int[3][3], new ArrayList<>(), 10, true);
            PathComputation pathComputation2 = new PathComputation(1L, new int[3][3], new ArrayList<>(), 10, true);
            Assertions.assertEquals(pathComputation1.hashCode(), pathComputation2.hashCode());
        }

        @Test
        @DisplayName("hashCode should return different hash code for unequal objects")
        void testHashCodeUnequalObjects() {
            PathComputation pathComputation1 = new PathComputation(1L, new int[3][3], new ArrayList<>(), 10, true);
            PathComputation pathComputation2 = new PathComputation(2L, new int[3][3], new ArrayList<>(), 10, true);
            Assertions.assertNotEquals(pathComputation1.hashCode(), pathComputation2.hashCode());
        }
    }
}