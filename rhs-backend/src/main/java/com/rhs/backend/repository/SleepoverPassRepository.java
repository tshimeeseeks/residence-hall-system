package com.rhs.backend.repository;

import com.rhs.backend.model.SleepOverPass;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SleepOverPassRepository extends MongoRepository<SleepOverPass, String> {

    List<SleepOverPass> findByStudentId(String studentId);

    Optional<SleepOverPass> findByIdAndStudentId(String id, String studentId);

    List<SleepOverPass> findByStatus(String status);

    long countByStudentIdAndStatus(String studentId, String status);

    // These two methods are needed:
    @Query("{ 'status': 'PENDING' }")
    List<SleepOverPass> findPendingPasses();

    @Query("{ 'status': 'APPROVED', 'sleepoverDate': ?0 }")
    List<SleepOverPass> findActivePasses(LocalDate date);
}