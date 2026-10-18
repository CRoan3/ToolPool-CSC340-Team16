package com.csc340.toolpool.controller;

import com.csc340.toolpool.dto.UserRegistrationRequest;
import com.csc340.toolpool.dto.UserResponse;
import com.csc340.toolpool.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

// Maps HTTP requests under /api/users to the user service.
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // POST /api/users (issue #38) -> 201, 400 if invalid, 409 if email taken.
    // @Valid checks the rules on UserRegistrationRequest before this runs.
    @PostMapping
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        UserResponse user = userService.register(request);
        // 201 Created, with a Location header pointing at the new user.
        return ResponseEntity.created(URI.create("/api/users/" + user.id())).body(user);
    }
}