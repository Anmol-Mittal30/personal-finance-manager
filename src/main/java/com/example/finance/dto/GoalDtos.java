package com.example.finance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class GoalDtos {
    private GoalDtos() {
    }

    public record GoalRequest(
            @NotBlank String goalName,
            @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal targetAmount,
            @NotNull LocalDate targetDate,
            LocalDate startDate) {
    }

    public record GoalUpdateRequest(
            @DecimalMin(value = "0.0", inclusive = false) BigDecimal targetAmount,
            LocalDate targetDate) {
    }

    public record GoalResponse(
            Long id,
            String goalName,
            BigDecimal targetAmount,
            LocalDate targetDate,
            LocalDate startDate,
            BigDecimal currentProgress,
            BigDecimal progressPercentage,
            BigDecimal remainingAmount) {
    }
}
