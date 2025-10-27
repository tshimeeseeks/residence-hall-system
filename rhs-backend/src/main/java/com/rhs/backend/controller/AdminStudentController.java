package com.rhs.backend.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.rhs.backend.dto.request.AdminCreateStudentRequest;
import com.rhs.backend.dto.response.StudentCreatedResponse;
import com.rhs.backend.model.Student;
import com.rhs.backend.model.Room;
import com.rhs.backend.model.embedded.RoomDetails;
import com.rhs.backend.model.enums.UserType;
import com.rhs.backend.model.enums.AccountStatus;
import com.rhs.backend.repository.StudentRepository;
import com.rhs.backend.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/students")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminStudentController {

        private final StudentRepository studentRepository;
        private final RoomRepository roomRepository;
        private final FirebaseAuth firebaseAuth;

        @PostMapping
        public ResponseEntity<?> createStudent(@Valid @RequestBody AdminCreateStudentRequest request) {
                try {
                        String firebaseUid = getCurrentUserFirebaseUid();

                        // Create Firebase user
                        UserRecord.CreateRequest firebaseRequest = new UserRecord.CreateRequest()
                                        .setEmail(request.getEmail())
                                        .setPassword(request.getPassword())
                                        .setDisplayName(request.getFirstName() + " " + request.getLastName())
                                        .setEmailVerified(false);

                        UserRecord firebaseUser = firebaseAuth.createUser(firebaseRequest);

                        // Verify room exists and get room information
                        Room room = roomRepository.findById(request.getRoomId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Room not found with id: " + request.getRoomId()));

                        // Check if room has available capacity
                        if (!room.isAvailable()) {
                                // Delete the Firebase user since we can't complete the registration
                                firebaseAuth.deleteUser(firebaseUser.getUid());
                                throw new RuntimeException("Room is at full capacity");
                        }

                        // Create student in MongoDB
                        Student student = Student.builder()
                                        .firebaseUid(firebaseUser.getUid())
                                        .email(request.getEmail())
                                        .firstName(request.getFirstName())
                                        .lastName(request.getLastName())
                                        .phoneNumber(request.getPhoneNumber())
                                        .userType(UserType.STUDENT)
                                        .accountStatus(AccountStatus.APPROVED)
                                        .isEnabled(true)
                                        .studentNumber(request.getStudentNumber())
                                        .roomId(request.getRoomId())
                                        .course(request.getCourse())
                                        .yearOfStudy(request.getYearOfStudy())
                                        .emergencyContact(request.getEmergencyContact())
                                        .createdAt(LocalDateTime.now())
                                        .build();

                        studentRepository.save(student);

                        // Update room occupancy
                        room.incrementOccupancy();
                        roomRepository.save(room);

                        log.info("Admin {} created student account for: {}", firebaseUid, student.getEmail());

                        // Return response with credentials for admin to share with student
                        StudentCreatedResponse response = StudentCreatedResponse.builder()
                                        .message("Student account created successfully")
                                        .studentId(student.getId())
                                        .firebaseUid(firebaseUser.getUid())
                                        .email(request.getEmail())
                                        .temporaryPassword(request.getPassword())
                                        .studentNumber(request.getStudentNumber())
                                        .fullName(request.getFirstName() + " " + request.getLastName())
                                        .roomNumber(room.getRoomNumber())
                                        .building(room.getBuilding())
                                        .floor(room.getFloor())
                                        .build();

                        return ResponseEntity.status(HttpStatus.CREATED).body(response);

                } catch (FirebaseAuthException e) {
                        log.error("Failed to create Firebase user", e);
                        return ResponseEntity.badRequest().body(
                                        Map.of("error", "Failed to create Firebase user: " + e.getMessage()));
                } catch (Exception e) {
                        log.error("Failed to create student", e);
                        return ResponseEntity.badRequest().body(
                                        Map.of("error", e.getMessage()));
                }
        }

        @GetMapping("/{id}")
        public ResponseEntity<?> getStudent(@PathVariable String id) {
                try {
                        Student student = studentRepository.findById(id)
                                        .orElseThrow(() -> new RuntimeException("Student not found"));

                        return ResponseEntity.ok(student);
                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(
                                        Map.of("error", e.getMessage()));
                }
        }

        @PutMapping("/{id}")
        public ResponseEntity<?> updateStudent(
                        @PathVariable String id,
                        @Valid @RequestBody AdminCreateStudentRequest request) {
                try {
                        Student student = studentRepository.findById(id)
                                        .orElseThrow(() -> new RuntimeException("Student not found"));

                        // Update student fields
                        if (request.getFirstName() != null) {
                                student.setFirstName(request.getFirstName());
                        }
                        if (request.getLastName() != null) {
                                student.setLastName(request.getLastName());
                        }
                        if (request.getPhoneNumber() != null) {
                                student.setPhoneNumber(request.getPhoneNumber());
                        }
                        if (request.getStudentNumber() != null) {
                                student.setStudentNumber(request.getStudentNumber());
                        }
                        if (request.getCourse() != null) {
                                student.setCourse(request.getCourse());
                        }
                        if (request.getYearOfStudy() != null) {
                                student.setYearOfStudy(request.getYearOfStudy());
                        }

                        // Handle room change if needed
                        if (request.getRoomId() != null && !request.getRoomId().equals(student.getRoomId())) {
                                // Remove from old room
                                if (student.getRoomId() != null) {
                                        Room oldRoom = roomRepository.findById(student.getRoomId()).orElse(null);
                                        if (oldRoom != null) {
                                                oldRoom.decrementOccupancy();
                                                roomRepository.save(oldRoom);
                                        }
                                }

                                // Add to new room
                                Room newRoom = roomRepository.findById(request.getRoomId())
                                                .orElseThrow(() -> new RuntimeException("Room not found"));

                                if (!newRoom.isAvailable()) {
                                        throw new RuntimeException("New room is at full capacity");
                                }

                                student.setRoomId(request.getRoomId());
                                newRoom.incrementOccupancy();
                                roomRepository.save(newRoom);
                        }

                        student.setUpdatedAt(LocalDateTime.now());
                        studentRepository.save(student);

                        return ResponseEntity.ok(student);

                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(
                                        Map.of("error", e.getMessage()));
                }
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<?> deleteStudent(@PathVariable String id) {
                try {
                        Student student = studentRepository.findById(id)
                                        .orElseThrow(() -> new RuntimeException("Student not found"));

                        // Remove from room
                        if (student.getRoomId() != null) {
                                Room room = roomRepository.findById(student.getRoomId()).orElse(null);
                                if (room != null) {
                                        room.decrementOccupancy();
                                        roomRepository.save(room);
                                }
                        }

                        // Delete from Firebase
                        try {
                                firebaseAuth.deleteUser(student.getFirebaseUid());
                        } catch (FirebaseAuthException e) {
                                log.warn("Failed to delete Firebase user: {}", e.getMessage());
                        }

                        // Delete from MongoDB
                        studentRepository.delete(student);

                        return ResponseEntity.ok(Map.of("message", "Student deleted successfully"));

                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(
                                        Map.of("error", e.getMessage()));
                }
        }

        private String getCurrentUserFirebaseUid() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null || !authentication.isAuthenticated()) {
                        throw new RuntimeException("User not authenticated");
                }
                return authentication.getName();
        }
}