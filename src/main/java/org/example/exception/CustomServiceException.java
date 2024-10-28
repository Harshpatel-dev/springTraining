package org.example.exception;

public class CustomServiceException extends RuntimeException {
    private final String errorCode;
    public CustomServiceException(String message) {
        super(message);
        this.errorCode = "UNKNOWN_ERROR"; // Default error code
    }

    public CustomServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "UNKNOWN_ERROR"; // Default error code
    }
    
    public CustomServiceException(String message, String errorcode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorcode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}