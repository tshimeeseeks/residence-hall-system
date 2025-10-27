package com.rhs.backend.repository;

import com.rhs.backend.model.User;
import com.rhs.backend.model.enums.AccountStatus;
import com.rhs.backend.model.enums.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Find user by Firebase UID
     */
    Optional<User> findByFirebaseUid(String firebaseUid);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find users by user type
     */
    List<User> findByUserType(UserType userType);

    /**
     * Find users by account status
     */
    List<User> findByAccountStatus(AccountStatus status);

    /**
     * Find enabled users
     */
    List<User> findByIsEnabled(Boolean isEnabled);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if Firebase UID exists
     */
    boolean existsByFirebaseUid(String firebaseUid);

    /**
     * Find all admins (ADMIN and SUPER_ADMIN)
     */
    List<User> findByUserTypeIn(List<UserType> userTypes);

    /**
     * Count users by user type
     */
    long countByUserType(UserType userType);

    /**
     * Find all users with pagination
     */
    Page<User> findAll(Pageable pageable);
}