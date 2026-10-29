package com.frankondou.focus.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record SemesterGpaSummary(UUID semesterId, String semesterName, BigDecimal gpa, BigDecimal gradedCredits) {
}
