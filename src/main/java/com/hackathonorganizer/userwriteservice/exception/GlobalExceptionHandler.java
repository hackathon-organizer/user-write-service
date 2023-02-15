package com.hackathonorganizer.userwriteservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({UserException.class, KeycloakException.class, ScheduleException.class})
    public ResponseEntity<ErrorResponse> handleUserExceptions(BaseException ex) {

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), List.of(ex.getMessage()));

        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }
}
