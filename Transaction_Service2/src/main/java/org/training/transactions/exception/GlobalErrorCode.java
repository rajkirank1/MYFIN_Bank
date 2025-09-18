package org.training.transactions.exception;

/**
 * Centralized error-code constants used by custom exceptions and the global handler.
 */
public final class GlobalErrorCode {
    public static final String NOT_FOUND = "404";
    public static final String BAD_REQUEST = "400";
    public static final String INTERNAL = "500";

    private GlobalErrorCode() {
        // utility class - prevent instantiation
    }
}
