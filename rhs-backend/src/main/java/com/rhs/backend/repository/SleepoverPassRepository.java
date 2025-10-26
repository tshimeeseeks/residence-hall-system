package com.rhs.backend.repository;

import com.rhs.backend.model.SleepOverPass;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SleepoverPassRepository extends MongoRepository<SleepOverPass, String> {
}
