package com.rhs.backend.repository;

import com.rhs.backend.model.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends MongoRepository<Admin, String> {

    Optional<Admin> findByFirebaseUid(String firebaseUid);

    Optional<Admin> findByEmail(String email);

    boolean existsByFirebaseUid(String firebaseUid);

    boolean existsByEmail(String email);

    @Query("{'adminPermissions.role': ?0}")
    List<Admin> findByRole(String role);

    @Query("{'adminPermissions.canManageQueries': true}")
    List<Admin> findAdminsWhoCanManageQueries();

    @Query("{'adminPermissions.canApprovePasses': true}")
    List<Admin> findAdminsWhoCanApprovePasses();

    List<Admin> findByDepartment(String department);
}