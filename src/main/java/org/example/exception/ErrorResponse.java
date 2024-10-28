package org.example.exception;

public class ErrorResponse {
    private String message;
    private String errorCode;
    private int status;

    public ErrorResponse(String message, String errorCode, int status) {
        this.message = message;
        this.errorCode = errorCode;
        this.status = status;
    }

    // Getters and Setters
}
