package com.rhs.backend.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.rhs.backend.model.Room;
import com.rhs.backend.model.Student;
import com.rhs.backend.model.enums.AccountStatus;
import com.rhs.backend.repository.RoomRepository;
import com.rhs.backend.repository.StudentRepository;
import com.rhs.backend.security.FirebaseUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/admin/students")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminStudentController {

    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;
    private final FirebaseAuth firebaseAuth;

    /**
     * POST /api/admin/students - Create new student (admin only)
     */
    @PostMapping
    public ResponseEntity<?> createStudent(
            @AuthenticationPrincipal FirebaseUserDetails adminDetails,
            @RequestBody Map<String, Object> studentData) {

        try {
            log.info("Admin {} creating student account", adminDetails.getEmail());

            // Verify admin permissions
            if (!adminDetails.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin privileges required"));
            }

            // Extract data
            String email = (String) studentData.get("email");
            String password = (String) studentData.get("password");
            String firstName = (String) studentData.get("firstName");
            String lastName = (String) studentData.get("lastName");
            String studentNumber = (String) studentData.get("studentNumber");
            String phoneNumber = (String) studentData.get("phoneNumber");
            String course = (String) studentData.get("course");
            Integer yearOfStudy = (Integer) studentData.get("yearOfStudy");
            String roomId = (String) studentData.get("roomId");
            String accountStatusStr = (String) studentData.getOrDefault("accountStatus", "PENDING");

            // Validate required fields
            if (email == null || password == null || firstName == null ||
                    lastName == null || studentNumber == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Missing required fields"));
            }

            // Check if student already exists
            if (studentRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Student with this email already exists"));
            }

            if (studentRepository.findByStudentNumber(studentNumber).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Student with this student number already exists"));
            }

            // Step 1: Create Firebase user
            UserRecord.CreateRequest firebaseRequest = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password)
                    .setDisplayName(firstName + " " + lastName)
                    .setEmailVerified(true);

            UserRecord firebaseUser;
            try {
                firebaseUser = firebaseAuth.createUser(firebaseRequest);
                log.info("Firebase user created: {}", firebaseUser.getUid());
            } catch (FirebaseAuthException e) {
                log.error("Failed to create Firebase user", e);
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Failed to create Firebase account: " + e.getMessage()));
            }

            // Step 2: Validate and update room if provided
            if (roomId != null && !roomId.isEmpty()) {
                Optional<Room> roomOpt = roomRepository.findById(roomId);
                if (roomOpt.isEmpty()) {
                    // Rollback: delete Firebase user
                    try {
                        firebaseAuth.deleteUser(firebaseUser.getUid());
                    } catch (FirebaseAuthException e) {
                        log.warn("Failed to rollback Firebase user", e);
                    }
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Invalid room ID"));
                }

                Room room = roomOpt.get();
                if (!room.isAvailable()) {
                    // Rollback: delete Firebase user
                    try {
                        firebaseAuth.deleteUser(firebaseUser.getUid());
                    } catch (FirebaseAuthException e) {
                        log.warn("Failed to rollback Firebase user", e);
                    }
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Selected room is full"));
                }

                // Increment room occupancy
                room.incrementOccupancy();
                roomRepository.save(room);
            }

            // Parse account status
            AccountStatus accountStatus;
            try {
                accountStatus = AccountStatus.valueOf(accountStatusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                accountStatus = AccountStatus.PENDING;
            }

            // Step 3: Create student in MongoDB
            Student student = Student.builder()
                    .firebaseUid(firebaseUser.getUid())
                    .email(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .studentNumber(studentNumber)
                    .phoneNumber(phoneNumber)
                    .course(course)
                    .yearOfStudy(yearOfStudy)
                    .roomId(roomId)
                    .accountStatus(accountStatus)
                    .createdAt(LocalDateTime.now())
                    .isEnabled(true)
                    .build();

            Student savedStudent = studentRepository.save(student);

            log.info("Student created successfully: {} ({})", email, savedStudent.getId());

            // Return success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Student account created successfully");
            response.put("studentId", savedStudent.getId());
            response.put("email", savedStudent.getEmail());
            response.put("studentNumber", savedStudent.getStudentNumber());
            response.put("firebaseUid", firebaseUser.getUid());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Error creating student", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create student: " + e.getMessage()));
        }
    }

    /**
     * GET /api/admin/students - Get all students (admin only)
     */
    @GetMapping
    public ResponseEntity<?> getAllStudents(
            @AuthenticationPrincipal FirebaseUserDetails adminDetails) {

        try {
            if (!adminDetails.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin privileges required"));
            }

            List<Student> students = studentRepository.findAll();
            return ResponseEntity.ok(students);

        } catch (Exception e) {
            log.error("Error fetching students", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch students"));
        }
    }

    /**
     * GET /api/admin/students/{id} - Get student by ID (admin only)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(
            @AuthenticationPrincipal FirebaseUserDetails adminDetails,
            @PathVariable String id) {

        try {
            if (!adminDetails.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin privileges required"));
            }

            Optional<Student> studentOpt = studentRepository.findById(id);
            if (studentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Student not found"));
            }

            return ResponseEntity.ok(studentOpt.get());

        } catch (Exception e) {
            log.error("Error fetching student", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch student"));
        }
    }

    /**
     * PUT /api/admin/students/{id} - Update student (admin only)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(
            @AuthenticationPrincipal FirebaseUserDetails adminDetails,
            @PathVariable String id,
            @RequestBody Map<String, Object> updates) {

        try {
            if (!adminDetails.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin privileges required"));
            }

            Optional<Student> studentOpt = studentRepository.findById(id);
            if (studentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Student not found"));
            }

            Student student = studentOpt.get();

            // Update fields if provided
            if (updates.containsKey("firstName")) {
                student.setFirstName((String) updates.get("firstName"));
            }
            if (updates.containsKey("lastName")) {
                student.setLastName((String) updates.get("lastName"));
            }
            if (updates.containsKey("phoneNumber")) {
                student.setPhoneNumber((String) updates.get("phoneNumber"));
            }
            if (updates.containsKey("course")) {
                student.setCourse((String) updates.get("course"));
            }
            if (updates.containsKey("yearOfStudy")) {
                student.setYearOfStudy((Integer) updates.get("yearOfStudy"));
            }
            if (updates.containsKey("roomId")) {
                String newRoomId = (String) updates.get("roomId");

                // Handle room change
                if (student.getRoomId() != null && !student.getRoomId().equals(newRoomId)) {
                    // Decrement old room occupancy
                    roomRepository.findById(student.getRoomId()).ifPresent(oldRoom -> {
                        oldRoom.decrementOccupancy();
                        roomRepository.save(oldRoom);
                    });
                }

                if (newRoomId != null && !newRoomId.isEmpty()) {
                    // Increment new room occupancy
                    Optional<Room> newRoomOpt = roomRepository.findById(newRoomId);
                    if (newRoomOpt.isPresent()) {
                        Room newRoom = newRoomOpt.get();
                        if (newRoom.isAvailable()) {
                            newRoom.incrementOccupancy();
                            roomRepository.save(newRoom);
                            student.setRoomId(newRoomId);
                        } else {
                            return ResponseEntity.badRequest()
                                    .body(Map.of("error", "Selected room is full"));
                        }
                    }
                }
            }

            Student updatedStudent = studentRepository.save(student);
            return ResponseEntity.ok(updatedStudent);

        } catch (Exception e) {
            log.error("Error updating student", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update student"));
        }
    }

    /**
     * DELETE /api/admin/students/{id} - Delete student (admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(
            @AuthenticationPrincipal FirebaseUserDetails adminDetails,
            @PathVariable String id) {

        try {
            if (!adminDetails.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin privileges required"));
            }

            Optional<Student> studentOpt = studentRepository.findById(id);
            if (studentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Student not found"));
            }

            Student student = studentOpt.get();

            // Decrement room occupancy if student has a room
            if (student.getRoomId() != null) {
                roomRepository.findById(student.getRoomId()).ifPresent(room -> {
                    room.decrementOccupancy();
                    roomRepository.save(room);
                });
            }

            // Delete from Firebase
            try {
                firebaseAuth.deleteUser(student.getFirebaseUid());
            } catch (FirebaseAuthException e) {
                log.warn("Failed to delete Firebase user: {}", e.getMessage());
            }

            // Delete from MongoDB
            studentRepository.deleteById(id);

            return ResponseEntity.ok(Map.of("message", "Student deleted successfully"));

        } catch (Exception e) {
            log.error("Error deleting student", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete student"));
        }
    }

    /**
     * POST /api/admin/students/approve - Approve pending student (admin only)
     */
    @PostMapping("/approve")
    public ResponseEntity<?> approveStudent(
            @AuthenticationPrincipal FirebaseUserDetails adminDetails,
            @RequestBody Map<String, String> request) {

        try {
            if (!adminDetails.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin privileges required"));
            }

            String studentId = request.get("studentId");
            Optional<Student> studentOpt = studentRepository.findById(studentId);

            if (studentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Student not found"));
            }

            Student student = studentOpt.get();
            student.setAccountStatus(AccountStatus.ACTIVE);
            studentRepository.save(student);

            return ResponseEntity.ok(Map.of("message", "Student approved successfully"));

        } catch (Exception e) {
            log.error("Error approving student", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to approve student"));
        }
    }
}