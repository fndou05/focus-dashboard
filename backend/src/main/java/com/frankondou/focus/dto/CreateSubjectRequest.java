package com.frankondou.focus.dto;

import com.frankondou.focus.entity.enums.StrengthRating;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateSubjectRequest(
        @NotBlank @Size(max = 255) String name,
        @NotNull @Positive BigDecimal credits,
        @NotNull StrengthRating selfRatedStrength
) {
}