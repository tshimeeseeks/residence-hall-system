package com.rhs.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;
import com.rhs.backend.model.enums.UserType;
import com.rhs.backend.model.enums.AccountStatus;
import java.time.LocalDateTime;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true) // ADD toBuilder = true
public class User {
    @Id
    private String id;

    @Field("firebase_uid")
    @Indexed(unique = true)
    private String firebaseUid;

    @Field("email")
    @Indexed(unique = true)
    private String email;

    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    @Field("phone_number")
    private String phoneNumber;

    @Field("user_type")
    private UserType userType;

    @Field("account_status")
    private AccountStatus accountStatus;

    @Field("is_enabled")
    private Boolean isEnabled;

    @Field("approved_by_admin_id")
    private String approvedByAdminId;

    @Field("approval_date")
    private LocalDateTime approvalDate;

    @Field("rejection_reason")
    private String rejectionReason;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field("last_login")
    private LocalDateTime lastLogin;
}