package com.rhs.backend.service;

import com.rhs.backend.model.SleepOverPass;
import com.rhs.backend.model.embedded.GuestDetails;
import com.rhs.backend.repository.SleepOverPassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SleepOverService {

    private final SleepOverPassRepository sleepOverPassRepository;

    /**
     * Create a new sleepover pass application
     */
    @Transactional
    public SleepOverPass createSleepOverPass(SleepOverPass sleepOverPass) {
        // Check if guest is blacklisted
        if (isGuestBlacklisted(sleepOverPass.getVisitor())) {
            throw new RuntimeException("Guest is blacklisted and cannot be approved for a sleepover pass");
        }

        // Check for date conflicts
        if (hasDateConflict(sleepOverPass)) {
            throw new RuntimeException("You already have an active sleepover pass for these dates");
        }

        // Save the sleepover pass
        sleepOverPass.setStatus("PENDING");
        SleepOverPass savedPass = sleepOverPassRepository.save(sleepOverPass);

        log.info("Sleepover pass created: id={}, student={}", savedPass.getId(), savedPass.getStudentId());
        return savedPass;
    }

    /**
     * Approve a sleepover pass
     */
    @Transactional
    public SleepOverPass approveSleepOverPass(String passId, String adminId) {
        SleepOverPass pass = sleepOverPassRepository.findById(passId)
                .orElseThrow(() -> new RuntimeException("Sleepover pass not found"));

        pass.approve(adminId);
        pass = sleepOverPassRepository.save(pass);

        log.info("Sleepover pass approved: id={}, admin={}", passId, adminId);
        return pass;
    }

    /**
     * Reject a sleepover pass
     */
    @Transactional
    public SleepOverPass rejectSleepOverPass(String passId, String adminId, String reason) {
        SleepOverPass pass = sleepOverPassRepository.findById(passId)
                .orElseThrow(() -> new RuntimeException("Sleepover pass not found"));

        pass.reject(adminId, reason);
        pass = sleepOverPassRepository.save(pass);

        log.info("Sleepover pass rejected: id={}, admin={}, reason={}", passId, adminId, reason);
        return pass;
    }

    /**
     * Get all sleepover passes for a specific student
     */
    @Transactional(readOnly = true)
    public List<SleepOverPass> getStudentSleepOverPasses(String studentId) {
        return sleepOverPassRepository.findByStudentId(studentId);
    }

    /**
     * Get all pending sleepover passes
     */
    @Transactional(readOnly = true)
    public List<SleepOverPass> getPendingSleepOverPasses() {
        return sleepOverPassRepository.findPendingPasses();
    }

    /**
     * Get all sleepover passes
     */
    @Transactional(readOnly = true)
    public List<SleepOverPass> getAllSleepOverPasses() {
        return sleepOverPassRepository.findAll();
    }

    /**
     * Get a specific sleepover pass by ID
     */
    @Transactional(readOnly = true)
    public SleepOverPass getSleepOverPassById(String passId) {
        return sleepOverPassRepository.findById(passId)
                .orElseThrow(() -> new RuntimeException("Sleepover pass not found"));
    }

    /**
     * Get all approved and active sleepover passes
     */
    @Transactional(readOnly = true)
    public List<SleepOverPass> getActiveSleepOverPasses() {
        return sleepOverPassRepository.findActivePasses(LocalDate.now());
    }

    /**
     * Check if there's a date conflict for the student
     */
    private boolean hasDateConflict(SleepOverPass newPass) {
        List<SleepOverPass> existingPasses = sleepOverPassRepository.findByStudentId(newPass.getStudentId());

        for (SleepOverPass existingPass : existingPasses) {
            // Skip rejected passes
            if ("REJECTED".equals(existingPass.getStatus())) {
                continue;
            }

            // Check for date overlap
            boolean datesOverlap = !newPass.getStartDate().isAfter(existingPass.getEndDate()) &&
                    !newPass.getEndDate().isBefore(existingPass.getStartDate());

            if (datesOverlap) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if guest is blacklisted
     * In a real application, you would check against a blacklist collection
     */
    private boolean isGuestBlacklisted(GuestDetails visitor) {
        if (visitor == null || visitor.getGuestIdNumber() == null) {
            return false;
        }

        // TODO: Implement actual blacklist checking
        // For now, just a placeholder check
        return "BLACKLISTED".equals(visitor.getGuestIdNumber());
    }

    /**
     * Delete a sleepover pass
     */
    @Transactional
    public void deleteSleepOverPass(String passId) {
        SleepOverPass pass = sleepOverPassRepository.findById(passId)
                .orElseThrow(() -> new RuntimeException("Sleepover pass not found"));

        sleepOverPassRepository.delete(pass);
        log.info("Sleepover pass deleted: id={}", passId);
    }

    @Transactional
    public SleepOverPass updateSleepOverPass(SleepOverPass sleepOverPass) {
        // Verify the pass exists
        SleepOverPass existingPass = sleepOverPassRepository.findById(sleepOverPass.getId())
                .orElseThrow(() -> new RuntimeException("Sleepover pass not found"));

        // Check if guest is blacklisted with new details
        if (isGuestBlacklisted(sleepOverPass.getVisitor())) {
            throw new RuntimeException("Guest is blacklisted and cannot be approved for a sleepover pass");
        }

        // Check for date conflicts (excluding this pass itself)
        if (hasDateConflictExcluding(sleepOverPass, sleepOverPass.getId())) {
            throw new RuntimeException("Date conflict with another existing sleepover pass");
        }

        // Update the timestamp
        sleepOverPass.setUpdatedAt(LocalDateTime.now());

        // Save the updated pass
        SleepOverPass updatedPass = sleepOverPassRepository.save(sleepOverPass);

        log.info("Sleepover pass updated: id={}", updatedPass.getId());
        return updatedPass;
    }

    private boolean hasDateConflictExcluding(SleepOverPass newPass, String excludePassId) {
        List<SleepOverPass> existingPasses = sleepOverPassRepository.findByStudentId(newPass.getStudentId());

        for (SleepOverPass existingPass : existingPasses) {
            // Skip the pass being updated
            if (existingPass.getId().equals(excludePassId)) {
                continue;
            }

            // Skip rejected passes
            if ("REJECTED".equals(existingPass.getStatus())) {
                continue;
            }

            // Check for date overlap
            boolean datesOverlap = !newPass.getStartDate().isAfter(existingPass.getEndDate()) &&
                    !newPass.getEndDate().isBefore(existingPass.getStartDate());

            if (datesOverlap) {
                return true;
            }
        }

        return false;

    }
}