package com.frankondou.focus.dto;

import com.frankondou.focus.entity.GradeEntry;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record GradeEntryResponse(UUID id, UUID componentId, String title, BigDecimal scoreEarned,
                                 BigDecimal scorePossible, LocalDate gradedOn) {

    public static GradeEntryResponse from(GradeEntry entry) {
        return new GradeEntryResponse(
                entry.getId(),
                entry.getComponent().getId(),
                entry.getTitle(),
                entry.getScoreEarned(),
                entry.getScorePossible(),
                entry.getGradedOn()
        );
    }
}
