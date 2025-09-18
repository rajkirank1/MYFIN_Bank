package com.company.userservice.exception;

/**
 * Base runtime exception used across the application.
 * Carries an optional error code in addition to the exception message.
 */
public class GlobalException extends RuntimeException {

    private String errorCode;

    public GlobalException(String message) {
        super(message);
    }

    public GlobalException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public GlobalException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
