package com.frankondou.focus.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CumulativeGpaResponse(UUID userId, BigDecimal priorCredits, BigDecimal priorGpa,
                                    BigDecimal trackedCredits, BigDecimal cumulativeGpa,
                                    List<SemesterGpaSummary> semesters) {
}
