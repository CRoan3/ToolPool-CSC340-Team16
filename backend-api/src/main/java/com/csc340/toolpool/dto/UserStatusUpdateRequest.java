package com.csc340.toolpool.dto;

import jakarta.validation.constraints.NotBlank;

// The JSON body for PUT /api/users/{id}/status (admin only): { "accountStatus": "BANNED" }
public record UserStatusUpdateRequest(
        @NotBlank String accountStatus) {
}
