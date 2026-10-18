package com.csc340.toolpool.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// The JSON body for POST /api/users. Only these fields can be sent;
// id, role, accountStatus, and createdAt are set by the server.
public record UserRegistrationRequest(
                @NotBlank @Size(max = 100) String name,
                @NotBlank @Email @Size(max = 254) String email,
                @NotBlank @Size(min = 8, max = 72) String password,
                @Size(max = 20) String phoneNumber,
                @Size(max = 100) String location) {
}