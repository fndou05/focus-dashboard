package com.frankondou.focus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateGradeEntryRequest(
        @NotBlank @Size(max = 255) String title,
        @NotNull @PositiveOrZero BigDecimal scoreEarned,
        @NotNull @Positive BigDecimal scorePossible,
        @NotNull LocalDate gradedOn
) {
}
