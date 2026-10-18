package com.csc340.toolpool.dto;

import com.csc340.toolpool.entity.User;

import java.time.LocalDateTime;

// What the API sends back for a user. There is no password field, so it can never leak.
public record UserResponse(
        Long id,
        String name,
        String email,
        String phoneNumber,
        String location,
        String role,
        String accountStatus,
        LocalDateTime createdAt) {

    // Copies the fields we want to expose from a User entity.
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(),
                user.getPhoneNumber(), user.getLocation(), user.getRole(),
                user.getAccountStatus(), user.getCreatedAt());
    }
}