package com.kabu.admin.user.exception;

public class UserConflictException extends RuntimeException {

    public UserConflictException(String username) {
        super("Username already exists: " + username);
    }
}
