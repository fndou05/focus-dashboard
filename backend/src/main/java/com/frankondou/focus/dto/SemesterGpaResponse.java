package com.frankondou.focus.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record SemesterGpaResponse(UUID semesterId, String semesterName, BigDecimal gpa,
                                  BigDecimal gradedCredits, List<SubjectGradeResponse> subjects) {
}
