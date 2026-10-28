package com.frankondou.focus.dto;

import com.frankondou.focus.entity.WeeklyGoal;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record WeeklyGoalResponse(UUID id, UUID subjectId, BigDecimal targetHours, LocalDate effectiveFrom) {

    public static WeeklyGoalResponse from(WeeklyGoal goal) {
        return new WeeklyGoalResponse(
                goal.getId(),
                goal.getSubject().getId(),
                goal.getTargetHours(),
                goal.getEffectiveFrom()
        );
    }
}