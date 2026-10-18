package com.csc340.toolpool.service;

// Thrown for invalid input that @Valid can't catch on its own
// (e.g. endDate before startDate). ApiExceptionHandler turns it into a 400 Bad Request.
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}