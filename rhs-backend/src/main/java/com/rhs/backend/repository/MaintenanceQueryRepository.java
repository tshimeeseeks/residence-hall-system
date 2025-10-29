package com.rhs.backend.repository;

import com.rhs.backend.model.MaintenanceQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceQueryRepository extends MongoRepository<MaintenanceQuery, String> {

    /**
     * Find all maintenance queries by student ID
     */
    List<MaintenanceQuery> findByStudentId(String studentId);

    /**
     * Find all maintenance queries by room ID
     */
    List<MaintenanceQuery> findByRoomId(String roomId);

    /**
     * Find all maintenance queries by status
     */
    List<MaintenanceQuery> findByStatus(String status);

    /**
     * Find all maintenance queries assigned to a specific admin
     */
    List<MaintenanceQuery> findByAssignedToId(String assignedToId);

    /**
     * Find all maintenance queries by category
     */
    List<MaintenanceQuery> findByCategory(String category);

    /**
     * Find all maintenance queries by priority
     */
    List<MaintenanceQuery> findByPriority(String priority);
}