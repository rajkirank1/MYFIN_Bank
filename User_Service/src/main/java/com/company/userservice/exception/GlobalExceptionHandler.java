package com.company.userservice.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Centralized exception handler that converts exceptions into ErrorResponse payloads.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${app.error.code.notfound:404}")
    private String errorCodeNotFound;

    @Value("${app.error.code.conflict:409}")
    private String errorCodeConflict;

    @Value("${app.error.code.badrequest:400}")
    private String errorCodeBadRequest;

    @Value("${app.error.code.internal:500}")
    private String errorCodeInternal;

    /**
     * Handle our own GlobalException subtypes.
     */
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(GlobalException ex) {
        String code = ex.getErrorCode() != null ? ex.getErrorCode() : errorCodeBadRequest;
        ErrorResponse er = new ErrorResponse();
        er.setErrorCode(code);
        er.setErrorMessage(ex.getMessage() != null ? ex.getMessage() : "An error occurred");

        HttpStatus status = resolveHttpStatus(code);
        return new ResponseEntity<>(er, status);
    }

    /**
     * Fallback for any other exceptions not handled explicitly.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse er = new ErrorResponse();
        er.setErrorCode(errorCodeInternal);
        er.setErrorMessage(ex.getMessage() != null ? ex.getMessage() : "Internal server error");
        return new ResponseEntity<>(er, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle validation errors (e.g., @Valid failures).
     *
     * Note: signature uses HttpStatusCode to be compatible with Spring 6+.
     */
    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map((FieldError fe) -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponse er = new ErrorResponse();
        er.setErrorCode(errorCodeBadRequest);
        er.setErrorMessage(String.join("; ", errors));

        return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
    }

    private HttpStatus resolveHttpStatus(String code) {
        if (code == null) return HttpStatus.BAD_REQUEST;
        switch (code) {
            case GlobalError.NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            case GlobalError.CONFLICT:
                return HttpStatus.CONFLICT;
            case GlobalError.INTERNAL:
                return HttpStatus.INTERNAL_SERVER_ERROR;
            case GlobalError.BAD_REQUEST:
                return HttpStatus.BAD_REQUEST;
            default:
                // try numeric
                try {
                    int c = Integer.parseInt(code);
                    if (c >= 500) return HttpStatus.INTERNAL_SERVER_ERROR;
                    if (c >= 400) return HttpStatus.BAD_REQUEST;
                } catch (NumberFormatException e) {
                    // fall through
                }
                return HttpStatus.BAD_REQUEST;
        }
    }

    // explicit getters/setters for configurable codes (if needed programmatically)
    public String getErrorCodeNotFound() {
        return errorCodeNotFound;
    }

    public void setErrorCodeNotFound(String errorCodeNotFound) {
        this.errorCodeNotFound = errorCodeNotFound;
    }

    public String getErrorCodeConflict() {
        return errorCodeConflict;
    }

    public void setErrorCodeConflict(String errorCodeConflict) {
        this.errorCodeConflict = errorCodeConflict;
    }

    public String getErrorCodeBadRequest() {
        return errorCodeBadRequest;
    }

    public void setErrorCodeBadRequest(String errorCodeBadRequest) {
        this.errorCodeBadRequest = errorCodeBadRequest;
    }

    public String getErrorCodeInternal() {
        return errorCodeInternal;
    }

    public void setErrorCodeInternal(String errorCodeInternal) {
        this.errorCodeInternal = errorCodeInternal;
    }
}
