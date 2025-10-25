package com.rhs.backend.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ApprovalRequest {
    @NotBlank(message = "Student ID is required")
    private String studentId;

    @NotNull(message = "Approval status is required")
    private Boolean approved;

    private String rejectionReason;
}