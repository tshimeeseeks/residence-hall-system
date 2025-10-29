package com.rhs.backend.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.rhs.backend.model.User;
import com.rhs.backend.model.Admin;
import com.rhs.backend.model.Room;
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

import com.rhs.backend.model.Room;
import com.rhs.backend.repository.RoomRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/init")
@RequiredArgsConstructor
public class InitController {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final FirebaseAuth firebaseAuth;
    private final RoomRepository roomRepository;

    /**
     * Create the master admin account
     * This should only be called once during initial setup
     */

    /**
     * Initialize sample rooms in the database
     * This should be called once during initial setup
     */
    @PostMapping("/create-sample-rooms")
    public ResponseEntity<?> createSampleRooms() {
        try {
            log.info("Creating sample rooms...");

            // Check if rooms already exist
            long existingRooms = roomRepository.count();
            if (existingRooms > 0) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Rooms already exist in database. Count: " + existingRooms));
            }

            // Create sample rooms
            List<Room> rooms = new ArrayList<>();

            // Building A - Ground Floor (101-105)
            for (int i = 1; i <= 5; i++) {
                rooms.add(Room.builder()
                        .roomNumber("10" + i)
                        .type("Standard")
                        .block("A")
                        .building("Building A")
                        .floor(1)
                        .capacity(2)
                        .currentOccupancy(0)
                        .lastMaintenance(LocalDate.now().minusMonths(3))
                        .build());
            }

            // Building A - First Floor (201-205)
            for (int i = 1; i <= 5; i++) {
                rooms.add(Room.builder()
                        .roomNumber("20" + i)
                        .type("Standard")
                        .block("A")
                        .building("Building A")
                        .floor(2)
                        .capacity(2)
                        .currentOccupancy(0)
                        .lastMaintenance(LocalDate.now().minusMonths(2))
                        .build());
            }

            // Building A - Second Floor (301-305)
            for (int i = 1; i <= 5; i++) {
                rooms.add(Room.builder()
                        .roomNumber("30" + i)
                        .type("Deluxe")
                        .block("A")
                        .building("Building A")
                        .floor(3)
                        .capacity(3)
                        .currentOccupancy(0)
                        .lastMaintenance(LocalDate.now().minusMonths(1))
                        .build());
            }

            // Building B - Ground Floor (B101-B105)
            for (int i = 1; i <= 5; i++) {
                rooms.add(Room.builder()
                        .roomNumber("B10" + i)
                        .type("Standard")
                        .block("B")
                        .building("Building B")
                        .floor(1)
                        .capacity(2)
                        .currentOccupancy(0)
                        .lastMaintenance(LocalDate.now().minusMonths(4))
                        .build());
            }

            // Building B - First Floor (B201-B205)
            for (int i = 1; i <= 5; i++) {
                rooms.add(Room.builder()
                        .roomNumber("B20" + i)
                        .type("Standard")
                        .block("B")
                        .building("Building B")
                        .floor(2)
                        .capacity(2)
                        .currentOccupancy(0)
                        .lastMaintenance(LocalDate.now().minusMonths(2))
                        .build());
            }

            // Building C - Suite Rooms (C301-C305)
            for (int i = 1; i <= 5; i++) {
                rooms.add(Room.builder()
                        .roomNumber("C30" + i)
                        .type("Suite")
                        .block("C")
                        .building("Building C")
                        .floor(3)
                        .capacity(4)
                        .currentOccupancy(0)
                        .lastMaintenance(LocalDate.now())
                        .build());
            }

            // Save all rooms to database
            List<Room> savedRooms = roomRepository.saveAll(rooms);

            log.info("Successfully created {} sample rooms", savedRooms.size());

            return ResponseEntity.ok(Map.of(
                    "message", "Sample rooms created successfully",
                    "count", savedRooms.size(),
                    "rooms", savedRooms));

        } catch (Exception e) {
            log.error("Error creating sample rooms", e);
            return ResponseEntity.status(500).body(
                    Map.of("error", "Failed to create sample rooms: " + e.getMessage()));
        }
    }

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
                    .userType(UserType.SUPER_ADMIN.name()) // Convert enum to String
                    .accountStatus(AccountStatus.APPROVED.name()) // Convert enum to String
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
            // Find user in MongoDB - CHANGED: Look in users collection instead of admins
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "User not found in database"));
            }

            User user = userOpt.get();
            String firebaseUid = user.getFirebaseUid();

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