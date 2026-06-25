package com.testcraft.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BinarySearchRequest(
        @NotNull(message = "Array must not be null")
        @Size(min = 1, message = "Array must have at least one element")
        int[] sortedArray,

        int target
) {
}
