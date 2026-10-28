package com.frankondou.focus.dto;

import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(min = 1, max = 16) String displayName,
        @Size(min = 1, max = 64) String timezone
) {
}