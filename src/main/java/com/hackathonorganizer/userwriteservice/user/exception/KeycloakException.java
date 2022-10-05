package com.hackathonorganizer.userwriteservice.user.exception;

public class KeycloakException extends RuntimeException {

    public KeycloakException(String message) {
        super(message);
    }
}
