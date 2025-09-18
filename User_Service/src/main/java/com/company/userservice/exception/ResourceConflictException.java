package com.company.userservice.exception;

/**
 * Thrown when attempting to create a resource that already exists.
 */
public class ResourceConflictException extends GlobalException {

    public ResourceConflictException() {
        super("Resource already present on the server!!!", GlobalError.CONFLICT);
    }

    public ResourceConflictException(String message) {
        super(message, GlobalError.CONFLICT);
    }

    public ResourceConflictException(String message, Throwable cause) {
        super(message, GlobalError.CONFLICT, cause);
    }
}
