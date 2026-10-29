package com.frankondou.focus.dto;

import com.frankondou.focus.entity.enums.ResponsibilityCategory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateResponsibilityRequest(
        @Size(min = 1, max = 100) String label,
        ResponsibilityCategory category,
        @DecimalMin(value = "0", inclusive = false) @DecimalMax("168") BigDecimal hoursPerWeek
) {
}
