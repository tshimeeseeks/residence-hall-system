package com.rhs.backend.dto;

import com.rhs.backend.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user profile (hides sensitive information)
 */
@Data
@NoArgsConstructor
public class UserProfileDTO {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String userType;
    private String department;
    private String accountStatus;
    private Boolean isEnabled;

    public UserProfileDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phoneNumber = user.getPhoneNumber();
        this.userType = user.getUserType();
        this.department = user.getDepartment();
        this.accountStatus = user.getAccountStatus();
        this.isEnabled = user.getIsEnabled();
    }
}