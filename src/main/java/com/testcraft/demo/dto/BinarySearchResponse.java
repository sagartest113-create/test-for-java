package com.testcraft.demo.dto;

import java.util.List;

public record BinarySearchResponse(
        Long searchId,
        int target,
        boolean found,
        int index,
        int comparisons,
        List<String> steps
) {
}
