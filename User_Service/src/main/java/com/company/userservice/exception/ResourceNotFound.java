package com.company.userservice.exception;

/**
 * Thrown when a requested resource cannot be found.
 */
public class ResourceNotFound extends GlobalException {

    public ResourceNotFound() {
        super("Resource not found on the server", GlobalError.NOT_FOUND);
    }

    public ResourceNotFound(String message) {
        super(message, GlobalError.NOT_FOUND);
    }
}
