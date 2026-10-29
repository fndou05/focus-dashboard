package com.frankondou.focus.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class GradeComponentNotFoundException extends RuntimeException {
    public GradeComponentNotFoundException() {
        super("Grade component not found");
    }
}
