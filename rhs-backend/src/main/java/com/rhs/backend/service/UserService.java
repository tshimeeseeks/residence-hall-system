package com.rhs.backend.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.rhs.backend.model.User;
import com.rhs.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final FirebaseAuth firebaseAuth;

    @Transactional
    public User syncUser(String firebaseUid, String email) {
        Optional<User> existingUser = userRepository.findByFirebaseUid(firebaseUid);
        
        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        
        User newUser = new User();
        newUser.setFirebaseUid(firebaseUid);
        newUser.setEmail(email);
        
        return userRepository.save(newUser);
    }

    @Transactional
    public Map<String, Object> createStudent(String email, String firstName, String lastName, 
                                              String roomNumber, String phoneNumber) {
        try {
            if (userRepository.existsByEmail(email)) {
                throw new RuntimeException("User with this email already exists");
            }

            String temporaryPassword = UUID.randomUUID().toString().substring(0, 8);
            
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(temporaryPassword)
                .setEmailVerified(true)
                .setDisabled(false);

            UserRecord userRecord = firebaseAuth.createUser(request);
            
            User user = new User();
            user.setFirebaseUid(userRecord.getUid());
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setRoomNumber(roomNumber);
            user.setPhoneNumber(phoneNumber);
            user.setAdmin(false);
            
            User savedUser = userRepository.save(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", savedUser);
            response.put("temporaryPassword", temporaryPassword);
            response.put("message", "Student created successfully");
            
            return response;
            
        } catch (FirebaseAuthException e) {
            log.error("Failed to create user in Firebase", e);
            throw new RuntimeException("Failed to create user: " + e.getMessage());
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserByFirebaseUid(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid);
    }

    @Transactional
    public User updateUser(String firebaseUid, String firstName, String lastName, 
                           String roomNumber, String phoneNumber) {
        User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (roomNumber != null) user.setRoomNumber(roomNumber);
        if (phoneNumber != null) user.setPhoneNumber(phoneNumber);
        
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String firebaseUid) {
        try {
            firebaseAuth.deleteUser(firebaseUid);
            
            User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            userRepository.delete(user);
            
        } catch (FirebaseAuthException e) {
            log.error("Failed to delete user from Firebase", e);
            throw new RuntimeException("Failed to delete user: " + e.getMessage());
        }
    }
}
