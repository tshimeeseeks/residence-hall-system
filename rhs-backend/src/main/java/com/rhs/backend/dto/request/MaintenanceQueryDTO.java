package com.rhs.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceQueryDTO {

    private String id;
    private String studentId;
    private String studentName;
    private String studentEmail;
    private String roomId;
    private String queryTitle;
    private String queryDescription;
    private String category;
    private List<String> photoUrls;
    private String status;
    private String priority;
    private String assignedToId;
    private String assignedToName;
    private String resolutionNotes;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}