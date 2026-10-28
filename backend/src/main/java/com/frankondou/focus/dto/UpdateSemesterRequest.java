package com.frankondou.focus.dto;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UpdateSemesterRequest(
        @Size(min = 1, max = 255) String name,
        LocalDate startDate,
        LocalDate endDate
) {
}