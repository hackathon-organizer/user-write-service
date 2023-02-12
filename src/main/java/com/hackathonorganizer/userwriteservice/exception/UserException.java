package com.hackathonorganizer.userwriteservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserException extends BaseException {


    public UserException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
