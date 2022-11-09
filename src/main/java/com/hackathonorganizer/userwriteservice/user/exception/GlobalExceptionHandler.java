package com.hackathonorganizer.userwriteservice.user.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.ServiceUnavailableException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({UserException.class, ServiceUnavailableException.class})
    public ResponseEntity<ErrorResponse> handleUserExceptions(UserException ex) {

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(),
                List.of(ex.getMessage()));

        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }
}
