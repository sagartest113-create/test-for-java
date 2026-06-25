package com.testcraft.demo.repository;

import com.testcraft.demo.model.PathComputation;
import com.testcraft.demo.model.Cell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class PathComputationRepositoryTest {

    private PathComputationRepository repository;

    @BeforeEach
    void setup() {
        repository = new PathComputationRepository();
    }

    @Test
    @DisplayName("Save PathComputation returns entity with auto-generated ID")
    void savePathComputation_ReturnsEntityWithId() {
        PathComputation computation = new PathComputation();
        PathComputation saved = repository.save(computation);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Save multiple PathComputations, IDs increment correctly")
    void saveMultiplePathComputations_IDsIncrementCorrectly() {
        PathComputation computation1 = new PathComputation();
        PathComputation computation2 = new PathComputation();
        PathComputation computation3 = new PathComputation();

        repository.save(computation1);
        repository.save(computation2);
        repository.save(computation3);

        assertThat(computation1.getId()).isLessThan(computation2.getId());
        assertThat(computation2.getId()).isLessThan(computation3.getId());
    }

    @Test
    @DisplayName("FindById returns present PathComputation")
    void findById_ReturnsPresentPathComputation() {
        PathComputation computation = new PathComputation();
        PathComputation saved = repository.save(computation);
        Optional<PathComputation> found = repository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(saved);
    }

    @Test
    @DisplayName("FindById returns absent PathComputation")
    void findById_ReturnsAbsentPathComputation() {
        Optional<PathComputation> found = repository.findById(1L);
        assertThat(found).isNotPresent();
    }

    @Test
    @DisplayName("FindAll returns list of all PathComputations")
    void findAll_ReturnsList() {
        PathComputation computation1 = new PathComputation();
        PathComputation computation2 = new PathComputation();
        repository.save(computation1);
        repository.save(computation2);
        List<PathComputation> all = repository.findAll();
        assertThat(all).hasSize(2);
        assertThat(all).contains(computation1, computation2);
    }

    @Test
    @DisplayName("DeleteById returns true for present PathComputation")
    void deleteById_ReturnsTrueForPresentPathComputation() {
        PathComputation computation = new PathComputation();
        repository.save(computation);
        boolean deleted = repository.deleteById(computation.getId());
        assertThat(deleted).isTrue();
    }

    @Test
    @DisplayName("DeleteById returns false for absent PathComputation")
    void deleteById_ReturnsFalseForAbsentPathComputation() {
        boolean deleted = repository.deleteById(1L);
        assertThat(deleted).isFalse();
    }
}