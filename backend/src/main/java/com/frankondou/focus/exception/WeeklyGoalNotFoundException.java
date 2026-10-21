package com.frankondou.focus.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class WeeklyGoalNotFoundException extends RuntimeException {
    public WeeklyGoalNotFoundException() {
        super("Weekly goal not found");
    }
}