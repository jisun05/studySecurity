package com.example.studysecurity.exception;

public class LoginException extends RuntimeException {
    private final LoginExceptionType type;

    public LoginException(LoginExceptionType type) {
        super(type.getMessage());
        this.type = type;
    }

    public LoginExceptionType getType() {
        return type;
    }
}
