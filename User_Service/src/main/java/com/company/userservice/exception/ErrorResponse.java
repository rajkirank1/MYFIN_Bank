package com.company.userservice.exception;

/**
 * Standard error response model returned from exception handlers.
 */
public class ErrorResponse {
    private String errorCode;
    private String errorMessage;

    public ErrorResponse() {
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
