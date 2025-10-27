package com.rhs.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "maintenance_queries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceQuery {

    @Id
    private String id;

    // Denormalized student data
    @Field("student_id")
    private String studentId;

    @Field("student_name")
    private String studentName;

    @Field("student_email")
    private String studentEmail;

    // Denormalized room data
    @Field("room_id")
    private String roomId;

    @Field("query_title")
    private String queryTitle;

    @Field("query_description")
    private String queryDescription;

    @Field("category")
    private String category;

    @Field("photo_urls")
    private List<String> photoUrls;

    @Field("status")
    private String status = "PENDING";

    @Field("priority")
    private String priority;

    // Denormalized admin data (who it's assigned to)
    @Field("assigned_to_id")
    private String assignedToId;

    @Field("assigned_to_name")
    private String assignedToName;

    @Field("resolution_notes")
    private String resolutionNotes;

    @Field("resolved_at")
    private LocalDateTime resolvedAt;

    @Field("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Field("updated_at")
    private LocalDateTime updatedAt;

    // Builder pattern
    public static MaintenanceQueryBuilder builder() {
        return new MaintenanceQueryBuilder();
    }

    public static class MaintenanceQueryBuilder {
        private String id;
        private String studentId;
        private String studentName;
        private String studentEmail;
        private String roomId;
        private String queryTitle;
        private String queryDescription;
        private String category;
        private List<String> photoUrls;
        private String status = "PENDING";
        private String priority;
        private String assignedToId;
        private String assignedToName;
        private String resolutionNotes;
        private LocalDateTime resolvedAt;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt;

        public MaintenanceQueryBuilder id(String id) {
            this.id = id;
            return this;
        }

        public MaintenanceQueryBuilder studentId(String studentId) {
            this.studentId = studentId;
            return this;
        }

        public MaintenanceQueryBuilder studentName(String studentName) {
            this.studentName = studentName;
            return this;
        }

        public MaintenanceQueryBuilder studentEmail(String studentEmail) {
            this.studentEmail = studentEmail;
            return this;
        }

        public MaintenanceQueryBuilder roomId(String roomId) {
            this.roomId = roomId;
            return this;
        }

        public MaintenanceQueryBuilder queryTitle(String queryTitle) {
            this.queryTitle = queryTitle;
            return this;
        }

        public MaintenanceQueryBuilder queryDescription(String queryDescription) {
            this.queryDescription = queryDescription;
            return this;
        }

        public MaintenanceQueryBuilder category(String category) {
            this.category = category;
            return this;
        }

        public MaintenanceQueryBuilder photoUrls(List<String> photoUrls) {
            this.photoUrls = photoUrls;
            return this;
        }

        public MaintenanceQueryBuilder status(String status) {
            this.status = status;
            return this;
        }

        public MaintenanceQueryBuilder priority(String priority) {
            this.priority = priority;
            return this;
        }

        public MaintenanceQueryBuilder assignedToId(String assignedToId) {
            this.assignedToId = assignedToId;
            return this;
        }

        public MaintenanceQueryBuilder assignedToName(String assignedToName) {
            this.assignedToName = assignedToName;
            return this;
        }

        public MaintenanceQueryBuilder resolutionNotes(String resolutionNotes) {
            this.resolutionNotes = resolutionNotes;
            return this;
        }

        public MaintenanceQueryBuilder resolvedAt(LocalDateTime resolvedAt) {
            this.resolvedAt = resolvedAt;
            return this;
        }

        public MaintenanceQueryBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public MaintenanceQueryBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public MaintenanceQuery build() {
            MaintenanceQuery query = new MaintenanceQuery();
            query.id = this.id;
            query.studentId = this.studentId;
            query.studentName = this.studentName;
            query.studentEmail = this.studentEmail;
            query.roomId = this.roomId;
            query.queryTitle = this.queryTitle;
            query.queryDescription = this.queryDescription;
            query.category = this.category;
            query.photoUrls = this.photoUrls;
            query.status = this.status;
            query.priority = this.priority;
            query.assignedToId = this.assignedToId;
            query.assignedToName = this.assignedToName;
            query.resolutionNotes = this.resolutionNotes;
            query.resolvedAt = this.resolvedAt;
            query.createdAt = this.createdAt;
            query.updatedAt = this.updatedAt;
            return query;
        }
    }
}