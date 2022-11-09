package com.hackathonorganizer.userwriteservice.exception;

import com.hackathonorganizer.userwriteservice.user.exception.KeycloakException;
import com.hackathonorganizer.userwriteservice.user.exception.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@RestControllerAdvice
class ErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({KeycloakException.class})
    ResponseEntity<ErrorResponse> handleKeyCloakException(KeycloakException ex) {

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(),
                List.of(ex.getMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({UserException.class})
    ResponseEntity<ErrorResponse> handleUserException(UserException ex) {

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(),
                List.of(ex.getMessage()));

        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }
}
