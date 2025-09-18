package org.training.transactions.exception;

public class ResourceNotFound extends GlobalException {
    public ResourceNotFound(String message) {
        super(message, GlobalErrorCode.NOT_FOUND);
    }
}
