package com.rhs.backend.repository;

import com.rhs.backend.model.MaintenanceQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaintenanceRepository extends MongoRepository<MaintenanceQuery, String> {

    /**
     * Find all queries by room ID - THIS IS THE METHOD YOU NEED
     */
    List<MaintenanceQuery> findByRoomId(String roomId);

    /**
     * Find all queries by room ID with pagination
     */
    Page<MaintenanceQuery> findByRoomId(String roomId, Pageable pageable);

    /**
     * Find all queries by student ID
     */
    List<MaintenanceQuery> findByStudentId(String studentId);

    /**
     * Find all queries by student ID with pagination
     */
    Page<MaintenanceQuery> findByStudentId(String studentId, Pageable pageable);

    /**
     * Find all queries by status
     */
    List<MaintenanceQuery> findByStatus(String status);

    /**
     * Find all queries by status with pagination
     */
    Page<MaintenanceQuery> findByStatus(String status, Pageable pageable);

    /**
     * Find all queries by category
     */
    List<MaintenanceQuery> findByCategory(String category);

    /**
     * Find all queries by priority
     */
    List<MaintenanceQuery> findByPriority(String priority);

    /**
     * Find all queries assigned to a specific admin
     */
    List<MaintenanceQuery> findByAssignedToId(String assignedToId);

    /**
     * Find all queries assigned to a specific admin with pagination
     */
    Page<MaintenanceQuery> findByAssignedToId(String assignedToId, Pageable pageable);

    /**
     * Find pending queries
     */
    @Query("{ 'status': 'PENDING' }")
    List<MaintenanceQuery> findPendingQueries();

    /**
     * Find pending queries with pagination
     */
    @Query("{ 'status': 'PENDING' }")
    Page<MaintenanceQuery> findPendingQueries(Pageable pageable);

    /**
     * Find resolved queries
     */
    @Query("{ 'status': 'RESOLVED' }")
    List<MaintenanceQuery> findResolvedQueries();

    /**
     * Find in-progress queries
     */
    @Query("{ 'status': 'IN_PROGRESS' }")
    List<MaintenanceQuery> findInProgressQueries();

    /**
     * Find queries by student and status
     */
    List<MaintenanceQuery> findByStudentIdAndStatus(String studentId, String status);

    /**
     * Find queries by room and status
     */
    List<MaintenanceQuery> findByRoomIdAndStatus(String roomId, String status);

    /**
     * Find unassigned queries
     */
    @Query("{ 'assignedToId': { $exists: false } }")
    List<MaintenanceQuery> findUnassignedQueries();

    /**
     * Find queries created between dates
     */
    List<MaintenanceQuery> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Count queries by status
     */
    long countByStatus(String status);

    /**
     * Count queries by student ID
     */
    long countByStudentId(String studentId);

    /**
     * Count queries by room ID
     */
    long countByRoomId(String roomId);

    /**
     * Count queries by category
     */
    long countByCategory(String category);

    /**
     * Find all with pagination
     */
    Page<MaintenanceQuery> findAll(Pageable pageable);
}