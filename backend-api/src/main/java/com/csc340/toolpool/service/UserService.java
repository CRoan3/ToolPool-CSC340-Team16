package com.csc340.toolpool.service;

import com.csc340.toolpool.dto.UserRegistrationRequest;
import com.csc340.toolpool.dto.UserResponse;
import com.csc340.toolpool.entity.User;
import com.csc340.toolpool.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

// Business rules for users. The controller calls this class; only this class calls the repository.
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    // Spring creates the repository and passes it in automatically.
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // US-1: create an account.
    public UserResponse register(UserRegistrationRequest request) {
        // Emails must be unique: 409 if one is already taken.
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("An account with that email already exists.");
        }

        // Copy the allowed request fields into a new entity.
        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(request.email().trim().toLowerCase(Locale.ROOT));
        user.setPassword(request.password()); // TODO: hash with BCrypt later, like the demo
        user.setPhoneNumber(request.phoneNumber());
        user.setLocation(request.location());

        // Server-controlled fields: clients can't choose these.
        user.setRole("USER");
        user.setAccountStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());

        // Save, then return the response DTO (no password).
        return UserResponse.from(userRepository.save(user));
    }
}