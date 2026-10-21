package com.csc340.toolpool.service;

import com.csc340.toolpool.dto.UserRegistrationRequest;
import com.csc340.toolpool.dto.UserResponse;
import com.csc340.toolpool.dto.UserStatusUpdateRequest;
import com.csc340.toolpool.dto.UserUpdateRequest;
import com.csc340.toolpool.entity.User;
import com.csc340.toolpool.repository.ReportRepository;
import com.csc340.toolpool.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

// Business rules for users. The controller calls this class; only this class calls the repository.
@Service
@Transactional
public class UserService {

    private static final Set<String> ACCOUNT_STATUSES = Set.of("ACTIVE", "BANNED");

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    // Spring creates the repositories and passes them in automatically.
    public UserService(UserRepository userRepository, ReportRepository reportRepository) {
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
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

    // Story 7: view another user's profile. 404 if the id doesn't exist.
    // readOnly = true: this method only reads, never changes data.
    @Transactional(readOnly = true)
    public UserResponse getProfile(Long id) {
        return UserResponse.from(findUser(id));
    }

    // Edit your own profile: name, email, phone number, location.
    public UserResponse updateProfile(Long id, UserUpdateRequest request) {
        User user = findUser(id);

        // Changing to an email that another user already has: 409.
        // Keeping your own email (in any capitalization) is fine.
        if (!user.getEmail().equalsIgnoreCase(request.email())
                && userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("An account with that email already exists.");
        }

        user.setName(request.name().trim());
        user.setEmail(request.email().trim().toLowerCase(Locale.ROOT));
        user.setPhoneNumber(request.phoneNumber());
        user.setLocation(request.location());

        return UserResponse.from(userRepository.save(user));
    }

    // Admin (issue #40): list all users. Optional filter, e.g. ?accountStatus=BANNED.
    @Transactional(readOnly = true)
    public List<UserResponse> listUsers(String accountStatus) {
        List<User> users = accountStatus == null
                ? userRepository.findAllByOrderByIdAsc()
                : userRepository.findByAccountStatusOrderByIdAsc(validAccountStatus(accountStatus));
        return users.stream().map(UserResponse::from).toList();
    }

    // Admin (issue #40): ban or unban a user. 400 if the status is invalid, 404 if missing.
    public UserResponse updateAccountStatus(Long id, UserStatusUpdateRequest request) {
        User user = findUser(id);
        user.setAccountStatus(validAccountStatus(request.accountStatus()));
        return UserResponse.from(userRepository.save(user));
    }

    // Admin (issue #40): permanently remove a user. 404 if missing.
    // 409 if the user is still linked to other records; the admin should ban them instead.
    public void deleteUser(Long id) {
        User user = findUser(id);

        if (reportRepository.existsByReporterIdOrReportedUserId(id, id)) {
            throw new ConflictException("User " + id + " has reports. Ban the user instead.");
        }

        // Tools, rentals, and reviews also point at users. If any still do, the database
        // refuses the delete; flush() makes that happen here so it becomes a 409, not a 500.
        try {
            userRepository.delete(user);
            userRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException(
                    "User " + id + " still has tools, rentals, or reviews. Ban the user instead.");
        }
    }

    // Accepts "banned" or "BANNED"; anything other than ACTIVE/BANNED is a 400.
    private String validAccountStatus(String accountStatus) {
        String normalized = accountStatus.trim().toUpperCase(Locale.ROOT);
        if (!ACCOUNT_STATUSES.contains(normalized)) {
            throw new BadRequestException("accountStatus must be ACTIVE or BANNED.");
        }
        return normalized;
    }

    // Looks up a user by id, or throws NotFoundException (404).
    // Reused by every method that needs an existing user.
    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User " + id + " was not found."));
    }

}