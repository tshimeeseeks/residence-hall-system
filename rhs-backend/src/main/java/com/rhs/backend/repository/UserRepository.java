package com.rhs.backend.repository;

import com.rhs.backend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Find user by Firebase UID
     * This is the key method to link Firebase auth with MongoDB storage
     */
    User findByFirebaseUid(String firebaseUid);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);

    /**
     * Find all users by user type
     */
    java.util.List<User> findByUserType(String userType);

    /**
     * Find all users by account status
     */
    java.util.List<User> findByAccountStatus(String accountStatus);

    /**
     * Find all enabled users
     */
    java.util.List<User> findByIsEnabled(Boolean isEnabled);

    /**
     * Find users by department
     */
    java.util.List<User> findByDepartment(String department);
}