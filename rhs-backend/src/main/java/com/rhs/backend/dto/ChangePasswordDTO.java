package com.rhs.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for password change requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

    public boolean passwordsMatch() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }

    public boolean isValidNewPassword() {
        // Add password validation rules
        return newPassword != null &&
                newPassword.length() >= 8 &&
                newPassword.matches(".*[A-Z].*") && // At least one uppercase
                newPassword.matches(".*[a-z].*") && // At least one lowercase
                newPassword.matches(".*\\d.*"); // At least one digit
    }
}