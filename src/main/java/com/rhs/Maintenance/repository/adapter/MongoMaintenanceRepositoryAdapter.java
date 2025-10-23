package com.rhs.Maintenance.repository.adapter;

import com.rhs.Maintenance.model.MaintenanceQuery;
import com.rhs.Maintenance.model.enums.PriorityLevel;
import com.rhs.Maintenance.model.enums.Status;
import com.rhs.Maintenance.repository.MaintenanceQueryRepository;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Adapter that delegates to the real Spring Data repository.
 * Important: DO NOT implement MaintenanceQueryRepository here. Just delegate.
 */
@Component
@Profile("!mock")
public class MongoMaintenanceRepositoryAdapter {

    private final MaintenanceQueryRepository delegate;

    public MongoMaintenanceRepositoryAdapter(MaintenanceQueryRepository delegate) {
        this.delegate = delegate;
    }

    public <S extends MaintenanceQuery> S save(S entity) {
        return delegate.save(entity);
    }

    public Optional<MaintenanceQuery> findById(ObjectId id) {
        return delegate.findById(id);
    }

    public List<MaintenanceQuery> findAll() {
        return delegate.findAll();
    }

    public void deleteById(ObjectId id) {
        delegate.deleteById(id);
    }

    public List<MaintenanceQuery> findByStatus(Status status) {
        return delegate.findByStatus(status);
    }

    public List<MaintenanceQuery> findByPriority(PriorityLevel priority) {
        return delegate.findByPriority(priority);
    }

    public List<MaintenanceQuery> findByAssignedTo(ObjectId assignedTo) {
        return delegate.findByAssignedTo(assignedTo);
    }

    public List<MaintenanceQuery> findByReportedAtBetween(LocalDateTime start, LocalDateTime end) {
        return delegate.findByReportedAtBetween(start, end);
    }

    // Expose access to underlying repo if needed:
    public MaintenanceQueryRepository getDelegate() {
        return delegate;
    }
}
