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

    List<SleepOverPass> findByStatus(String status);

    List<SleepOverPass> findByRoomId(String roomId);

    Optional<SleepOverPass> findByIdAndStudentId(String id, String studentId);

    // Find passes with pending status
    @Query("{ 'status': 'PENDING' }")
    List<SleepOverPass> findPendingPasses();

    // Find active passes for a given date
    @Query("{ 'status': 'APPROVED', 'startDate': { $lte: ?0 }, 'endDate': { $gte: ?0 } }")
    List<SleepOverPass> findActivePasses(LocalDate date);
}