package com.frankondou.focus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CreateSemesterRequest(
        @NotBlank @Size(max = 255) String name,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {
}