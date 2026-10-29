package com.frankondou.focus.dto;

import com.frankondou.focus.entity.User;
import java.math.BigDecimal;
import java.util.UUID;

public record UserResponse(UUID id, String email, String displayName, String timezone,
                           BigDecimal priorCredits, BigDecimal priorGpa) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getTimezone(),
                user.getPriorCredits(),
                user.getPriorGpa()
        );
    }
}