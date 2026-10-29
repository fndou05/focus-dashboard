package com.frankondou.focus.dto;

import com.frankondou.focus.entity.enums.StrengthRating;
import com.frankondou.focus.entity.enums.SubjectCategory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateSubjectRequest(
        @Size(min = 1, max = 255) String name,
        @Positive BigDecimal credits,
        StrengthRating selfRatedStrength,
        @Size(max = 2) String finalLetterGrade,
        @DecimalMin("0") @DecimalMax("168") BigDecimal weeklyClassHours,
        SubjectCategory category
) {
}