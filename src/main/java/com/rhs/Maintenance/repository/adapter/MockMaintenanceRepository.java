package com.rhs.Maintenance.repository.adapter;

import com.rhs.Maintenance.model.MaintenanceQuery;
import com.rhs.Maintenance.model.enums.PriorityLevel;
import com.rhs.Maintenance.model.enums.Status;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Simple in-memory repository for development / tests.
 */
@Component
@Profile("mock")
public class MockMaintenanceRepository {

    private final Map<ObjectId, MaintenanceQuery> store = new ConcurrentHashMap<>();

    public MockMaintenanceRepository() {
        // optional seeding
        MaintenanceQuery q = new MaintenanceQuery();
        q.setId(new ObjectId());
        q.setIssueType("Leaky tap");
        q.setDescription("Tap leaking in bathroom");
        q.setPriority(PriorityLevel.MEDIUM);
        q.setStatus(Status.PENDING);
        q.setReportedAt(LocalDateTime.now().minusDays(1));
        store.put(q.getId(), q);
    }

    public <S extends MaintenanceQuery> S save(S entity) {
        if (entity.getId() == null) entity.setId(new ObjectId());
        store.put(entity.getId(), entity);
        return entity;
    }

    public Optional<MaintenanceQuery> findById(ObjectId id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<MaintenanceQuery> findAll() {
        return new ArrayList<>(store.values());
    }

    public void deleteById(ObjectId id) {
        store.remove(id);
    }

    public List<MaintenanceQuery> findByStatus(Status status) {
        return store.values().stream()
                .filter(q -> q.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<MaintenanceQuery> findByPriority(PriorityLevel priority) {
        return store.values().stream()
                .filter(q -> q.getPriority() == priority)
                .collect(Collectors.toList());
    }

    public List<MaintenanceQuery> findByAssignedTo(ObjectId assignedTo) {
        return store.values().stream()
                .filter(q -> assignedTo != null && assignedTo.equals(q.getAssignedTo()))
                .collect(Collectors.toList());
    }

    public List<MaintenanceQuery> findByReportedAtBetween(LocalDateTime start, LocalDateTime end) {
        return store.values().stream()
                .filter(q -> q.getReportedAt() != null && !q.getReportedAt().isBefore(start) && !q.getReportedAt().isAfter(end))
                .collect(Collectors.toList());
    }
}
