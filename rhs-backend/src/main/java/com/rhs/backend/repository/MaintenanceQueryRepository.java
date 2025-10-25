package com.rhs.backend.repository;

import com.rhs.backend.model.MaintenanceQuery;
import com.rhs.backend.model.Admin;
import com.rhs.backend.model.enums.QueryStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaintenanceQueryRepository extends MongoRepository<MaintenanceQuery, String> {

    List<MaintenanceQuery> findByRoomId(String roomId);

    List<MaintenanceQuery> findByStatus(QueryStatus status);

    List<MaintenanceQuery> findByAssignedTo(Admin admin);
}
