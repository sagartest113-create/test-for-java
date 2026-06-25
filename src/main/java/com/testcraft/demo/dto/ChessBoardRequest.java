package com.testcraft.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChessBoardRequest(
        @NotNull(message = "Board must not be null")
        @Size(min = 8, max = 8, message = "Board must have exactly 8 rows")
        String[][] board
) {
}
