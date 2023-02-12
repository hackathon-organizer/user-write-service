package com.hackathonorganizer.userwriteservice.exception;

import org.springframework.http.HttpStatus;

public class KeycloakException extends BaseException {

    public KeycloakException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
