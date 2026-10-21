
package com.example.hosteltracker.controller;

import com.example.hosteltracker.model.UserEntity;
import com.example.hosteltracker.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Display all accounts without exposing passwords
    @GetMapping
    public List<Map<String, Object>> getUsers() {
        return userRepository.findAll().stream()
                .map(this::accountDetails)
                .toList();
    }

    // Create a new account
    @PostMapping
    public ResponseEntity<?> createUser(
            @RequestBody Map<String, String> request) {

        String loginId = request.get("loginId");
        String password = request.get("password");
        String role = request.get("role");

        if (loginId == null || password == null || role == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "All fields are required."));
        }

        loginId = loginId.trim();
        role = role.trim().toUpperCase();

        if (password.length() < 8 || password.length() > 72) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Password must contain 8 to 72 characters."));
        }

        if (!Set.of("STUDENT", "RC", "STAFF").contains(role)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid role."));
        }

        boolean validId =
                (role.equals("STUDENT") && loginId.matches("stud@[0-9]{4}"))
                        || (role.equals("RC") && loginId.matches("rc@[0-9]{4}"))
                        || (role.equals("STAFF") && loginId.matches("staff@[0-9]{4}"));

        if (!validId) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Login ID does not match the selected role."));
        }

        if (userRepository.existsByLoginId(loginId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Login ID already exists."));
        }

        UserEntity user = new UserEntity(
                loginId,
                passwordEncoder.encode(password),
                role
        );

        UserEntity savedUser = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountDetails(savedUser));
    }

    // Enable or disable an account
    @PatchMapping("/{id}/enabled")
    public ResponseEntity<?> setEnabled(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {

        Boolean enabled = request.get("enabled");

        if (enabled == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Provide enabled as true or false."));
        }

        return userRepository.findById(id)
                .map(user -> {
                    if ("ADMIN".equals(user.getRole())) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("message",
                                        "Admin accounts cannot be changed here."));
                    }

                    user.setEnabled(enabled);
                    UserEntity savedUser = userRepository.save(user);

                    return ResponseEntity.ok(accountDetails(savedUser));
                })
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of("message", "Account not found.")));
    }

    private Map<String, Object> accountDetails(UserEntity user) {
        return Map.of(
                "id", user.getId(),
                "loginId", user.getLoginId(),
                "role", user.getRole(),
                "enabled", user.isEnabled()
        );
    }
}
