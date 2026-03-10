package com.testcraft.demo.service;

import com.testcraft.demo.model.Cell;
import com.testcraft.demo.model.PathComputation;
import com.testcraft.demo.repository.PathComputationRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ShortestPathService {

    private static final int GRID_SIZE = 9;
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    private static final int BLOCKED = -1;

    private final PathComputationRepository repository;

    public ShortestPathService(PathComputationRepository repository) {
        this.repository = repository;
    }

    public PathComputation computeShortestPath(int[][] grid) {
        validateGrid(grid);

        int[][] dist = initDistances();
        Cell[][] parent = new Cell[GRID_SIZE][GRID_SIZE];

        dist[0][0] = grid[0][0];
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));
        pq.offer(new int[]{0, 0, grid[0][0]});

        runDijkstra(grid, dist, parent, pq);

        boolean reachable = dist[GRID_SIZE - 1][GRID_SIZE - 1] != Integer.MAX_VALUE;
        List<Cell> path = reachable ? reconstructPath(parent, grid) : Collections.emptyList();
        int totalCost = reachable ? dist[GRID_SIZE - 1][GRID_SIZE - 1] : -1;

        PathComputation computation = new PathComputation(null, grid, path, totalCost, reachable);
        return repository.save(computation);
    }

    public Optional<PathComputation> getComputation(Long id) {
        return repository.findById(id);
    }

    public List<PathComputation> getAllComputations() {
        return repository.findAll();
    }

    private int[][] initDistances() {
        int[][] dist = new int[GRID_SIZE][GRID_SIZE];
        for (int[] row : dist) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        return dist;
    }

    private void runDijkstra(int[][] grid, int[][] dist, Cell[][] parent, PriorityQueue<int[]> pq) {
        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int row = current[0];
            int col = current[1];
            int cost = current[2];

            if (cost > dist[row][col]) {
                continue;
            }
            if (row == GRID_SIZE - 1 && col == GRID_SIZE - 1) {
                return;
            }

            exploreNeighbors(grid, dist, parent, pq, row, col, cost);
        }
    }

    private void exploreNeighbors(int[][] grid, int[][] dist, Cell[][] parent,
                                  PriorityQueue<int[]> pq, int row, int col, int cost) {
        for (int[] dir : DIRECTIONS) {
            int nr = row + dir[0];
            int nc = col + dir[1];
            if (nr < 0 || nr >= GRID_SIZE || nc < 0 || nc >= GRID_SIZE) {
                continue;
            }
            if (grid[nr][nc] == BLOCKED) {
                continue;
            }

            int newCost = cost + grid[nr][nc];
            if (newCost < dist[nr][nc]) {
                dist[nr][nc] = newCost;
                parent[nr][nc] = new Cell(row, col);
                pq.offer(new int[]{nr, nc, newCost});
            }
        }
    }

    private List<Cell> reconstructPath(Cell[][] parent, int[][] grid) {
        LinkedList<Cell> path = new LinkedList<>();
        int r = GRID_SIZE - 1;
        int c = GRID_SIZE - 1;

        while (r != 0 || c != 0) {
            path.addFirst(new Cell(r, c, grid[r][c]));
            Cell p = parent[r][c];
            r = p.getRow();
            c = p.getCol();
        }
        path.addFirst(new Cell(0, 0, grid[0][0]));
        return path;
    }

    private void validateGrid(int[][] grid) {
        if (grid == null || grid.length != GRID_SIZE) {
            throw new IllegalArgumentException("Grid must be a " + GRID_SIZE + "x" + GRID_SIZE + " array");
        }
        for (int i = 0; i < GRID_SIZE; i++) {
            if (grid[i] == null || grid[i].length != GRID_SIZE) {
                throw new IllegalArgumentException("Each row must have exactly " + GRID_SIZE + " columns");
            }
        }
        if (grid[0][0] == BLOCKED) {
            throw new IllegalArgumentException("Start cell (0,0) cannot be blocked");
        }
        if (grid[GRID_SIZE - 1][GRID_SIZE - 1] == BLOCKED) {
            throw new IllegalArgumentException("End cell (8,8) cannot be blocked");
        }
    }
}
