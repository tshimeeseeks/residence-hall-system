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
    private String temporaryPassword;
    private String studentNumber;
    private String fullName;
    private String roomNumber;
    private String building;
    private Integer floor;
}