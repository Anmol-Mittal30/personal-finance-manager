package com.example.finance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.finance.entity.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class CategoryDtos {
    private CategoryDtos() {
    }

    public record CategoryRequest(@NotBlank String name, @NotNull CategoryType type) {
    }

    public record CategoryResponse(
            Long id,
            String name,
            CategoryType type,
            boolean custom,
            @JsonProperty("isCustom") boolean isCustom) {
    }
}
