package com.testcraft.demo.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class PathComputation {

    private Long id;
    private int[][] grid;
    private List<Cell> path;
    private int totalCost;
    private boolean reachable;
    private LocalDateTime computedAt;

    public PathComputation() {
    }

    public PathComputation(Long id, int[][] grid, List<Cell> path, int totalCost, boolean reachable) {
        this.id = id;
        this.grid = grid;
        this.path = path;
        this.totalCost = totalCost;
        this.reachable = reachable;
        this.computedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int[][] getGrid() {
        return grid;
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
    }

    public List<Cell> getPath() {
        return path;
    }

    public void setPath(List<Cell> path) {
        this.path = path;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    public boolean isReachable() {
        return reachable;
    }

    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }

    public LocalDateTime getComputedAt() {
        return computedAt;
    }

    public void setComputedAt(LocalDateTime computedAt) {
        this.computedAt = computedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathComputation that = (PathComputation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
