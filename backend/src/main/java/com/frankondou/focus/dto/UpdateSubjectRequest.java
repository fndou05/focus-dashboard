package com.frankondou.focus.dto;

import com.frankondou.focus.entity.enums.StrengthRating;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateSubjectRequest(
        @Size(min = 1, max = 255) String name,
        @Positive BigDecimal credits,
        StrengthRating selfRatedStrength,
        @Size(max = 2) String finalLetterGrade
) {
}