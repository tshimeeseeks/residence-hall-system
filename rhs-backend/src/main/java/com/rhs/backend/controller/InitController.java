package com.rhs.backend.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.rhs.backend.model.User;
import com.rhs.backend.model.Admin;
import com.rhs.backend.model.enums.AccountStatus;
import com.rhs.backend.model.enums.UserType;
import com.rhs.backend.repository.UserRepository;
import com.rhs.backend.repository.AdminRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/init")
@RequiredArgsConstructor
public class InitController {

    private final UserRepository userRepository; // Fixed: removed extra semicolon and added 'final'
    private final AdminRepository adminRepository; // Added: missing repository
    private final FirebaseAuth firebaseAuth;

    /**
     * Create the master admin account
     * This should only be called once during initial setup
     */
    @PostMapping("/create-master-admin")
    public ResponseEntity<?> createMasterAdmin() {
        try {
            // Check if master admin already exists
            if (userRepository.findByEmail("admin@rhs.com").isPresent()) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Master admin already exists"));
            }

            // Create Firebase user
            String email = "admin@rhs.com";
            String password = "Admin@123456"; // Change this in production!

            UserRecord.CreateRequest firebaseRequest = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password)
                    .setDisplayName("Master Administrator")
                    .setEmailVerified(true);

            UserRecord firebaseUser = firebaseAuth.createUser(firebaseRequest);

            // Create admin in MongoDB
            User admin = User.builder()
                    .firebaseUid(firebaseUser.getUid())
                    .email(email)
                    .firstName("Master")
                    .lastName("Administrator")
                    .phoneNumber("0000000000")
                    .userType(UserType.SUPER_ADMIN)
                    .accountStatus(AccountStatus.APPROVED)
                    .isEnabled(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(admin);

            log.info("Master admin created successfully");

            Map<String, String> response = Map.of(
                    "message", "Master admin created in MongoDB!",
                    "mongoId", admin.getId(),
                    "email", email,
                    "password", password,
                    "note", "Please change this password immediately!");

            return ResponseEntity.ok(response);

        } catch (FirebaseAuthException e) {
            log.error("Error creating master admin in Firebase", e);
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Firebase error: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating master admin", e);
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/set-admin-claim")
    public ResponseEntity<?> setAdminClaim(@RequestParam String email) {
        try {
            // Find user in MongoDB
            Optional<Admin> adminOpt = adminRepository.findByEmail(email);
            if (adminOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Admin not found in database"));
            }

            Admin admin = adminOpt.get();
            String firebaseUid = admin.getFirebaseUid();

            // Set custom claims in Firebase
            Map<String, Object> claims = new HashMap<>();
            claims.put("admin", true);
            firebaseAuth.setCustomUserClaims(firebaseUid, claims);

            log.info("Admin custom claim set for user: {}", email);

            return ResponseEntity.ok(Map.of(
                    "message", "Admin claim set successfully",
                    "email", email,
                    "firebaseUid", firebaseUid,
                    "note", "User must log out and log back in for changes to take effect"));

        } catch (FirebaseAuthException e) {
            log.error("Error setting admin claim", e);
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Firebase error: " + e.getMessage()));
        }
    }

    /**
     * Health check endpoint
     */
    @PostMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "message", "Init controller is running",
                "timestamp", LocalDateTime.now()));
    }
}