package com.frankondou.focus.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CheckInNotFoundException extends RuntimeException {
    public CheckInNotFoundException() {
        super("Check-in not found");
    }
}
