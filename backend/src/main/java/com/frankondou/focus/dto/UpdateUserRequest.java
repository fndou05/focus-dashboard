package com.frankondou.focus.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateUserRequest(
        @Size(min = 1, max = 16) String displayName,
        @Size(min = 1, max = 64) String timezone,
        @PositiveOrZero BigDecimal priorCredits,
        @DecimalMin("0.0") @DecimalMax("5.0") BigDecimal priorGpa
) {
}