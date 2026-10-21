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
import com.csc340.toolpool.dto.UserUpdateRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import com.csc340.toolpool.dto.UserStatusUpdateRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.util.List;

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

    // GET /api/users/{id} (issue #39) -> 200, 404 if the id doesn't exist.
    // @PathVariable takes the {id} part of the URL, e.g. /api/users/3 -> id = 3.
    @GetMapping("/{id}")
    public UserResponse getProfile(@PathVariable Long id) {
        return userService.getProfile(id);
    }

    // PUT /api/users/{id} (issue #39) -> 200, 400 if invalid, 404 if missing, 409
    // if email taken.
    @PutMapping("/{id}")
    public UserResponse updateProfile(@PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateProfile(id, request);
    }

    // GET /api/users (issue #40) -> 200. Optional ?accountStatus=ACTIVE or BANNED (400 if anything else).
    @GetMapping
    public List<UserResponse> listUsers(@RequestParam(required = false) String accountStatus) {
        return userService.listUsers(accountStatus);
    }

    // PUT /api/users/{id}/status (issue #40) -> 200, 400 if invalid status, 404 if missing.
    @PutMapping("/{id}/status")
    public UserResponse updateAccountStatus(@PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateRequest request) {
        return userService.updateAccountStatus(id, request);
    }

    // DELETE /api/users/{id} (issue #40) -> 204, 404 if missing, 409 if the user has linked records.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}