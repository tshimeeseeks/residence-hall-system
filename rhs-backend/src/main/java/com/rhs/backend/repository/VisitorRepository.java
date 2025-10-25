package com.rhs.backend.repository;

import com.rhs.backend.model.Visitor;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VisitorRepository extends MongoRepository<Visitor, String> {
}
