package com.rhs.backend.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.rhs.backend.model.Admin;
import com.rhs.backend.model.embedded.AdminPermissions;
import com.rhs.backend.model.enums.AccountStatus;
import com.rhs.backend.model.enums.UserType;
import com.rhs.backend.repository.AdminRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/init")
@RequiredArgsConstructor
public class InitController {

    private final AdminRepository adminRepository;

    // Endpoint to set admin claim
    @PostMapping("/set-admin-claim")
    public ResponseEntity<Map<String, String>> setAdminClaim(@RequestParam String uid) {
        try {
            // Set custom claims
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", "ADMIN");

            FirebaseAuth.getInstance().setCustomUserClaims(uid, claims);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Admin claim set successfully!");
            response.put("uid", uid);

            return ResponseEntity.ok(response);

        } catch (FirebaseAuthException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Helper endpoint to verify claims
    @GetMapping("/verify-claims")
    public ResponseEntity<Map<String, Object>> verifyClaims(@RequestParam String uid) {
        try {
            UserRecord user = FirebaseAuth.getInstance().getUser(uid);
            Map<String, Object> response = new HashMap<>();
            response.put("uid", user.getUid());
            response.put("email", user.getEmail());
            response.put("customClaims", user.getCustomClaims());

            return ResponseEntity.ok(response);

        } catch (FirebaseAuthException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/create-master-admin")
    public ResponseEntity<Map<String, String>> createMasterAdmin(
            @RequestParam String firebaseUid,
            @RequestParam String email) {
        try {
            // Check if admin already exists
            if (adminRepository.existsByFirebaseUid(firebaseUid)) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Admin already exists in database");
                return ResponseEntity.ok(response);
            }

            // Create admin permissions
            AdminPermissions permissions = AdminPermissions.builder()
                    .role("MASTER_ADMIN")
                    .permissions(Arrays.asList(
                            "CREATE_ADMIN",
                            "DELETE_ADMIN",
                            "MANAGE_USERS",
                            "MANAGE_QUERIES",
                            "APPROVE_PASSES",
                            "MANAGE_ROOMS",
                            "VIEW_REPORTS",
                            "SYSTEM_CONFIGURATION"))
                    .canManageUsers(true)
                    .canManageQueries(true)
                    .canApprovePasses(true)
                    .canManageRooms(true)
                    .build();

            // Create admin
            Admin admin = Admin.builder()
                    .firebaseUid(firebaseUid)
                    .email(email)
                    .firstName("Master")
                    .lastName("Administrator")
                    .phoneNumber("0000000000")
                    .userType(UserType.ADMIN)
                    .accountStatus(AccountStatus.APPROVED)
                    .isEnabled(true)
                    .department("Administration")
                    .adminPermissions(permissions)
                    .build();

            adminRepository.save(admin);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Master admin created in MongoDB!");
            response.put("mongoId", admin.getId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
