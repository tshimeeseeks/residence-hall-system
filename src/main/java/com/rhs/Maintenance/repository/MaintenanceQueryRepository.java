package com.rhs.Maintenance.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.rhs.Maintenance.model.MaintenanceQuery;
import com.rhs.Maintenance.model.enums.PriorityLevel;
import com.rhs.Maintenance.model.enums.Status;

import java.time.LocalDateTime;
import java.util.List;


public interface MaintenanceQueryRepository extends MongoRepository<MaintenanceQuery, ObjectId> {
List<MaintenanceQuery> findByStatus(Status status);
List<MaintenanceQuery> findByPriority(PriorityLevel priority);
List<MaintenanceQuery> findByAssignedTo(ObjectId assignedTo);


@Query("{ 'reportedAt': { $gte: ?0, $lte: ?1 } }")
List<MaintenanceQuery> findByReportedAtBetween(LocalDateTime start, LocalDateTime end);
}
