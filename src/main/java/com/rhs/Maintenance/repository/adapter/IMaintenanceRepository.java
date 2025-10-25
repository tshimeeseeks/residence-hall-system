package com.rhs.Maintenance.repository.adapter;

import org.bson.types.ObjectId;

import com.rhs.Maintenance.model.MaintenanceQuery;
import com.rhs.Maintenance.model.enums.PriorityLevel;
import com.rhs.Maintenance.model.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface IMaintenanceRepository {
MaintenanceQuery save(MaintenanceQuery q);
Optional<MaintenanceQuery> findById(ObjectId id);
List<MaintenanceQuery> findAll();
List<MaintenanceQuery> findByStatus(Status status);
List<MaintenanceQuery> findByPriority(PriorityLevel priority);
List<MaintenanceQuery> findByAssignedTo(ObjectId assignedTo);
List<MaintenanceQuery> findByReportedAtBetween(LocalDateTime start, LocalDateTime end);
void deleteById(ObjectId id);
}