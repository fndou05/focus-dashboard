package com.frankondou.focus.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record SubjectProgressResponse(UUID subjectId, String subjectName,
                                      BigDecimal loggedHours, BigDecimal targetHours) {
}