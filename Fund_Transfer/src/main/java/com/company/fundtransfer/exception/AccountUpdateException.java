package com.company.fundtransfer.exception;

public class AccountUpdateException extends RuntimeException {
    public AccountUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
    public AccountUpdateException(String message) {
        super(message);
    }
}
