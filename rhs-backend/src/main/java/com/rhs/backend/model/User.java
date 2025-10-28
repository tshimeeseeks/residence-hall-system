package com.rhs.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Field("firebase_uid")
    private String firebaseUid;

    @Field("email")
    private String email;

    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    @Field("phone_number")
    private String phoneNumber;

    @Field("user_type")
    private String userType; // "ADMIN", "STUDENT", "STAFF", etc.

    @Field("department")
    private String department;

    @Field("account_status")
    private String accountStatus; // "APPROVED", "PENDING", "REJECTED", "DELETED"

    @Field("is_enabled")
    private Boolean isEnabled;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field("_class")
    private String classType = "com.rhs.backend.model.Admin";

    // Constructor for creating new users
    public User(String firebaseUid, String email, String firstName, String lastName, String userType) {
        this.firebaseUid = firebaseUid;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
        this.accountStatus = "PENDING";
        this.isEnabled = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Helper method to check if user is admin
    public boolean isAdmin() {
        return "ADMIN".equals(this.userType) &&
                "APPROVED".equals(this.accountStatus) &&
                Boolean.TRUE.equals(this.isEnabled);
    }

    // Helper method to check if user is active
    public boolean isActive() {
        return "APPROVED".equals(this.accountStatus) &&
                Boolean.TRUE.equals(this.isEnabled);
    }
}