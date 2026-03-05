package com.testcraft.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EchoRequest(
        @NotBlank(message = "Input cannot be empty")
        @Size(max = 500, message = "Input must not exceed 500 characters")
        String input
) {}
