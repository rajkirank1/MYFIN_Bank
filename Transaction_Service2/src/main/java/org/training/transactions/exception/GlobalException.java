package org.training.transactions.exception;

/**
 * Base runtime exception with error code.
 */
public class GlobalException extends RuntimeException {

    private final String errorCode;

    public GlobalException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public GlobalException(String message) {
        super(message);
        this.errorCode = GlobalErrorCode.BAD_REQUEST;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
