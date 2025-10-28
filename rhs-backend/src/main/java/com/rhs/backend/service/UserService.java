package com.rhs.backend.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.rhs.backend.model.User;
import com.rhs.backend.repository.UserRepository;
import com.rhs.backend.dto.UserProfileDTO;
import com.rhs.backend.dto.ChangePasswordDTO;
import com.rhs.backend.dto.UserStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FirebaseAuth firebaseAuth;

    /**
     * Verify Firebase token and get user from MongoDB
     */
    public User getUserByFirebaseToken(String token) throws Exception {
        // Verify Firebase token
        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token);
        String firebaseUid = decodedToken.getUid();

        // Get user from MongoDB using firebase_uid
        User user = userRepository.findByFirebaseUid(firebaseUid);

        if (user == null) {
            throw new Exception("User not found in database");
        }

        return user;
    }

    /**
     * Check if user is admin
     */
    public boolean isAdmin(String token) throws Exception {
        User user = getUserByFirebaseToken(token);
        return "ADMIN".equals(user.getUserType()) &&
                "APPROVED".equals(user.getAccountStatus());
    }

    /**
     * Update user profile
     */
    public User updateUserProfile(String token, UserProfileDTO profileUpdate) throws Exception {
        User user = getUserByFirebaseToken(token);

        // Update allowed fields
        if (profileUpdate.getFirstName() != null) {
            user.setFirstName(profileUpdate.getFirstName());
        }
        if (profileUpdate.getLastName() != null) {
            user.setLastName(profileUpdate.getLastName());
        }
        if (profileUpdate.getPhoneNumber() != null) {
            user.setPhoneNumber(profileUpdate.getPhoneNumber());
        }

        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * Change user password (updates Firebase)
     */
    public void changePassword(String token, ChangePasswordDTO passwordDTO) throws Exception {
        User user = getUserByFirebaseToken(token);

        // Update password in Firebase - Use constructor, not builder
        UserRecord.UpdateRequest updateRequest = new UserRecord.UpdateRequest(user.getFirebaseUid())
                .setPassword(passwordDTO.getNewPassword());
        
        firebaseAuth.updateUser(updateRequest);
    }

    /**
     * Get all users (admin only)
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by ID (admin only)
     */
    public User getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Update user status (admin only)
     */
    public User updateUserStatus(String id, UserStatusDTO statusDTO) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("User not found"));

        if (statusDTO.getIsEnabled() != null) {
            user.setIsEnabled(statusDTO.getIsEnabled());
        }

        if (statusDTO.getAccountStatus() != null) {
            user.setAccountStatus(statusDTO.getAccountStatus());
        }

        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * Soft delete user (admin only)
     */
    public void softDeleteUser(String id) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("User not found"));

        user.setIsEnabled(false);
        user.setAccountStatus("DELETED");
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }
}