package com.rhs.backend.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.rhs.backend.dto.request.AdminCreateStudentRequest;
import com.rhs.backend.dto.response.StudentCreatedResponse;
import com.rhs.backend.model.Student;
import com.rhs.backend.model.embedded.RoomDetails;
import com.rhs.backend.model.enums.AccountStatus;
import com.rhs.backend.model.enums.UserType;
import com.rhs.backend.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/students")
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
public class AdminStudentController {

    private final StudentRepository studentRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createStudent(@Valid @RequestBody AdminCreateStudentRequest request) {
        try {
            // Check for duplicates
            if (studentRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Email already exists"));
            }

            if (studentRepository.existsByStudentNumber(request.getStudentNumber())) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Student number already exists"));
            }

            // Create user in Firebase with admin-generated password
            UserRecord.CreateRequest firebaseRequest = new UserRecord.CreateRequest()
                    .setEmail(request.getEmail())
                    .setPassword(request.getPassword())
                    .setDisplayName(request.getFirstName() + " " + request.getLastName())
                    .setDisabled(false); // Enabled immediately since admin created

            UserRecord firebaseUser = FirebaseAuth.getInstance().createUser(firebaseRequest);

            // Set custom claims (STUDENT role)
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", "STUDENT");
            FirebaseAuth.getInstance().setCustomUserClaims(firebaseUser.getUid(), claims);

            // Create room details
            RoomDetails roomDetails = RoomDetails.builder()
                    .roomId(request.getRoomId())
                    .building(request.getBuilding())
                    .floor(request.getFloor())
                    .build();

            // Create student in MongoDB
            Student student = Student.builder()
                    .firebaseUid(firebaseUser.getUid())
                    .email(request.getEmail())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .phoneNumber(request.getPhoneNumber())
                    .userType(UserType.STUDENT)
                    .accountStatus(AccountStatus.APPROVED) // Auto-approved
                    .isEnabled(true)
                    .studentNumber(request.getStudentNumber())
                    .roomDetails(roomDetails)
                    .course(request.getCourse())
                    .yearOfStudy(request.getYearOfStudy())
                    .build();

            studentRepository.save(student);

            // Return response with credentials for admin to share
            StudentCreatedResponse response = StudentCreatedResponse.builder()
                    .message("Student account created successfully!")
                    .studentId(student.getId())
                    .firebaseUid(firebaseUser.getUid())
                    .email(request.getEmail())
                    .temporaryPassword(request.getPassword()) // Admin sees this to share with student
                    .studentNumber(request.getStudentNumber())
                    .fullName(request.getFirstName() + " " + request.getLastName())
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (FirebaseAuthException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Firebase error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Error creating student: " + e.getMessage()));
        }
    }
}