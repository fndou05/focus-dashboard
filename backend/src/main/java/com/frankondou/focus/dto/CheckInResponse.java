package com.frankondou.focus.dto;

import com.frankondou.focus.entity.CheckIn;
import java.util.UUID;

public record CheckInResponse(UUID id, UUID sessionId, int moodRating, int difficultyRating,
                              int confidenceRating, String notes) {

    public static CheckInResponse from(CheckIn checkIn) {
        return new CheckInResponse(
                checkIn.getId(),
                checkIn.getSession().getId(),
                checkIn.getMoodRating(),
                checkIn.getDifficultyRating(),
                checkIn.getConfidenceRating(),
                checkIn.getNotes()
        );
    }
}
