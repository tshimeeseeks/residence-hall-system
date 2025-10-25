package com.rhs.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentCreatedResponse {
    private String message;
    private String studentId;
    private String firebaseUid;
    private String email;
    private String temporaryPassword; // Admin sees this to share with student
    private String studentNumber;
    private String fullName;
}