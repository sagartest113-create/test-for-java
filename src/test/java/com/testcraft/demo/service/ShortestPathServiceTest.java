package com.testcraft.demo.service;

import com.testcraft.demo.model.Cell;
import com.testcraft.demo.model.PathComputation;
import com.testcraft.demo.repository.PathComputationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShortestPathServiceTest {

    @Mock
    private PathComputationRepository repository;

    @InjectMocks
    private ShortestPathService service;

    @Test
    @DisplayName("should return computation with valid path when grid has no obstacles")
    void shouldReturnComputationWithValidPath() {
        // given
        int[][] grid = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1}
        };

        // when
        PathComputation computation = service.computeShortestPath(grid);

        // then
        assertThat(computation.getPath()).isNotNull();
        assertThat(computation.getTotalCost()).isGreaterThan(0);
        assertThat(computation.isReachable()).isTrue();
    }

    @Test
    @DisplayName("should return computation with empty path when grid has obstacles")
    void shouldReturnComputationWithEmptyPath() {
        // given
        int[][] grid = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 0}
        };

        // when
        PathComputation computation = service.computeShortestPath(grid);

        // then
        assertThat(computation.getPath()).isEmpty();
        assertThat(computation.getTotalCost()).isEqualTo(-1);
        assertThat(computation.isReachable()).isFalse();
    }

    @Test
    @DisplayName("should return computation with valid path when grid has no obstacles and start/end cells are at the same position")
    void shouldReturnComputationWithValidPathWhenStartEndCellsAreAtTheSamePosition() {
        // given
        int[][] grid = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1}
        };

        // when
        PathComputation computation = service.computeShortestPath(grid);

        // then
        assertThat(computation.getPath()).isNotNull();
        assertThat(computation.getTotalCost()).isGreaterThan(0);
        assertThat(computation.isReachable()).isTrue();
    }

    @Test
    @DisplayName("should return computation with empty path when grid is null")
    void shouldReturnComputationWithEmptyPathWhenGridIsNull() {
        // given
        int[][] grid = null;

        // when
        PathComputation computation = service.computeShortestPath(grid);

        // then
        assertThat(computation.getPath()).isEmpty();
        assertThat(computation.getTotalCost()).isEqualTo(-1);
        assertThat(computation.isReachable()).isFalse();
    }

    @Test
    @DisplayName("should return computation with empty path when grid has wrong size")
    void shouldReturnComputationWithEmptyPathWhenGridHasWrongSize() {
        // given
        int[][] grid = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 2}
        };

        // when
        PathComputation computation = service.computeShortestPath(grid);

        // then
        assertThat(computation.getPath()).isEmpty();
        assertThat(computation.getTotalCost()).isEqualTo(-1);
        assertThat(computation.isReachable()).isFalse();
    }

    @Test
    @DisplayName("should return computation with empty path when grid has start cell blocked")
    void shouldReturnComputationWithEmptyPathWhenGridHasStartCellBlocked() {
        // given
        int[][] grid = {
                {0, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1}
        };

        // when
        PathComputation computation = service.computeShortestPath(grid);

        // then
        assertThat(computation.getPath()).isEmpty();
        assertThat(computation.getTotalCost()).isEqualTo(-1);
        assertThat(computation.isReachable()).isFalse();
    }

    @Test
    @DisplayName("should return computation with empty path when grid has end cell blocked")
    void shouldReturnComputationWithEmptyPathWhenGridHasEndCellBlocked() {
        // given
        int[][] grid = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 0}
        };

        // when
        PathComputation computation = service.computeShortestPath(grid);

        // then
        assertThat(computation.getPath()).isEmpty();
        assertThat(computation.getTotalCost()).isEqualTo(-1);
        assertThat(computation.isReachable()).isFalse();
    }

    @Test
    @DisplayName("should save computation in repository")
    void shouldSaveComputationInRepository() {
        // given
        int[][] grid = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1}
        };

        PathComputation computation = service.computeShortestPath(grid);

        // when
        when(repository.save(any(PathComputation.class))).thenReturn(computation);

        // then
        PathComputation savedComputation = service.computeShortestPath(grid);
        verify(repository, times(1)).save(any(PathComputation.class));
        assertThat(savedComputation).isEqualTo(computation);
    }

    @Test
    @DisplayName("should return computation by id")
    void shouldReturnComputationById() {
        // given
        int[][] grid = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1}
        };

        PathComputation computation = service.computeShortestPath(grid);

        // when
        when(repository.findById(computation.getId())).thenReturn(Optional.of(computation));

        // then
        Optional<PathComputation> foundComputation = service.getComputation(computation.getId());
        assertThat(foundComputation).contains(computation);
    }

    @Test
    @DisplayName("should return empty list when no computations found")
    void shouldReturnEmptyListWhenNoComputationsFound() {
        // given
        int[][] grid = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1}
        };

        PathComputation computation = service.computeShortestPath(grid);

        // when
        when(repository.findById(computation.getId())).thenReturn(Optional.empty());

        // then
        Optional<PathComputation> foundComputation = service.getComputation(computation.getId());
        assertThat(foundComputation).isEmpty();
    }

    @Test
    @DisplayName("should return all computations")
    void shouldReturnAllComputations() {
        // given
        int[][] grid = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1}
        };

        PathComputation computation = service.computeShortestPath(grid);

        // when
        when(repository.findAll()).thenReturn(List.of(computation));

        // then
        List<PathComputation> allComputations = service.getAllComputations();
        assertThat(allComputations).containsExactly(computation);
    }
}