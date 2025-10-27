package com.rhs.backend.dto.response;

import com.rhs.backend.model.MaintenanceQuery;
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
public class MaintenanceResponse {

    private String id;
    private String studentId;
    private String studentName;
    private String studentEmail;
    private String roomId;
    private String queryTitle;
    private String description;
    private String category;
    private String priority;
    private String status;
    private List<String> photoUrls;
    private String photoUrl; // Keep for backward compatibility if needed
    private String assignedToId;
    private String assignedToName;
    private String resolutionNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

    public static MaintenanceResponse fromEntity(MaintenanceQuery maintenanceQuery) {
        if (maintenanceQuery == null) {
            return null;
        }

        return MaintenanceResponse.builder()
                .id(maintenanceQuery.getId())
                .studentId(maintenanceQuery.getStudentId())
                .studentName(maintenanceQuery.getStudentName())
                .studentEmail(maintenanceQuery.getStudentEmail())
                .roomId(maintenanceQuery.getRoomId())
                .queryTitle(maintenanceQuery.getQueryTitle())
                .description(maintenanceQuery.getQueryDescription())
                .category(maintenanceQuery.getCategory())
                .priority(maintenanceQuery.getPriority())
                .status(maintenanceQuery.getStatus())
                .photoUrls(maintenanceQuery.getPhotoUrls())
                .photoUrl(maintenanceQuery.getPhotoUrls() != null && !maintenanceQuery.getPhotoUrls().isEmpty()
                        ? maintenanceQuery.getPhotoUrls().get(0)
                        : null)
                .assignedToId(maintenanceQuery.getAssignedToId())
                .assignedToName(maintenanceQuery.getAssignedToName())
                .resolutionNotes(maintenanceQuery.getResolutionNotes())
                .createdAt(maintenanceQuery.getCreatedAt())
                .updatedAt(maintenanceQuery.getUpdatedAt())
                .resolvedAt(maintenanceQuery.getResolvedAt())
                .build();
    }
}