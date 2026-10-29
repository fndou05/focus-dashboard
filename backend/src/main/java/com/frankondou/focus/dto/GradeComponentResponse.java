package com.frankondou.focus.dto;

import com.frankondou.focus.entity.GradeComponent;
import com.frankondou.focus.entity.enums.GradeCategory;
import java.math.BigDecimal;
import java.util.UUID;

public record GradeComponentResponse(UUID id, UUID subjectId, String name, GradeCategory category,
                                     BigDecimal weightPercent) {

    public static GradeComponentResponse from(GradeComponent component) {
        return new GradeComponentResponse(
                component.getId(),
                component.getSubject().getId(),
                component.getName(),
                component.getCategory(),
                component.getWeightPercent()
        );
    }
}
