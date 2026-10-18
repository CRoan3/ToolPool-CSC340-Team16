package com.csc340.toolpool.controller;

import com.csc340.toolpool.service.BadRequestException;
import com.csc340.toolpool.service.ConflictException;
import com.csc340.toolpool.service.NotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

// One place that turns exceptions from any controller or service into JSON error responses:
// { "error": "message" }
@RestControllerAdvice
public class ApiExceptionHandler {

    // 404: an id that doesn't exist
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    // 409: duplicates or invalid state changes.
    // DataIntegrityViolationException is the database's own unique/foreign-key
    // error,
    // a backup if a service check is ever missed.
    @ExceptionHandler({ ConflictException.class, DataIntegrityViolationException.class })
    public ResponseEntity<Map<String, String>> handleConflict(RuntimeException exception) {
        String message = exception instanceof ConflictException
                ? exception.getMessage()
                : "The request conflicts with an existing record.";
        return error(HttpStatus.CONFLICT, message);
    }

    // 400: invalid input found by our own service checks
    @ExceptionHandler({ BadRequestException.class, ConstraintViolationException.class })
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException exception) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    // 400: @Valid failed on a request body. Lists every broken rule,
    // e.g. "password: size must be between 8 and 72; email: must be a well-formed
    // email address"
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleInvalidBody(
            MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return error(HttpStatus.BAD_REQUEST, message);
    }

    // Builds the response: the status code plus { "error": message }
    private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }
}