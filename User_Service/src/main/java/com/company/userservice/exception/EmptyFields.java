package com.company.userservice.exception;

/**
 * Thrown when a request payload contains empty/invalid fields.
 */
public class EmptyFields extends GlobalException {
    public EmptyFields(String message, String errorCode) {
        super(message, errorCode);
    }
}
