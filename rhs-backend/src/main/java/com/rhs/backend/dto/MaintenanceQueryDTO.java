package com.rhs.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
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

    // Builder pattern
    public static MaintenanceQueryDTOBuilder builder() {
        return new MaintenanceQueryDTOBuilder();
    }

    public static class MaintenanceQueryDTOBuilder {
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

        public MaintenanceQueryDTOBuilder id(String id) {
            this.id = id;
            return this;
        }

        public MaintenanceQueryDTOBuilder studentId(String studentId) {
            this.studentId = studentId;
            return this;
        }

        public MaintenanceQueryDTOBuilder studentName(String studentName) {
            this.studentName = studentName;
            return this;
        }

        public MaintenanceQueryDTOBuilder studentEmail(String studentEmail) {
            this.studentEmail = studentEmail;
            return this;
        }

        public MaintenanceQueryDTOBuilder roomId(String roomId) {
            this.roomId = roomId;
            return this;
        }

        public MaintenanceQueryDTOBuilder queryTitle(String queryTitle) {
            this.queryTitle = queryTitle;
            return this;
        }

        public MaintenanceQueryDTOBuilder queryDescription(String queryDescription) {
            this.queryDescription = queryDescription;
            return this;
        }

        public MaintenanceQueryDTOBuilder category(String category) {
            this.category = category;
            return this;
        }

        public MaintenanceQueryDTOBuilder photoUrls(List<String> photoUrls) {
            this.photoUrls = photoUrls;
            return this;
        }

        public MaintenanceQueryDTOBuilder status(String status) {
            this.status = status;
            return this;
        }

        public MaintenanceQueryDTOBuilder priority(String priority) {
            this.priority = priority;
            return this;
        }

        public MaintenanceQueryDTOBuilder assignedToId(String assignedToId) {
            this.assignedToId = assignedToId;
            return this;
        }

        public MaintenanceQueryDTOBuilder assignedToName(String assignedToName) {
            this.assignedToName = assignedToName;
            return this;
        }

        public MaintenanceQueryDTOBuilder resolutionNotes(String resolutionNotes) {
            this.resolutionNotes = resolutionNotes;
            return this;
        }

        public MaintenanceQueryDTOBuilder resolvedAt(LocalDateTime resolvedAt) {
            this.resolvedAt = resolvedAt;
            return this;
        }

        public MaintenanceQueryDTOBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public MaintenanceQueryDTOBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public MaintenanceQueryDTO build() {
            MaintenanceQueryDTO dto = new MaintenanceQueryDTO();
            dto.id = this.id;
            dto.studentId = this.studentId;
            dto.studentName = this.studentName;
            dto.studentEmail = this.studentEmail;
            dto.roomId = this.roomId;
            dto.queryTitle = this.queryTitle;
            dto.queryDescription = this.queryDescription;
            dto.category = this.category;
            dto.photoUrls = this.photoUrls;
            dto.status = this.status;
            dto.priority = this.priority;
            dto.assignedToId = this.assignedToId;
            dto.assignedToName = this.assignedToName;
            dto.resolutionNotes = this.resolutionNotes;
            dto.resolvedAt = this.resolvedAt;
            dto.createdAt = this.createdAt;
            dto.updatedAt = this.updatedAt;
            return dto;
        }
    }
}