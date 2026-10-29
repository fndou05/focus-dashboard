package com.frankondou.focus.dto;

import com.frankondou.focus.entity.enums.GradeCategory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateGradeComponentRequest(
        @NotBlank @Size(max = 255) String name,
        @NotNull GradeCategory category,
        @NotNull @Positive @DecimalMax("100") BigDecimal weightPercent
) {
}
