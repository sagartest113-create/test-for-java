package com.testcraft.demo.repository;

import com.testcraft.demo.model.GameAnalysis;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class GameAnalysisRepository {

    private final List<GameAnalysis> analyses = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public GameAnalysis save(GameAnalysis analysis) {
        analysis.setId(idGenerator.getAndIncrement());
        analyses.add(analysis);
        return analysis;
    }

    public Optional<GameAnalysis> findById(Long id) {
        return analyses.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst();
    }

    public List<GameAnalysis> findAll() {
        return new ArrayList<>(analyses);
    }
}
