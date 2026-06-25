package com.testcraft.demo.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PrefixRequest(@NotNull List<String> words) {}
