package com.csc340.toolpool.service;

// Thrown when login details are wrong (unknown email or wrong password).
// ApiExceptionHandler turns it into a 401 Unauthorized.
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
