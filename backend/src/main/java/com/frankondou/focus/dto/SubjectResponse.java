package com.frankondou.focus.dto;

import com.frankondou.focus.entity.Subject;
import com.frankondou.focus.entity.enums.StrengthRating;
import java.math.BigDecimal;
import java.util.UUID;

public record SubjectResponse(UUID id, UUID semesterId, String name, BigDecimal credits,
                              StrengthRating selfRatedStrength) {

    public static SubjectResponse from(Subject subject) {
        return new SubjectResponse(
                subject.getId(),
                subject.getSemester().getId(),
                subject.getName(),
                subject.getCredits(),
                subject.getSelfRatedStrength()
        );
    }
}