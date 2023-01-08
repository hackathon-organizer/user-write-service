package com.hackathonorganizer.userwriteservice.exception;

import org.springframework.http.HttpStatus;

public class ScheduleException extends BaseException {

    private final HttpStatus httpStatus;

    public ScheduleException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
        this.httpStatus = httpStatus;
    }
}
