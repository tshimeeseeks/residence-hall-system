package com.rhs.backend.controller;

import com.rhs.backend.dto.MaintenanceQueryDTO;
import com.rhs.backend.model.Admin;
import com.rhs.backend.model.enums.QueryStatus;
import com.rhs.backend.repository.AdminRepository;
import com.rhs.backend.repository.MaintenanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public MaintenanceService(MaintenanceRepository maintenanceRepository, AdminRepository adminRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.adminRepository = adminRepository;
    }

    /**
     * Create a new maintenance query.
     */
    public MaintenanceQueryDTO createQuery(MaintenanceQueryDTO query) {
        query.setStatus(QueryStatus.PENDING);
        query.setCreatedAt(LocalDateTime.now());
        query.setUpdatedAt(LocalDateTime.now());
        return maintenanceRepository.save(query);
    }

    /**
     * Update query status.
     */
    public MaintenanceQueryDTO updateStatus(String id, QueryStatus status) {
        MaintenanceQueryDTO query = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance query not found with ID: " + id));

        query.setStatus(status);
        query.setUpdatedAt(LocalDateTime.now());
        if (status == QueryStatus.RESOLVED) {
            query.setResolvedAt(LocalDateTime.now());
        }

        return maintenanceRepository.save(query);
    }

    /**
     * Assign a maintenance query to an admin.
     */
    public MaintenanceQueryDTO assignTo(String queryId, String adminId) {
        MaintenanceQueryDTO query = maintenanceRepository.findById(queryId)
                .orElseThrow(() -> new RuntimeException("Maintenance query not found with ID: " + queryId));

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + adminId));

        query.setAssignedTo(admin);
        query.setStatus(QueryStatus.IN_PROGRESS);
        query.setUpdatedAt(LocalDateTime.now());

        return maintenanceRepository.save(query);
    }

    /**
     * Search queries by optional filters: priority, status, date range.
     */
    public List<MaintenanceQueryDTO> searchQueries(String priority, QueryStatus status, LocalDateTime start,
            LocalDateTime end) {
        List<MaintenanceQueryDTO> all = maintenanceRepository.findAll();

        return all.stream()
                .filter(q -> priority == null
                        || (q.getPriority() != null && q.getPriority().equalsIgnoreCase(priority)))
                .filter(q -> status == null || q.getStatus() == status)
                .filter(q -> start == null || (q.getCreatedAt() != null && !q.getCreatedAt().isBefore(start)))
                .filter(q -> end == null || (q.getCreatedAt() != null && !q.getCreatedAt().isAfter(end)))
                .collect(Collectors.toList());
    }

    /**
     * Retrieve all maintenance queries.
     */
    public List<MaintenanceQueryDTO> getAllQueries() {
        return maintenanceRepository.findAll();
    }

    /**
     * Get overall statistics for maintenance queries.
     */
    public Map<String, Object> getStats() {
        List<MaintenanceQueryDTO> all = maintenanceRepository.findAll();

        long total = all.size();
        long pending = all.stream().filter(q -> q.getStatus() == QueryStatus.PENDING).count();
        long inProgress = all.stream().filter(q -> q.getStatus() == QueryStatus.IN_PROGRESS).count();
        long resolved = all.stream().filter(q -> q.getStatus() == QueryStatus.RESOLVED).count();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("Total Queries", total);
        stats.put("Pending", pending);
        stats.put("In Progress", inProgress);
        stats.put("Resolved", resolved);
        stats.put("Last Updated", LocalDateTime.now());

        return stats;
    }

    /**
     * Mark a query as resolved and add resolution notes.
     */
    public MaintenanceQueryDTO resolveQuery(String queryId, String resolutionNotes) {
        MaintenanceQueryDTO query = maintenanceRepository.findById(queryId)
                .orElseThrow(() -> new RuntimeException("Maintenance query not found with ID: " + queryId));

        query.setStatus(QueryStatus.RESOLVED);
        query.setResolutionNotes(resolutionNotes);
        query.setResolvedAt(LocalDateTime.now());
        query.setUpdatedAt(LocalDateTime.now());

        return maintenanceRepository.save(query);
    }
}
