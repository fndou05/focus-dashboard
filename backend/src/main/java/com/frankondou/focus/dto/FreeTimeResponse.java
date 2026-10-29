package com.frankondou.focus.dto;

import java.math.BigDecimal;
import java.util.List;

public record FreeTimeResponse(BigDecimal sleepHours, BigDecimal workHours, BigDecimal customHours,
                               BigDecimal classHours, BigDecimal committedHours, BigDecimal freeHours,
                               BigDecimal plannedStudyHours, BigDecimal leftoverHours,
                               BigDecimal recommendedStudyHours, List<FreeTimeWarning> warnings) {
}
