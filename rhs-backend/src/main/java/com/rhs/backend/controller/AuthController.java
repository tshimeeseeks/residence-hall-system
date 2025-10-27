package com.rhs.backend.controller;

import com.rhs.backend.model.Student;
import com.rhs.backend.model.User;
import com.rhs.backend.security.FirebaseUserDetails;
import com.rhs.backend.service.UserService;
import com.rhs.backend.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final StudentRepository studentRepository;

    /**
     * Verify Firebase token and sync user data
     */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyToken(
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {

        String firebaseUid = userDetails.getUid();
        String email = userDetails.getEmail();

        // Try to find user in database (could be Admin/User or Student)
        Optional<User> userOpt = userService.getUserByFirebaseUid(firebaseUid);
        Optional<Student> studentOpt = studentRepository.findByFirebaseUid(firebaseUid);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("firebaseUid", firebaseUid);
        response.put("email", email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            response.put("userId", user.getId());
            response.put("userType", user.getUserType().name());
            response.put("accountStatus", user.getAccountStatus().name());
            response.put("isAdmin", user.isAdmin());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
        } else if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            response.put("userId", student.getId());
            response.put("userType", "STUDENT");
            response.put("accountStatus", student.getAccountStatus().name());
            response.put("isAdmin", false);
            response.put("firstName", student.getFirstName());
            response.put("lastName", student.getLastName());
            response.put("studentNumber", student.getStudentNumber());
            response.put("roomId", student.getRoomId());
        } else {
            // User not found in database
            response.put("error", "User not registered in system");
            return ResponseEntity.status(404).body(response);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Get current user information
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {

        String firebaseUid = userDetails.getUid();

        // Try to find user as Admin/User first
        Optional<User> userOpt = userService.getUserByFirebaseUid(firebaseUid);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("phoneNumber", user.getPhoneNumber());
            response.put("userType", user.getUserType().name());
            response.put("accountStatus", user.getAccountStatus().name());
            response.put("isAdmin", user.isAdmin());
            response.put("isSuperAdmin", user.isSuperAdmin());
            return ResponseEntity.ok(response);
        }

        // Try to find user as Student
        Optional<Student> studentOpt = studentRepository.findByFirebaseUid(firebaseUid);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("userId", student.getId());
            response.put("email", student.getEmail());
            response.put("firstName", student.getFirstName());
            response.put("lastName", student.getLastName());
            response.put("phoneNumber", student.getPhoneNumber());
            response.put("userType", "STUDENT");
            response.put("accountStatus", student.getAccountStatus().name());
            response.put("studentNumber", student.getStudentNumber());
            response.put("roomId", student.getRoomId());
            response.put("course", student.getCourse());
            response.put("yearOfStudy", student.getYearOfStudy());
            response.put("isAdmin", false);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(404).body(
                Map.of("error", "User not found"));
    }

    /**
     * Logout endpoint (mainly for logging purposes)
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {

        log.info("User logged out: {}", userDetails.getEmail());

        return ResponseEntity.ok(Map.of(
                "message", "Logged out successfully"));
    }

    /**
     * Check authentication status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> checkStatus(
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {

        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "firebaseUid", userDetails.getUid(),
                "email", userDetails.getEmail()));
    }
}