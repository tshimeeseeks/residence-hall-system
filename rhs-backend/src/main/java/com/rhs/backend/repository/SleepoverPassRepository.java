package com.rhs.backend.repository;

import com.rhs.backend.model.SleepoverPass;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SleepoverPassRepository extends MongoRepository<SleepoverPass, String> {
}
