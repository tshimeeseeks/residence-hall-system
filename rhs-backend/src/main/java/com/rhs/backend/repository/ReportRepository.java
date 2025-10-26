package com.rhs.backend.repository;

import com.rhs.backend.model.Reports;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends MongoRepository<Reports, ObjectId> {
}
