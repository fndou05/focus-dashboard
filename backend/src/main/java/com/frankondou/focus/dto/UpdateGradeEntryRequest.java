package com.frankondou.focus.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateGradeEntryRequest(
        @Size(min = 1, max = 255) String title,
        @PositiveOrZero BigDecimal scoreEarned,
        @Positive BigDecimal scorePossible,
        LocalDate gradedOn
) {
}
