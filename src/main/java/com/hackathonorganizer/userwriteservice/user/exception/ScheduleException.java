package com.hackathonorganizer.userwriteservice.user.exception;

import org.springframework.http.HttpStatus;

public class ScheduleException extends RuntimeException {

    private final HttpStatus httpStatus;

    public ScheduleException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
