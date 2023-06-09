package com.example.exceptions;

public class ExceptionResponse {
    private final String error;
    private final String description;

    public ExceptionResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }
}
