package com.csc340.toolpool.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// The JSON body for PUT /api/users/{id}: the profile fields a user can edit.
// No password, role, or accountStatus: those can't be changed here.
public record UserUpdateRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email @Size(max = 254) String email,
        @Size(max = 20) String phoneNumber,
        @Size(max = 100) String location) {
}