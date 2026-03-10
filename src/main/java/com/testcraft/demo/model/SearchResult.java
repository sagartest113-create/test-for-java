package com.testcraft.demo.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class SearchResult {

    private Long id;
    private int[] sortedArray;
    private int target;
    private boolean found;
    private int index;
    private int comparisons;
    private List<String> steps;
    private LocalDateTime searchedAt;

    public SearchResult() {
    }

    public SearchResult(Long id, int[] sortedArray, int target, boolean found,
                        int index, int comparisons, List<String> steps) {
        this.id = id;
        this.sortedArray = sortedArray;
        this.target = target;
        this.found = found;
        this.index = index;
        this.comparisons = comparisons;
        this.steps = steps;
        this.searchedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int[] getSortedArray() { return sortedArray; }
    public void setSortedArray(int[] sortedArray) { this.sortedArray = sortedArray; }

    public int getTarget() { return target; }
    public void setTarget(int target) { this.target = target; }

    public boolean isFound() { return found; }
    public void setFound(boolean found) { this.found = found; }

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public int getComparisons() { return comparisons; }
    public void setComparisons(int comparisons) { this.comparisons = comparisons; }

    public List<String> getSteps() { return steps; }
    public void setSteps(List<String> steps) { this.steps = steps; }

    public LocalDateTime getSearchedAt() { return searchedAt; }
    public void setSearchedAt(LocalDateTime searchedAt) { this.searchedAt = searchedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchResult that = (SearchResult) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
