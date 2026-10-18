package com.csc340.toolpool.service;

// Thrown when a request clashes with existing data (e.g. an email already in use).
// ApiExceptionHandler turns it into a 409 Conflict.
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
