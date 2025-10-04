package com.springboottest.user_management_api.exception;

import com.springboottest.user_management_api.dto.response.ErrorResponse;
import com.springboottest.user_management_api.util.enums.ErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

    /**
     * Handle ResourceNotFoundException (404)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.name())
                .code(ErrorCode.RESOURCE_NOT_FOUND.getCode())
                .message(List.of(ex.getMessage()))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handle DuplicateResourceException (409)
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex) {
        log.error("Duplicate resource: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.name())
                .code(ErrorCode.DUPLICATE_RESOURCE.getCode())
                .message(List.of(ex.getMessage()))
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handle InvalidRequestException (422)
     */
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(InvalidRequestException ex) {
        log.error("Invalid resource: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.UNPROCESSABLE_ENTITY.name())
                .code(ErrorCode.INVALID_REQUEST.getCode())
                .message(List.of(ex.getMessage()))
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    /**
     * Handle bean validation errors (422)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.UNPROCESSABLE_ENTITY.name())
                .code(ErrorCode.INVALID_REQUEST.getCode())
                .message(errors)
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    /**
     * Handle Constraint Violation (422)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("Constraint violation: {}", ex.getMessage());

        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(this::formatConstraintViolation)
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.UNPROCESSABLE_ENTITY.name())
                .code(ErrorCode.INVALID_REQUEST.getCode())
                .message(errors)
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    /*
    * Handle all other exceptions (500)
    * */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("System error: ", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .code(ErrorCode.SYSTEM_ERROR.getCode())
                .message(List.of(ErrorCode.SYSTEM_ERROR.getMessageTemplate()))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Format field error message
     */
    private String formatFieldError(FieldError fieldError) {
        String fieldName = convertToSnakeCase(fieldError.getField());
        Object rejectedValue = fieldError.getRejectedValue();
        String rejectedValueStr = rejectedValue != null ? rejectedValue.toString() : "null";

        return String.format("Invalid value for field %s, rejected value: %s", fieldName, rejectedValueStr);
    }

    /**
     * Format constraint violation message
     */
    private String formatConstraintViolation(ConstraintViolation<?> violation) {
        String fieldName = getFieldNameFromPath(violation.getPropertyPath().toString());
        String rejectedValueStr = violation.getInvalidValue() != null ?
                violation.getInvalidValue().toString() : "null";

        return String.format("Invalid value for field %s, rejected value: %s", fieldName, rejectedValueStr);
    }

    /**
     * Convert camelCase to snake_case
     */
    private String convertToSnakeCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    /**
     * Extract field name from property path
     */
    private String getFieldNameFromPath(String path) {
        String[] parts = path.split("\\.");
        String fieldName = parts[parts.length - 1];
        return convertToSnakeCase(fieldName);
    }
}
