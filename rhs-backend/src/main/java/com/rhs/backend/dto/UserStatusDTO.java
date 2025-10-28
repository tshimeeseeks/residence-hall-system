package com.rhs.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for updating user status (admin only)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusDTO {
    private Boolean isEnabled;
    private String accountStatus; // "APPROVED", "PENDING", "REJECTED", "DELETED"
}