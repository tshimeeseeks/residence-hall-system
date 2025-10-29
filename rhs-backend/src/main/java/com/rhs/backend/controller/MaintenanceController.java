package com.rhs.backend.controller;

import com.rhs.backend.model.MaintenanceQuery;
import com.rhs.backend.model.Student;
import com.rhs.backend.repository.MaintenanceQueryRepository;
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
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MaintenanceController {

    private final MaintenanceQueryRepository maintenanceQueryRepository;
    private final StudentRepository studentRepository;

    /**
     * POST /api/maintenance - Create a new maintenance request (Students only)
     */
    @PostMapping
    public ResponseEntity<?> createMaintenanceQuery(
            @RequestBody Map<String, Object> queryData,
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {
        try {
            log.info("Creating maintenance query for user: {}", userDetails.getEmail());

            // Check if user is admin (admins can't create maintenance queries for
            // themselves)
            if (userDetails.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        Map.of("error", "Admins cannot create maintenance queries"));
            }

            // Get student information
            String firebaseUid = userDetails.getUid();
            Optional<Student> studentOpt = studentRepository.findByFirebaseUid(firebaseUid);

            if (studentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("error", "Student not found"));
            }

            Student student = studentOpt.get();

            // Extract data from request
            String queryTitle = (String) queryData.get("queryTitle");
            String queryDescription = (String) queryData.get("queryDescription");
            String category = (String) queryData.get("category");
            List<String> photoUrls = (List<String>) queryData.get("photoUrls");
            String priority = (String) queryData.getOrDefault("priority", "MEDIUM");

            // Validate required fields
            if (queryTitle == null || queryDescription == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Missing required fields: queryTitle and queryDescription are required"));
            }

            // Create MaintenanceQuery object
            // CRITICAL FIX: Use firebaseUid as studentId for consistency across queries
            MaintenanceQuery query = MaintenanceQuery.builder()
                    .studentId(firebaseUid) // ✓ FIXED: Using Firebase UID instead of MongoDB ID
                    .studentName(student.getFirstName() + " " + student.getLastName())
                    .studentEmail(student.getEmail())
                    .roomId(student.getRoomId())
                    .queryTitle(queryTitle)
                    .queryDescription(queryDescription)
                    .category(category)
                    .photoUrls(photoUrls)
                    .status("PENDING")
                    .priority(priority)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Save to database
            MaintenanceQuery savedQuery = maintenanceQueryRepository.save(query);

            log.info("Maintenance query created successfully: {} (studentId: {})",
                    savedQuery.getId(), firebaseUid);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Maintenance query created successfully",
                    "id", savedQuery.getId(),
                    "query", savedQuery));

        } catch (Exception e) {
            log.error("Error creating maintenance query", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Failed to create maintenance query: " + e.getMessage()));
        }
    }

    /**
     * GET /api/maintenance/my-queries - Get my maintenance queries (Students only)
     */
    @GetMapping("/my-queries")
    public ResponseEntity<?> getMyMaintenanceQueries(
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {
        try {
            log.info("Fetching maintenance queries for user: {}", userDetails.getEmail());

            String firebaseUid = userDetails.getUid();
            Optional<Student> studentOpt = studentRepository.findByFirebaseUid(firebaseUid);

            log.debug("Firebase UID: {}, Found student: {}", firebaseUid, studentOpt.isPresent());

            if (studentOpt.isEmpty()) {
                log.warn("Student not found for firebaseUid: {}, email: {}", firebaseUid, userDetails.getEmail());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of(
                                "error", "Student not found",
                                "firebaseUid", firebaseUid,
                                "email", userDetails.getEmail(),
                                "message", "Your student account may not be registered or approved yet"));
            }

            // CRITICAL FIX: Use firebaseUid directly as studentId
            String studentId = firebaseUid; // ✓ FIXED: Using Firebase UID consistently

            log.debug("Querying maintenance_queries with studentId: {}", studentId);
            List<MaintenanceQuery> queries = maintenanceQueryRepository.findByStudentId(studentId);

            log.info("Found {} maintenance queries for student {}", queries.size(), studentId);

            return ResponseEntity.ok(queries);

        } catch (Exception e) {
            log.error("Error fetching maintenance queries", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Failed to fetch maintenance queries"));
        }
    }

    /**
     * GET /api/maintenance - Get all maintenance queries (Admin only)
     */
    @GetMapping
    public ResponseEntity<?> getAllMaintenanceQueries(
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {
        try {
            if (!userDetails.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        Map.of("error", "Admin privileges required"));
            }

            List<MaintenanceQuery> queries = maintenanceQueryRepository.findAll();
            log.info("Admin {} fetched {} maintenance queries", userDetails.getEmail(), queries.size());
            return ResponseEntity.ok(queries);

        } catch (Exception e) {
            log.error("Error fetching all maintenance queries", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Failed to fetch maintenance queries"));
        }
    }

    /**
     * GET /api/maintenance/{id} - Get a specific maintenance query by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getMaintenanceQueryById(
            @PathVariable String id,
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {
        try {
            Optional<MaintenanceQuery> queryOpt = maintenanceQueryRepository.findById(id);

            if (queryOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("error", "Maintenance query not found"));
            }

            MaintenanceQuery query = queryOpt.get();

            // Check if user has permission to view this query
            boolean isAdmin = userDetails.isAdmin();
            boolean isOwner = false;

            if (!isAdmin) {
                // Check if current user owns this query using firebaseUid
                String firebaseUid = userDetails.getUid();
                isOwner = query.getStudentId().equals(firebaseUid);
            }

            if (!isAdmin && !isOwner) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        Map.of("error", "Access denied"));
            }

            return ResponseEntity.ok(query);

        } catch (Exception e) {
            log.error("Error fetching maintenance query", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("error", "Maintenance query not found"));
        }
    }

    /**
     * PUT /api/maintenance/{id}/status - Update query status (Admin only)
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateQueryStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> requestBody,
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {
        try {
            if (!userDetails.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        Map.of("error", "Admin privileges required"));
            }

            Optional<MaintenanceQuery> queryOpt = maintenanceQueryRepository.findById(id);

            if (queryOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("error", "Maintenance query not found"));
            }

            MaintenanceQuery query = queryOpt.get();
            String newStatus = requestBody.get("status");

            if (newStatus == null || newStatus.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Status is required"));
            }

            query.setStatus(newStatus);
            query.setUpdatedAt(LocalDateTime.now());

            MaintenanceQuery updatedQuery = maintenanceQueryRepository.save(query);

            log.info("Maintenance query {} status updated to {} by admin {}", id, newStatus, userDetails.getEmail());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Status updated successfully",
                    "query", updatedQuery));

        } catch (Exception e) {
            log.error("Error updating maintenance query status", e);
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/maintenance/{id}/assign - Assign query to an admin (Admin only)
     */
    @PutMapping("/{id}/assign")
    public ResponseEntity<?> assignQuery(
            @PathVariable String id,
            @RequestBody Map<String, String> requestBody,
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {
        try {
            if (!userDetails.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        Map.of("error", "Admin privileges required"));
            }

            Optional<MaintenanceQuery> queryOpt = maintenanceQueryRepository.findById(id);

            if (queryOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("error", "Maintenance query not found"));
            }

            MaintenanceQuery query = queryOpt.get();
            String assignedToId = requestBody.get("assignedToId");
            String assignedToName = requestBody.get("assignedToName");

            query.setAssignedToId(assignedToId);
            query.setAssignedToName(assignedToName);
            query.setStatus("IN_PROGRESS");
            query.setUpdatedAt(LocalDateTime.now());

            MaintenanceQuery updatedQuery = maintenanceQueryRepository.save(query);

            log.info("Maintenance query {} assigned to {} by admin {}", id, assignedToName, userDetails.getEmail());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Query assigned successfully",
                    "query", updatedQuery));

        } catch (Exception e) {
            log.error("Error assigning maintenance query", e);
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/maintenance/{id}/resolve - Mark query as resolved (Admin only)
     */
    @PutMapping("/{id}/resolve")
    public ResponseEntity<?> resolveQuery(
            @PathVariable String id,
            @RequestBody Map<String, String> requestBody,
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {
        try {
            if (!userDetails.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        Map.of("error", "Admin privileges required"));
            }

            Optional<MaintenanceQuery> queryOpt = maintenanceQueryRepository.findById(id);

            if (queryOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("error", "Maintenance query not found"));
            }

            MaintenanceQuery query = queryOpt.get();
            String resolutionNotes = requestBody.get("resolutionNotes");

            query.setStatus("RESOLVED");
            query.setResolutionNotes(resolutionNotes);
            query.setResolvedAt(LocalDateTime.now());
            query.setUpdatedAt(LocalDateTime.now());

            MaintenanceQuery updatedQuery = maintenanceQueryRepository.save(query);

            log.info("Maintenance query {} resolved by admin {}", id, userDetails.getEmail());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Query resolved successfully",
                    "query", updatedQuery));

        } catch (Exception e) {
            log.error("Error resolving maintenance query", e);
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/maintenance/{id} - Delete a maintenance query (Admin only or
     * Student who owns it)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMaintenanceQuery(
            @PathVariable String id,
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {
        try {
            Optional<MaintenanceQuery> queryOpt = maintenanceQueryRepository.findById(id);

            if (queryOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("error", "Maintenance query not found"));
            }

            MaintenanceQuery query = queryOpt.get();

            // Check if user has permission to delete
            boolean isAdmin = userDetails.isAdmin();
            boolean isOwner = false;

            if (!isAdmin) {
                // Check ownership using firebaseUid
                String firebaseUid = userDetails.getUid();
                isOwner = query.getStudentId().equals(firebaseUid);
            }

            if (!isAdmin && !isOwner) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        Map.of("error", "Access denied"));
            }

            maintenanceQueryRepository.deleteById(id);

            log.info("Maintenance query {} deleted by user {}", id, userDetails.getEmail());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Maintenance query deleted successfully"));

        } catch (Exception e) {
            log.error("Error deleting maintenance query", e);
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage()));
        }
    }
}