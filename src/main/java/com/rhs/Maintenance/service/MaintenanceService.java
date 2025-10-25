package com.rhs.Maintenance.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.rhs.Maintenance.dto.MaintenanceQueryDto;
import com.rhs.Maintenance.model.MaintenanceQuery;
import com.rhs.Maintenance.model.enums.PriorityLevel;
import com.rhs.Maintenance.model.enums.Status;
import com.rhs.Maintenance.repository.MaintenanceQueryRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintenanceService {
    private final MaintenanceQueryRepository repository;

    public MaintenanceQuery createQuery(MaintenanceQueryDto dto) {
        MaintenanceQuery query = new MaintenanceQuery();
        query.setStudentId(new ObjectId(dto.getStudentId()));
        query.setRoomId(new ObjectId(dto.getRoomId()));
        query.setIssueType(dto.getIssueType());
        query.setDescription(dto.getDescription());
        query.setPriority(dto.getPriority());
        query.setStatus(Status.PENDING);
        query.setReportedAt(LocalDateTime.now());
        query.setPhotos(dto.getPhotos());
        repository.save(query);
        System.out.println("[NOTIFY] New maintenance request created by student: " + dto.getStudentId());
        return query;
    }

    public MaintenanceQuery updateStatus(ObjectId id, Status status) {
        MaintenanceQuery query = repository.findById(id).orElseThrow();
        query.setStatus(status);
        if (status == Status.RESOLVED) {
            query.setResolvedAt(LocalDateTime.now());
        }
        repository.save(query);
        System.out.println("[NOTIFY] Maintenance request " + id + " updated to status: " + status);
        return query;
    }

    public MaintenanceQuery assignTo(ObjectId id, ObjectId adminId) {
        MaintenanceQuery query = repository.findById(id).orElseThrow();
        query.setAssignedTo(adminId);
        repository.save(query);
        System.out.println("[NOTIFY] Query " + id + " assigned to admin: " + adminId);
        return query;
    }

    public List<MaintenanceQuery> searchQueries(PriorityLevel priority, Status status, LocalDateTime start, LocalDateTime end) {
        List<MaintenanceQuery> all = repository.findAll();
        return all.stream()
                .filter(q -> (priority == null || q.getPriority() == priority))
                .filter(q -> (status == null || q.getStatus() == status))
                .filter(q -> (start == null || end == null || (q.getReportedAt() != null &&
                        !q.getReportedAt().isBefore(start) && !q.getReportedAt().isAfter(end))))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getStats() {
        List<MaintenanceQuery> all = repository.findAll();
        Map<String, Long> byStatus = all.stream()
                .collect(Collectors.groupingBy(q -> q.getStatus().name(), Collectors.counting()));
        Map<String, Long> byPriority = all.stream()
                .collect(Collectors.groupingBy(q -> q.getPriority().name(), Collectors.counting()));

        double avgResolutionTime = all.stream()
                .filter(q -> q.getResolvedAt() != null && q.getReportedAt() != null)
                .mapToLong(q -> Duration.between(q.getReportedAt(), q.getResolvedAt()).toHours())
                .average().orElse(0);

        Map<String, Object> stats = new HashMap<>();
        stats.put("statusCounts", byStatus);
        stats.put("priorityCounts", byPriority);
        stats.put("avgResolutionTimeHrs", avgResolutionTime);
        return stats;
    }
}

