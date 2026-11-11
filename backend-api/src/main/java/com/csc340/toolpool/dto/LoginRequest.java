package com.csc340.toolpool.dto;

import jakarta.validation.constraints.NotBlank;

// The JSON body for POST /api/users/login.
// The email works as a username: it is only matched, never verified.
public record LoginRequest(
        @NotBlank String email,
        @NotBlank String password) {
}
