package com.testcraft.demo.service;

import com.testcraft.demo.model.SearchResult;
import com.testcraft.demo.repository.SearchResultRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BinarySearchService {

    private final SearchResultRepository repository;

    public BinarySearchService(SearchResultRepository repository) {
        this.repository = repository;
    }

    public SearchResult search(int[] sortedArray, int target) {
        int[] sorted = Arrays.copyOf(sortedArray, sortedArray.length);
        Arrays.sort(sorted);

        List<String> steps = new ArrayList<>();
        int comparisons = 0;
        int low = 0;
        int high = sorted.length - 1;
        int foundIndex = -1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            comparisons++;

            if (sorted[mid] == target) {
                steps.add("Step " + comparisons + ": mid=" + mid
                        + " value=" + sorted[mid] + " → FOUND");
                foundIndex = mid;
                break;
            } else if (sorted[mid] < target) {
                steps.add("Step " + comparisons + ": mid=" + mid
                        + " value=" + sorted[mid] + " < " + target + " → search right");
                low = mid + 1;
            } else {
                steps.add("Step " + comparisons + ": mid=" + mid
                        + " value=" + sorted[mid] + " > " + target + " → search left");
                high = mid - 1;
            }
        }

        if (foundIndex == -1) {
            steps.add("Target " + target + " not found after " + comparisons + " comparisons");
        }

        SearchResult result = new SearchResult(
                null, sorted, target, foundIndex != -1, foundIndex, comparisons, steps);
        return repository.save(result);
    }

    public Optional<SearchResult> getResult(Long id) {
        return repository.findById(id);
    }

    public List<SearchResult> getAllResults() {
        return repository.findAll();
    }
}
