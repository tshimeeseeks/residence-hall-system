package com.rhs.backend.repository;

import com.rhs.backend.model.MaintenanceQuery;
import com.rhs.backend.model.Student;
import com.rhs.backend.model.Admin;
import com.rhs.backend.model.enums.QueryStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for MaintenanceQuery operations
 */
@Repository
public interface MaintenanceQueryRepository extends MongoRepository<MaintenanceQuery, String> {

    /**
     * Find all queries by student
     * 
     * @param student the student
     * @return list of maintenance queries
     */
    List<MaintenanceQuery> findByStudent(Student student);

    /**
     * Find all queries by room ID
     * 
     * @param roomId the room ID
     * @return list of maintenance queries
     */
    List<MaintenanceQuery> findByRoomId(String roomId);

    /**
     * Find all queries by status
     * 
     * @param status the query status
     * @return list of maintenance queries
     */
    List<MaintenanceQuery> findByStatus(QueryStatus status);

    /**
     * Find all queries assigned to an admin
     * 
     * @param admin the admin
     * @return list of maintenance queries
     */
    List<MaintenanceQuery> findByAssignedTo(Admin admin);

    /**
     * Find queries created between dates
     * 
     * @param startDate start date
     * @param endDate   end date
     * @return list of maintenance queries
     */
    List<MaintenanceQuery> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find queries resolved between dates
     * 
     * @param startDate start date
     * @param endDate   end date
     * @return list of maintenance queries
     */
    List<MaintenanceQuery> findByResolvedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find queries by priority
     * 
     * @param priority the priority level
     * @return list of maintenance queries
     */
    List<MaintenanceQuery> findByPriority(String priority);

    /**
     * Find queries by student and status
     * 
     * @param student the student
     * @param status  the query status
     * @return list of maintenance queries
     */
    List<MaintenanceQuery> findByStudentAndStatus(Student student, QueryStatus status);

    /**
     * Find queries by room ID and status
     * 
     * @param roomId the room ID
     * @param status the query status
     * @return list of maintenance queries
     */
    List<MaintenanceQuery> findByRoomIdAndStatus(String roomId, QueryStatus status);

    /**
     * Count queries by status
     * 
     * @param status the query status
     * @return count of queries
     */
    long countByStatus(QueryStatus status);

    /**
     * Count queries by student
     * 
     * @param student the student
     * @return count of queries
     */
    long countByStudent(Student student);
}