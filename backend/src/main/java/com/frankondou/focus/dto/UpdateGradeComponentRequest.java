package com.frankondou.focus.dto;

import com.frankondou.focus.entity.enums.GradeCategory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateGradeComponentRequest(
        @Size(min = 1, max = 255) String name,
        GradeCategory category,
        @Positive @DecimalMax("100") BigDecimal weightPercent
) {
}
