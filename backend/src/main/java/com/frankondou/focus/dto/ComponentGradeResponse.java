package com.frankondou.focus.dto;

import com.frankondou.focus.entity.enums.GradeCategory;
import java.math.BigDecimal;
import java.util.UUID;

public record ComponentGradeResponse(UUID componentId, String name, GradeCategory category,
                                     BigDecimal weightPercent, BigDecimal percent, int entryCount) {
}
