package com.rhs.backend.repository;

import com.rhs.backend.model.EmergencyContact;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmergencyContactRepository extends MongoRepository<EmergencyContact, String> {
}
