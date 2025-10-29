package com.rhs.backend.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.rhs.backend.model.Admin;
import com.rhs.backend.model.Student;
import com.rhs.backend.model.User;
import com.rhs.backend.model.enums.AccountStatus;
import com.rhs.backend.security.FirebaseUserDetails;
import com.rhs.backend.service.UserService;
import com.rhs.backend.repository.AdminRepository;
import com.rhs.backend.repository.StudentRepository;
import com.rhs.backend.model.enums.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;
    private final FirebaseAuth firebaseAuth;

    /**
     * POST /api/auth/signup - Student self-registration
     */
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> studentSignup(
            @RequestBody Map<String, Object> signupData) {

        try {
            log.info("Processing student signup request");

            // Extract data
            String email = (String) signupData.get("email");
            String password = (String) signupData.get("password");
            String firstName = (String) signupData.get("firstName");
            String lastName = (String) signupData.get("lastName");
            String studentNumber = (String) signupData.get("studentNumber");
            String phoneNumber = (String) signupData.get("phoneNumber");
            String course = (String) signupData.get("course");
            Integer yearOfStudy = signupData.get("yearOfStudy") != null
                    ? ((Number) signupData.get("yearOfStudy")).intValue()
                    : null;

            // Validate required fields
            if (email == null || password == null || firstName == null ||
                    lastName == null || studentNumber == null || phoneNumber == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Missing required fields"));
            }

            // Check if student already exists
            if (studentRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "A student with this email already exists"));
            }

            if (studentRepository.findByStudentNumber(studentNumber).isPresent()) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "A student with this student number already exists"));
            }

            // Step 1: Create Firebase user
            UserRecord.CreateRequest firebaseRequest = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password)
                    .setDisplayName(firstName + " " + lastName)
                    .setEmailVerified(false); // Students need to verify email

            UserRecord firebaseUser;
            try {
                firebaseUser = firebaseAuth.createUser(firebaseRequest);
                log.info("Firebase user created: {}", firebaseUser.getUid());
            } catch (FirebaseAuthException e) {
                log.error("Failed to create Firebase user", e);
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Failed to create account: " + e.getMessage()));
            }

            // Step 2: Create student in MongoDB
            Student student = Student.builder()
                    .firebaseUid(firebaseUser.getUid())
                    .email(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .studentNumber(studentNumber)
                    .phoneNumber(phoneNumber)
                    .course(course)
                    .yearOfStudy(yearOfStudy)
                    .accountStatus(AccountStatus.PENDING) // Pending admin approval
                    .isEnabled(false) // Disabled until approved
                    .createdAt(LocalDateTime.now())
                    .build();

            Student savedStudent = studentRepository.save(student);

            log.info("Student registered successfully: {} ({})", email, savedStudent.getId());

            // Return success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registration successful! Your account is pending approval.");
            response.put("studentId", savedStudent.getId());
            response.put("email", savedStudent.getEmail());
            response.put("firebaseUid", firebaseUser.getUid());
            response.put("accountStatus", "PENDING");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Error during student signup", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }

    /**
     * Verify Firebase token and sync user data
     */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyToken(
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {

        String firebaseUid = userDetails.getUid();
        String email = userDetails.getEmail();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("firebaseUid", firebaseUid);
        response.put("email", email);

        // Try to find user as Admin first
        Optional<Admin> adminOpt = adminRepository.findByFirebaseUid(firebaseUid);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            response.put("userId", admin.getId());
            response.put("userType", admin.getUserType());
            response.put("accountStatus", admin.getAccountStatus());
            response.put("isAdmin", true);
            response.put("firstName", admin.getFirstName());
            response.put("lastName", admin.getLastName());
            response.put("department", admin.getDepartment());
            return ResponseEntity.ok(response);
        }

        // Try to find user as Student
        Optional<Student> studentOpt = studentRepository.findByFirebaseUid(firebaseUid);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            response.put("userId", student.getId());
            response.put("userType", "STUDENT");
            response.put("accountStatus", student.getAccountStatus().name());
            response.put("isAdmin", false);
            response.put("firstName", student.getFirstName());
            response.put("lastName", student.getLastName());
            response.put("studentNumber", student.getStudentNumber());
            response.put("roomId", student.getRoomId());
            return ResponseEntity.ok(response);
        }

        // User not found in database
        response.put("error", "User not registered in system");
        return ResponseEntity.status(404).body(response);
    }

    /**
     * Get current user information
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {

        String firebaseUid = userDetails.getUid();

        // Try to find user as Admin first
        Optional<Admin> adminOpt = adminRepository.findByFirebaseUid(firebaseUid);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("userId", admin.getId());
            response.put("email", admin.getEmail());
            response.put("firstName", admin.getFirstName());
            response.put("lastName", admin.getLastName());
            response.put("phoneNumber", admin.getPhoneNumber());
            response.put("userType", admin.getUserType());
            response.put("accountStatus", admin.getAccountStatus());
            response.put("isAdmin", true);
            response.put("department", admin.getDepartment());

            // Check if super admin based on permissions
            boolean isSuperAdmin = false;
            if (admin.getAdminPermissions() != null) {
                isSuperAdmin = "SUPER_ADMIN".equals(admin.getAdminPermissions().getRole());
            }
            response.put("isSuperAdmin", isSuperAdmin);

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
            response.put("isSuperAdmin", false);
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
                "email", userDetails.getEmail(),
                "isAdmin", userDetails.isAdmin()));
    }
}