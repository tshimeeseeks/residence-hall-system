package com.rhs.backend.service;

import com.rhs.backend.dto.MaintenanceQueryDTO;
import com.rhs.backend.model.Admin;
import com.rhs.backend.model.enums.QueryStatus;
import com.rhs.backend.repository.AdminRepository;
import com.rhs.backend.repository.MaintenanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
     * Mark a maintenance query as resolved.
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

    /**
     * Assign a maintenance query to an admin.
     */
    public MaintenanceQueryDTO assignQuery(String queryId, String adminId) {
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
     * Retrieve all maintenance queries.
     */
    public List<MaintenanceQueryDTO> getAllQueries() {
        return maintenanceRepository.findAll();
    }

    /**
     * Retrieve only pending queries.
     */
    public List<MaintenanceQueryDTO> getPendingQueries() {
        return maintenanceRepository.findByStatus(QueryStatus.PENDING);
    }

    /**
     * Retrieve only resolved queries.
     */
    public List<MaintenanceQueryDTO> getResolvedQueries() {
        return maintenanceRepository.findByStatus(QueryStatus.RESOLVED);
    }
}
