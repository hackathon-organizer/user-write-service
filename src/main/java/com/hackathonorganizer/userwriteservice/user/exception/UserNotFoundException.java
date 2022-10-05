package com.hackathonorganizer.userwriteservice.user.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("User with id %d not found in base!".formatted(id));
    }
}
