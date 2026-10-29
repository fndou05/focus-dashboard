package com.frankondou.focus.dto;

import com.frankondou.focus.entity.GradeCutoff;
import java.math.BigDecimal;
import java.util.UUID;

public record GradeCutoffResponse(UUID id, String letter, BigDecimal minPercent, BigDecimal gradePoints) {

    public static GradeCutoffResponse from(GradeCutoff cutoff) {
        return new GradeCutoffResponse(
                cutoff.getId(),
                cutoff.getLetter(),
                cutoff.getMinPercent(),
                cutoff.getGradePoints()
        );
    }
}