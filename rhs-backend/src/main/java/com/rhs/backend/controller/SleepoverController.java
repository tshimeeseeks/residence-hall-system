package com.rhs.backend.controller;

import com.rhs.backend.model.SleepOverPass;
import com.rhs.backend.security.FirebaseUserDetails;
import com.rhs.backend.service.SleepOverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/sleepover-passes")
@RequiredArgsConstructor
public class SleepOverController {

    private final SleepOverService sleepOverService;

    /**
     * Create a new sleepover pass application (Students only)
     */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> createSleepOverPass(
            @RequestBody SleepOverPass sleepOverPass,
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {
        try {
            // Set the student ID from authenticated user
            sleepOverPass.setStudentId(userDetails.getUid());

            SleepOverPass createdPass = sleepOverService.createSleepOverPass(sleepOverPass);
            return ResponseEntity.ok(createdPass);

        } catch (Exception e) {
            log.error("Error creating sleepover pass", e);
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get my sleepover passes (Students only)
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<SleepOverPass>> getMySleepoverPasses(
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {

        String studentId = userDetails.getUid();
        List<SleepOverPass> passes = sleepOverService.getStudentSleepOverPasses(studentId);
        return ResponseEntity.ok(passes);
    }

    /**
     * Get all sleepover passes (Admin only)
     */
    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<SleepOverPass>> getAllSleepOverPasses() {
        List<SleepOverPass> passes = sleepOverService.getAllSleepOverPasses();
        return ResponseEntity.ok(passes);
    }

    /**
     * Get all pending sleepover passes (Admin only)
     */
    @GetMapping("/admin/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<SleepOverPass>> getPendingSleepOverPasses() {
        List<SleepOverPass> passes = sleepOverService.getPendingSleepOverPasses();
        return ResponseEntity.ok(passes);
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
            return ResponseEntity.status(404).body(
                    Map.of("error", "Sleepover pass not found"));
        }
    }

    /**
     * Approve a sleepover pass (Admin only)
     */
    @PutMapping("/{applicationId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> approveSleepOverPass(
            @PathVariable String applicationId,
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {
        try {
            String adminId = userDetails.getUid();
            SleepOverPass approvedPass = sleepOverService.approveSleepOverPass(applicationId, adminId);
            return ResponseEntity.ok(approvedPass);

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
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> rejectSleeOverPass(
            @PathVariable String applicationId,
            @RequestParam String reason,
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {
        try {
            String adminId = userDetails.getUid();
            SleepOverPass rejectedPass = sleepOverService.rejectSleepOverPass(
                    applicationId, adminId, reason);
            return ResponseEntity.ok(rejectedPass);

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
    public ResponseEntity<?> deleteSleepoverPass(
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

            sleepOverService.deleteSleepoverPass(applicationId);
            return ResponseEntity.ok(Map.of("message", "Sleepover pass deleted successfully"));

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
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<SleepOverPass>> getActiveSleepOverPasses() {
        List<SleepOverPass> passes = sleepOverService.getActiveSleepOverPasses();
        return ResponseEntity.ok(passes);
    }
}