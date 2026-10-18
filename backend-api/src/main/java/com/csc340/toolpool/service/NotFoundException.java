package com.csc340.toolpool.service;

// Thrown when an id doesn't exist (e.g. GET /api/users/999).
// ApiExceptionHandler turns it into a 404 Not Found.
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}