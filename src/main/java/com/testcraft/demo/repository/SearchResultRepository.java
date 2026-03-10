package com.testcraft.demo.repository;

import com.testcraft.demo.model.SearchResult;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class SearchResultRepository {

    private final List<SearchResult> results = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public SearchResult save(SearchResult result) {
        result.setId(idGenerator.getAndIncrement());
        results.add(result);
        return result;
    }

    public Optional<SearchResult> findById(Long id) {
        return results.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst();
    }

    public List<SearchResult> findAll() {
        return new ArrayList<>(results);
    }
}
