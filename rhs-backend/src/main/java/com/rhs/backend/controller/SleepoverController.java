package com.rhs.backend.controller;

import com.rhs.backend.model.SleepOverPass;
import com.rhs.backend.model.embedded.GuestDetails;
import com.rhs.backend.security.FirebaseUserDetails;
import com.rhs.backend.service.SleepOverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/sleepover-passes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SleepOverController {

    private final SleepOverService sleepOverService;

    /**
     * Create a new sleepover pass application (Students only)
     */
    @PostMapping
    public ResponseEntity<?> createSleepOverPass(
            @RequestBody Map<String, Object> sleepOverData,
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {
        try {
            log.info("Creating sleepover pass for user: {}", userDetails.getEmail());

            // Check if user is admin (admins can't create sleepover passes for themselves)
            if (userDetails.isAdmin()) {
                return ResponseEntity.status(403).body(
                        Map.of("error", "Admins cannot create sleepover passes"));
            }

            // Extract data from request
            String guestName = (String) sleepOverData.get("guestName");
            String guestIdNumber = (String) sleepOverData.get("guestIdNumber");
            String guestPhoneNumber = (String) sleepOverData.get("guestPhoneNumber");
            String guestEmail = (String) sleepOverData.get("guestEmail");
            String relationship = (String) sleepOverData.get("relationship");
            String checkInDateTime = (String) sleepOverData.get("checkInDateTime");
            String checkOutDateTime = (String) sleepOverData.get("checkOutDateTime");
            String reason = (String) sleepOverData.get("reason");
            String guestIdDocumentUrl = (String) sleepOverData.get("guestIdDocumentUrl");

            // Validate required fields
            if (guestName == null || guestIdNumber == null || guestPhoneNumber == null ||
                    relationship == null || checkInDateTime == null || checkOutDateTime == null || reason == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Missing required fields"));
            }

            // Parse dates from ISO format strings (e.g., "2025-10-30T12:57:00")
            LocalDate startDate = LocalDate.parse(checkInDateTime.substring(0, 10));
            LocalDate endDate = LocalDate.parse(checkOutDateTime.substring(0, 10));

            // Create GuestDetails object
            GuestDetails guestDetails = GuestDetails.builder()
                    .guestName(guestName)
                    .guestIdNumber(guestIdNumber)
                    .guestPhone(guestPhoneNumber)
                    .idDocument(guestIdDocumentUrl)
                    .build();

            // Create SleepOverPass object using the correct model structure
            SleepOverPass sleepOverPass = SleepOverPass.builder()
                    .studentId(userDetails.getUid())
                    .visitor(guestDetails)
                    .startDate(startDate)
                    .endDate(endDate)
                    .status("PENDING")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Save via service
            SleepOverPass createdPass = sleepOverService.createSleepOverPass(sleepOverPass);

            log.info("Sleepover pass created successfully: {}", createdPass.getId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Sleepover pass created successfully",
                    "id", createdPass.getId(),
                    "pass", createdPass));

        } catch (Exception e) {
            log.error("Error creating sleepover pass", e);
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Failed to create sleepover pass: " + e.getMessage()));
        }
    }

    /**
     * Get my sleepover passes (Students only)
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMySleepoverPasses(
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {
        try {
            log.info("Fetching sleepover passes for user: {}", userDetails.getEmail());

            String studentId = userDetails.getUid();
            List<SleepOverPass> passes = sleepOverService.getStudentSleepOverPasses(studentId);

            return ResponseEntity.ok(passes);

        } catch (Exception e) {
            log.error("Error fetching sleepover passes", e);
            return ResponseEntity.status(500).body(
                    Map.of("error", "Failed to fetch sleepover passes"));
        }
    }

    /**
     * Get all sleepover passes (Admin only)
     */
    @GetMapping("/admin")
    public ResponseEntity<?> getAllSleepOverPasses(
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {
        try {
            if (!userDetails.isAdmin()) {
                return ResponseEntity.status(403).body(
                        Map.of("error", "Admin privileges required"));
            }

            List<SleepOverPass> passes = sleepOverService.getAllSleepOverPasses();
            return ResponseEntity.ok(passes);

        } catch (Exception e) {
            log.error("Error fetching all sleepover passes", e);
            return ResponseEntity.status(500).body(
                    Map.of("error", "Failed to fetch sleepover passes"));
        }
    }

    /**
     * Get all pending sleepover passes (Admin only)
     */
    @GetMapping("/admin/pending")
    public ResponseEntity<?> getPendingSleepOverPasses(
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {
        try {
            if (!userDetails.isAdmin()) {
                return ResponseEntity.status(403).body(
                        Map.of("error", "Admin privileges required"));
            }

            List<SleepOverPass> passes = sleepOverService.getPendingSleepOverPasses();
            return ResponseEntity.ok(passes);

        } catch (Exception e) {
            log.error("Error fetching pending sleepover passes", e);
            return ResponseEntity.status(500).body(
                    Map.of("error", "Failed to fetch pending passes"));
        }
    }

    /**
     * Get a specific sleepover pass by ID
     */
    @GetMapping("/{applicationId}")
    public ResponseEntity<?> getSleepOverPassById(
            @PathVariable String applicationId,
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {
        try {
            SleepOverPass pass = sleepOverService.getSleepOverPassById(applicationId);

            // Check if user has permission to view this pass
            boolean isAdmin = userDetails.isAdmin();
            boolean isOwner = pass.getStudentId().equals(userDetails.getUid());
            if (!isAdmin && !isOwner) {
                return ResponseEntity.status(403).body(
                        Map.of("error", "Access denied"));
            }

            return ResponseEntity.ok(pass);

        } catch (Exception e) {
            log.error("Error fetching sleepover pass", e);
            return ResponseEntity.status(404).body(
                    Map.of("error", "Sleepover pass not found"));
        }
    }

    /**
     * Approve a sleepover pass (Admin only)
     */
    @PutMapping("/{applicationId}/approve")
    public ResponseEntity<?> approveSleepOverPass(
            @PathVariable String applicationId,
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {
        try {
            if (!userDetails.isAdmin()) {
                return ResponseEntity.status(403).body(
                        Map.of("error", "Admin privileges required"));
            }

            String adminId = userDetails.getUid();
            SleepOverPass approvedPass = sleepOverService.approveSleepOverPass(applicationId, adminId);

            log.info("Sleepover pass {} approved by admin {}", applicationId, adminId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Sleepover pass approved successfully",
                    "pass", approvedPass));

        } catch (Exception e) {
            log.error("Error approving sleepover pass", e);
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage()));
        }
    }

    /**
     * Reject a sleepover pass (Admin only)
     */
    @PutMapping("/{applicationId}/reject")
    public ResponseEntity<?> rejectSleepOverPass(
            @PathVariable String applicationId,
            @RequestBody Map<String, String> requestBody,
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {
        try {
            if (!userDetails.isAdmin()) {
                return ResponseEntity.status(403).body(
                        Map.of("error", "Admin privileges required"));
            }

            String reason = requestBody.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Rejection reason is required"));
            }

            String adminId = userDetails.getUid();
            SleepOverPass rejectedPass = sleepOverService.rejectSleepOverPass(
                    applicationId, adminId, reason);

            log.info("Sleepover pass {} rejected by admin {}", applicationId, adminId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Sleepover pass rejected",
                    "pass", rejectedPass));

        } catch (Exception e) {
            log.error("Error rejecting sleepover pass", e);
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage()));
        }
    }

    /**
     * Delete a sleepover pass (Admin only or Student who owns it)
     */
    @DeleteMapping("/{applicationId}")
    public ResponseEntity<?> deleteSleepOverPass(
            @PathVariable String applicationId,
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {
        try {
            SleepOverPass pass = sleepOverService.getSleepOverPassById(applicationId);

            // Check if user has permission to delete
            boolean isAdmin = userDetails.isAdmin();
            boolean isOwner = pass.getStudentId().equals(userDetails.getUid());

            if (!isAdmin && !isOwner) {
                return ResponseEntity.status(403).body(
                        Map.of("error", "Access denied"));
            }

            sleepOverService.deleteSleepOverPass(applicationId);

            log.info("Sleepover pass {} deleted by user {}", applicationId, userDetails.getEmail());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Sleepover pass deleted successfully"));

        } catch (Exception e) {
            log.error("Error deleting sleepover pass", e);
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get active sleepover passes (Admin only)
     */
    @GetMapping("/admin/active")
    public ResponseEntity<?> getActiveSleepOverPasses(
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {
        try {
            if (!userDetails.isAdmin()) {
                return ResponseEntity.status(403).body(
                        Map.of("error", "Admin privileges required"));
            }

            List<SleepOverPass> passes = sleepOverService.getActiveSleepOverPasses();
            return ResponseEntity.ok(passes);

        } catch (Exception e) {
            log.error("Error fetching active sleepover passes", e);
            return ResponseEntity.status(500).body(
                    Map.of("error", "Failed to fetch active passes"));
        }
    }

    /**
     * Update a sleepover pass (Admin only or Student who owns it - before approval)
     */
    @PutMapping("/{applicationId}")
    public ResponseEntity<?> updateSleepOverPass(
            @PathVariable String applicationId,
            @RequestBody Map<String, Object> updates,
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {
        try {
            SleepOverPass existingPass = sleepOverService.getSleepOverPassById(applicationId);

            // Check if user has permission to update
            boolean isAdmin = userDetails.isAdmin();
            boolean isOwner = existingPass.getStudentId().equals(userDetails.getUid());

            if (!isAdmin && !isOwner) {
                return ResponseEntity.status(403).body(
                        Map.of("error", "Access denied"));
            }

            // Students can only update pending passes
            if (!isAdmin && !"PENDING".equals(existingPass.getStatus())) {
                return ResponseEntity.status(403).body(
                        Map.of("error", "Cannot update non-pending pass"));
            }

            // Update fields if provided
            if (updates.containsKey("guestName") || updates.containsKey("guestIdNumber") ||
                    updates.containsKey("guestPhoneNumber") || updates.containsKey("guestIdDocumentUrl")) {

                GuestDetails currentGuest = existingPass.getVisitor();
                GuestDetails updatedGuest = GuestDetails.builder()
                        .guestName((String) updates.getOrDefault("guestName", currentGuest.getGuestName()))
                        .guestIdNumber((String) updates.getOrDefault("guestIdNumber", currentGuest.getGuestIdNumber()))
                        .guestPhone((String) updates.getOrDefault("guestPhoneNumber", currentGuest.getGuestPhone()))
                        .idDocument((String) updates.getOrDefault("guestIdDocumentUrl", currentGuest.getIdDocument()))
                        .build();

                existingPass.setVisitor(updatedGuest);
            }

            if (updates.containsKey("checkInDateTime")) {
                String checkIn = (String) updates.get("checkInDateTime");
                existingPass.setStartDate(LocalDate.parse(checkIn.substring(0, 10)));
            }

            if (updates.containsKey("checkOutDateTime")) {
                String checkOut = (String) updates.get("checkOutDateTime");
                existingPass.setEndDate(LocalDate.parse(checkOut.substring(0, 10)));
            }

            existingPass.setUpdatedAt(LocalDateTime.now());

            SleepOverPass updatedPass = sleepOverService.updateSleepOverPass(existingPass);

            log.info("Sleepover pass {} updated by user {}", applicationId, userDetails.getEmail());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Sleepover pass updated successfully",
                    "pass", updatedPass));

        } catch (Exception e) {
            log.error("Error updating sleepover pass", e);
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage()));
        }
    }
}