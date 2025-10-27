package com.rhs.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceQueryFilter {

    private String roomId; // Changed from Long to String
    private String studentId;
    private String status;
    private String category;
    private String priority;
    private String assignedToId;
}