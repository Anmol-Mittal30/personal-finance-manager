package com.example.finance.dto;

import com.example.finance.entity.CategoryType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class TransactionDtos {
    private TransactionDtos() {
    }

    public record TransactionRequest(
            @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal amount,
            @NotNull LocalDate date,
            @NotBlank String category,
            String description) {
    }

    public record TransactionUpdateRequest(
            @DecimalMin(value = "0.0", inclusive = false) BigDecimal amount,
            LocalDate date,
            String category,
            String description) {
    }

    public record TransactionResponse(
            Long id,
            BigDecimal amount,
            LocalDate date,
            String category,
            String description,
            CategoryType type) {
    }
}
