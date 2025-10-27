package com.rhs.backend.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.rhs.backend.model.User;
import com.rhs.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FirebaseAuth firebaseAuth;

    /**
     * Get all users
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by Firebase UID
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByFirebaseUid(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid);
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Update user profile
     */
    @Transactional
    public User updateUser(String firebaseUid, String firstName, String lastName,
            String roomNumber, String phoneNumber) {
        User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (firstName != null)
            user.setFirstName(firstName);
        if (lastName != null)
            user.setLastName(lastName);
        if (phoneNumber != null)
            user.setPhoneNumber(phoneNumber);

        user.setUpdatedAt(LocalDateTime.now());

        user = userRepository.save(user);
        log.info("User updated: {}", firebaseUid);

        return user;
    }

    /**
     * Delete user (admin only)
     */
    @Transactional
    public void deleteUser(String firebaseUid) {
        try {
            // Delete from Firebase
            firebaseAuth.deleteUser(firebaseUid);

            // Delete from MongoDB
            User user = userRepository.findByFirebaseUid(firebaseUid)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            userRepository.delete(user);

            log.info("User deleted: {}", firebaseUid);

        } catch (FirebaseAuthException e) {
            log.error("Error deleting user from Firebase: {}", e.getMessage());
            throw new RuntimeException("Failed to delete user: " + e.getMessage());
        }
    }

    /**
     * Get user by email
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Check if user exists by Firebase UID
     */
    @Transactional(readOnly = true)
    public boolean userExists(String firebaseUid) {
        return userRepository.existsByFirebaseUid(firebaseUid);
    }
}