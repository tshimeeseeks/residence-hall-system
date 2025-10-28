package com.rhs.backend.controller;

import com.rhs.backend.model.User;
import com.rhs.backend.service.UserService;
import com.rhs.backend.dto.UserProfileDTO;
import com.rhs.backend.dto.ChangePasswordDTO;
import com.rhs.backend.dto.UserStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * GET /users/profile
     * Get current user's profile
     * Uses Firebase token from Authorization header
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from "Bearer <token>"
            String token = authHeader.replace("Bearer ", "");

            User user = userService.getUserByFirebaseToken(token);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            // Convert to DTO to hide sensitive fields
            UserProfileDTO userDTO = new UserProfileDTO(user);

            return ResponseEntity.ok(userDTO);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }
    }

    /**
     * PUT /users/profile
     * Update current user's profile
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserProfileDTO profileUpdate) {
        try {
            String token = authHeader.replace("Bearer ", "");

            User updatedUser = userService.updateUserProfile(token, profileUpdate);

            return ResponseEntity.ok(new UserProfileDTO(updatedUser));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /users/change-password
     * Change user's password
     */
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ChangePasswordDTO passwordDTO) {
        try {
            String token = authHeader.replace("Bearer ", "");

            userService.changePassword(token, passwordDTO);

            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /users (admin only)
     * List all users
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            // Verify admin access
            if (!userService.isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin privileges required"));
            }

            List<User> users = userService.getAllUsers();

            return ResponseEntity.ok(users);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized"));
        }
    }

    /**
     * GET /users/{id} (admin only)
     * Get specific user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String id) {
        try {
            String token = authHeader.replace("Bearer ", "");

            // Verify admin access
            if (!userService.isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin privileges required"));
            }

            User user = userService.getUserById(id);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            return ResponseEntity.ok(user);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /users/{id}/status (admin only)
     * Activate or deactivate user
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateUserStatus(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String id,
            @RequestBody UserStatusDTO statusDTO) {
        try {
            String token = authHeader.replace("Bearer ", "");

            // Verify admin access
            if (!userService.isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin privileges required"));
            }

            User updatedUser = userService.updateUserStatus(id, statusDTO);

            return ResponseEntity.ok(updatedUser);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /users/{id} (admin only)
     * Soft delete user
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String id) {
        try {
            String token = authHeader.replace("Bearer ", "");

            // Verify admin access
            if (!userService.isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin privileges required"));
            }

            userService.softDeleteUser(id);

            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}