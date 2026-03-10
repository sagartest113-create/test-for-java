package com.testcraft.demo.repository;

import com.testcraft.demo.model.PathComputation;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PathComputationRepository {

    private final List<PathComputation> computations = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public PathComputation save(PathComputation computation) {
        computation.setId(idGenerator.getAndIncrement());
        computations.add(computation);
        return computation;
    }

    public Optional<PathComputation> findById(Long id) {
        return computations.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    public List<PathComputation> findAll() {
        return new ArrayList<>(computations);
    }

    public boolean deleteById(Long id) {
        return computations.removeIf(c -> c.getId().equals(id));
    }
}
