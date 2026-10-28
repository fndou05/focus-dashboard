package com.frankondou.focus.dto;

import java.time.LocalDate;
import java.util.List;

public record WeeklyProgressResponse(LocalDate weekStart, List<SubjectProgressResponse> subjects) {
}