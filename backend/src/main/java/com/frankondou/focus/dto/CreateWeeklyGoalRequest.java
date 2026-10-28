package com.frankondou.focus.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateWeeklyGoalRequest(
        @NotNull @PositiveOrZero @DecimalMax("168") BigDecimal targetHours,
        @NotNull LocalDate effectiveFrom
) {
}