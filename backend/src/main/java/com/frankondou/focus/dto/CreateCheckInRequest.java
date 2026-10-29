package com.frankondou.focus.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Integer, not int, so a missing rating fails @NotNull instead of silently becoming 0
public record CreateCheckInRequest(
        @NotNull @Min(1) @Max(5) Integer moodRating,
        @NotNull @Min(1) @Max(5) Integer difficultyRating,
        @NotNull @Min(1) @Max(5) Integer confidenceRating,
        @Size(max = 1000) String notes
) {
}
