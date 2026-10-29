package com.frankondou.focus.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record SubjectGradeResponse(UUID subjectId, String subjectName, BigDecimal credits,
                                   BigDecimal currentPercent, String letter, BigDecimal gradePoints,
                                   boolean isFinal, List<ComponentGradeResponse> components) {
}
