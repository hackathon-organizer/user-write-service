package com.hackathonorganizer.userwriteservice.exception;

import com.hackathonorganizer.userwriteservice.user.exception.KeycloakException;
import com.hackathonorganizer.userwriteservice.user.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
class ErrorHandler {

    @ExceptionHandler({KeycloakException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse handleKeyCloakException(KeycloakException exception) {
        log.error(exception.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler({UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse handleUserNotFoundException(UserNotFoundException exception) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.toString(),
                exception.getMessage(), LocalDateTime.now());
    }
}
