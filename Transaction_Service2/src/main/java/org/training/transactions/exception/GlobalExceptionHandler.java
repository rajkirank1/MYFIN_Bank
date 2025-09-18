package org.training.transactions.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFound ex) {
        ErrorResponse err = new ErrorResponse(ex.getErrorCode(), ex.getMessage());
        return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<ErrorResponse> handleAccountStatus(AccountStatusException ex) {
        ErrorResponse err = new ErrorResponse(ex.getErrorCode(), ex.getMessage());
        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientBalance.class)
    public ResponseEntity<ErrorResponse> handleInsufficient(InsufficientBalance ex) {
        ErrorResponse err = new ErrorResponse(ex.getErrorCode(), ex.getMessage());
        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ErrorResponse> handleGlobal(GlobalException ex) {
        String code = ex.getErrorCode();
        HttpStatus status = GlobalErrorCode.NOT_FOUND.equals(code) ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        ErrorResponse err = new ErrorResponse(code, ex.getMessage());
        return new ResponseEntity<>(err, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOther(Exception ex) {
        ErrorResponse err = new ErrorResponse(GlobalErrorCode.INTERNAL, ex.getMessage() == null ? "Internal server error" : ex.getMessage());
        return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
