package com.rhs.backend.dto;

import com.rhs.backend.model.Admin;
import com.rhs.backend.model.enums.QueryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Maintenance Query operations
 * Matches the MaintenanceQuery model structure
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceQueryDTO {

    private String id;

    // Student reference (using ID instead of full object)
    private String studentId;

    // Student details for display (optional, populated on read)
    private String studentName;
    private String studentEmail;

    private String roomId;

    private String queryTitle;

    private String queryDescription;

    private List<String> photoUrls;

    private QueryStatus status;

    private String priority;

    // Admin reference (using ID or full object)
    private String assignedToId;
    private Admin assignedTo;

    // Admin details for display (optional, populated on read)
    private String assignedToName;

    private String resolutionNotes;

    private LocalDateTime resolvedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
