package com.frankondou.focus.dto;

import com.frankondou.focus.entity.enums.SessionMode;
import jakarta.validation.constraints.NotNull;

public record StartStudySessionRequest(
        @NotNull SessionMode mode
) {
}