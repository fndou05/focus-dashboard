package com.frankondou.focus.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public record UpdateGradeCutoffRequest(
        @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal minPercent,
        @DecimalMin("0.0") @DecimalMax("5.0") BigDecimal gradePoints
) {
}
