package com.example.studysecurity.exception;

public enum LoginExceptionType {
    NOT_FOUND_GOOGLE_ACCESS_TOKEN_RESPONSE("Could not find Google access token response"),
    INVALID_ACCESS_TOKEN("Invalid access token"),
    INVALID_GOOGLE_PROFILE("Invalid Google profile");

    private final String message;

    LoginExceptionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
