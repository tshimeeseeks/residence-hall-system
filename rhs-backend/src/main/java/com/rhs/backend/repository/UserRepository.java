package com.rhs.backend.repository;

import com.rhs.backend.model.User;
import com.rhs.backend.model.enums.UserType;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByFirebaseUid(String firebaseUid);

    Optional<User> findByEmail(String email);

    List<User> findByUserType(UserType userType);

    boolean existsByFirebaseUid(String firebaseUid);

    boolean existsByEmail(String email);
}