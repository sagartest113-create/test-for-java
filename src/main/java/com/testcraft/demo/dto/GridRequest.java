package com.testcraft.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GridRequest(
        @NotNull(message = "Grid must not be null")
        @Size(min = 9, max = 9, message = "Grid must have exactly 9 rows")
        int[][] grid
) {
}
