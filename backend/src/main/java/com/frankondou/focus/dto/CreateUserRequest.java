package com.frankondou.focus.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateUserRequest(
        @NotBlank @Email @Size(max = 320) String email,
        @NotBlank @Size(max = 16) String displayName,
        @NotBlank @Size(max = 64) String timezone,
        @PositiveOrZero BigDecimal priorCredits,
        @DecimalMin("0.0") @DecimalMax("5.0") BigDecimal priorGpa
) {
}