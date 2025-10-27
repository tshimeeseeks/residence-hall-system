package com.rhs.backend.service;

import com.rhs.backend.model.SleepOverPass;
import com.rhs.backend.model.embedded.GuestDetails;
import com.rhs.backend.repository.SleepoverPassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SleepOverService {

    private final SleepoverPassRepository sleepoverPassRepository;

    /**
     * Create a new sleepover pass application
     */
    @Transactional
    public SleepOverPass createSleepOverPass(SleepOverPass sleepoverPass) {
        // Check if guest is blacklisted
        if (isGuestBlacklisted(sleepoverPass.getVisitor())) {
            throw new RuntimeException("Guest is blacklisted and cannot be approved for a sleepover pass");
        }

        // Check for date conflicts
        if (hasDateConflict(sleepoverPass)) {
            throw new RuntimeException("You already have an active sleepover pass for these dates");
        }

        // Save the sleepover pass
        sleepoverPass.setStatus("PENDING");
        SleepOverPass savedPass = sleepoverPassRepository.save(sleepoverPass);

        log.info("Sleepover pass created: id={}, student={}", savedPass.getId(), savedPass.getStudentId());
        return savedPass;
    }

    /**
     * Approve a sleepover pass
     */
    @Transactional
    public SleepOverPass approveSleepoverPass(String passId, String adminId) {
        SleepOverPass pass = sleepoverPassRepository.findById(passId)
                .orElseThrow(() -> new RuntimeException("Sleepover pass not found"));

        pass.approve(adminId);
        pass = sleepoverPassRepository.save(pass);

        log.info("Sleepover pass approved: id={}, admin={}", passId, adminId);
        return pass;
    }

    /**
     * Reject a sleepover pass
     */
    @Transactional
    public SleepOverPass rejectSleepoverPass(String passId, String adminId, String reason) {
        SleepOverPass pass = sleepoverPassRepository.findById(passId)
                .orElseThrow(() -> new RuntimeException("Sleepover pass not found"));

        pass.reject(adminId, reason);
        pass = sleepoverPassRepository.save(pass);

        log.info("Sleepover pass rejected: id={}, admin={}, reason={}", passId, adminId, reason);
        return pass;
    }

    /**
     * Get all sleepover passes for a specific student
     */
    @Transactional(readOnly = true)
    public List<SleepOverPass> getStudentSleepoverPasses(String studentId) {
        return sleepoverPassRepository.findByStudentId(studentId);
    }

    /**
     * Get all pending sleepover passes
     */
    @Transactional(readOnly = true)
    public List<SleepOverPass> getPendingSleepoverPasses() {
        return sleepoverPassRepository.findPendingPasses();
    }

    /**
     * Get all sleepover passes
     */
    @Transactional(readOnly = true)
    public List<SleepOverPass> getAllSleepoverPasses() {
        return sleepoverPassRepository.findAll();
    }

    /**
     * Get a specific sleepover pass by ID
     */
    @Transactional(readOnly = true)
    public SleepOverPass getSleepoverPassById(String passId) {
        return sleepoverPassRepository.findById(passId)
                .orElseThrow(() -> new RuntimeException("Sleepover pass not found"));
    }

    /**
     * Get all approved and active sleepover passes
     */
    @Transactional(readOnly = true)
    public List<SleepOverPass> getActiveSleepoverPasses() {
        return sleepoverPassRepository.findActivePasses(LocalDate.now());
    }

    /**
     * Check if there's a date conflict for the student
     */
    private boolean hasDateConflict(SleepOverPass newPass) {
        List<SleepOverPass> existingPasses = sleepoverPassRepository.findByStudentId(newPass.getStudentId());

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
    public void deleteSleepoverPass(String passId) {
        SleepOverPass pass = sleepoverPassRepository.findById(passId)
                .orElseThrow(() -> new RuntimeException("Sleepover pass not found"));

        sleepoverPassRepository.delete(pass);
        log.info("Sleepover pass deleted: id={}", passId);
    }
}