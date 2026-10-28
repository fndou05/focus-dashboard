package com.frankondou.focus.dto;

import com.frankondou.focus.entity.Semester;
import java.time.LocalDate;
import java.util.UUID;

public record SemesterResponse(UUID id, UUID userId, String name, LocalDate startDate, LocalDate endDate) {

    public static SemesterResponse from(Semester semester) {
        return new SemesterResponse(
                semester.getId(),
                semester.getUser().getId(),
                semester.getName(),
                semester.getStartDate(),
                semester.getEndDate()
        );
    }
}