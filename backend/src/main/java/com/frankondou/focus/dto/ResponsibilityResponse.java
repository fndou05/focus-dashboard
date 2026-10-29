package com.frankondou.focus.dto;

import com.frankondou.focus.entity.Responsibility;
import com.frankondou.focus.entity.enums.ResponsibilityCategory;
import java.math.BigDecimal;
import java.util.UUID;

public record ResponsibilityResponse(UUID id, UUID userId, String label, ResponsibilityCategory category,
                                     BigDecimal hoursPerWeek) {

    public static ResponsibilityResponse from(Responsibility responsibility) {
        return new ResponsibilityResponse(
                responsibility.getId(),
                responsibility.getUser().getId(),
                responsibility.getLabel(),
                responsibility.getCategory(),
                responsibility.getHoursPerWeek()
        );
    }
}
