package com.rhs.backend.repository;

import com.rhs.backend.model.SleepOverPass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SleepoverPassRepository extends MongoRepository<SleepOverPass, String> {

    /**
     * Find all passes by student ID
     */
    List<SleepOverPass> findByStudentId(String studentId);

    /**
     * Find passes by student ID with pagination
     */
    Page<SleepOverPass> findByStudentId(String studentId, Pageable pageable);

    /**
     * Find passes by status
     */
    List<SleepOverPass> findByStatus(String status);

    /**
     * Find passes by status with pagination
     */
    Page<SleepOverPass> findByStatus(String status, Pageable pageable);

    /**
     * Find pending passes
     */
    @Query("{ 'status': 'PENDING' }")
    List<SleepOverPass> findPendingPasses();

    /**
     * Find approved passes
     */
    @Query("{ 'status': 'APPROVED' }")
    List<SleepOverPass> findApprovedPasses();

    /**
     * Find active passes (approved and within date range)
     */
    @Query("{ 'status': 'APPROVED', 'startDate': { $lte: ?0 }, 'endDate': { $gte: ?0 } }")
    List<SleepOverPass> findActivePasses(LocalDate currentDate);

    /**
     * Find expired passes
     */
    @Query("{ 'endDate': { $lt: ?0 } }")
    List<SleepOverPass> findExpiredPasses(LocalDate currentDate);

    /**
     * Find passes by date range
     */
    @Query("{ 'startDate': { $gte: ?0 }, 'endDate': { $lte: ?1 } }")
    List<SleepOverPass> findByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Count passes by status
     */
    long countByStatus(String status);

    /**
     * Count passes by student ID
     */
    long countByStudentId(String studentId);

    /**
     * Find passes approved by specific admin
     */
    List<SleepOverPass> findByApprovedBy(String adminId);
}