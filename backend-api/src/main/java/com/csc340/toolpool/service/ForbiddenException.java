package com.csc340.toolpool.service;

// Thrown when the request is valid but the user isn't allowed to do it
// (e.g. a banned user trying to log in). ApiExceptionHandler turns it into a 403 Forbidden.
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
