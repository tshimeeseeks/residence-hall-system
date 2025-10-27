package com.rhs.backend.model;

import com.rhs.backend.model.embedded.GuestDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "sleepover_passes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SleepOverPass {

    @Id
    private String id;

    @Field("student_id")
    private String studentId;

    @Field("visitor")
    private GuestDetails visitor;

    @Field("start_date")
    private LocalDate startDate;

    @Field("end_date")
    private LocalDate endDate;

    @Field("status")
    private String status = "PENDING";

    @Field("id_document_url")
    private String idDocumentUrl;

    @Field("approved_by")
    private String approvedBy;

    @Field("approved_at")
    private LocalDateTime approvedAt;

    @Field("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field("rejection_reason")
    private String rejectionReason;

    // Builder pattern implementation
    public static SleepOverPassBuilder builder() {
        return new SleepOverPassBuilder();
    }

    public static class SleepOverPassBuilder {
        private String id;
        private String studentId;
        private GuestDetails visitor;
        private LocalDate startDate;
        private LocalDate endDate;
        private String status = "PENDING";
        private String idDocumentUrl;
        private String approvedBy;
        private LocalDateTime approvedAt;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt;
        private String rejectionReason;

        public SleepOverPassBuilder id(String id) {
            this.id = id;
            return this;
        }

        public SleepOverPassBuilder studentId(String studentId) {
            this.studentId = studentId;
            return this;
        }

        public SleepOverPassBuilder visitor(GuestDetails visitor) {
            this.visitor = visitor;
            return this;
        }

        public SleepOverPassBuilder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public SleepOverPassBuilder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public SleepOverPassBuilder status(String status) {
            this.status = status;
            return this;
        }

        public SleepOverPassBuilder idDocumentUrl(String idDocumentUrl) {
            this.idDocumentUrl = idDocumentUrl;
            return this;
        }

        public SleepOverPassBuilder approvedBy(String approvedBy) {
            this.approvedBy = approvedBy;
            return this;
        }

        public SleepOverPassBuilder approvedAt(LocalDateTime approvedAt) {
            this.approvedAt = approvedAt;
            return this;
        }

        public SleepOverPassBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public SleepOverPassBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public SleepOverPassBuilder rejectionReason(String rejectionReason) {
            this.rejectionReason = rejectionReason;
            return this;
        }

        public SleepOverPass build() {
            SleepOverPass pass = new SleepOverPass();
            pass.id = this.id;
            pass.studentId = this.studentId;
            pass.visitor = this.visitor;
            pass.startDate = this.startDate;
            pass.endDate = this.endDate;
            pass.status = this.status;
            pass.idDocumentUrl = this.idDocumentUrl;
            pass.approvedBy = this.approvedBy;
            pass.approvedAt = this.approvedAt;
            pass.createdAt = this.createdAt;
            pass.updatedAt = this.updatedAt;
            pass.rejectionReason = this.rejectionReason;
            return pass;
        }
    }

    /**
     * Create application with guest details
     */
    public void createApplication(GuestDetails guestDetails, String documentPath) {
        this.visitor = guestDetails;
        this.idDocumentUrl = documentPath;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Upload ID document
     */
    public void uploadIdDocument(String documentPath) {
        this.idDocumentUrl = documentPath;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Approve the sleepover pass
     */
    public void approve(String adminId) {
        this.status = "APPROVED";
        this.approvedBy = adminId;
        this.approvedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Reject the sleepover pass
     */
    public void reject(String adminId, String reason) {
        this.status = "REJECTED";
        this.approvedBy = adminId;
        this.rejectionReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Check if the pass is valid (approved and within date range)
     */
    public boolean isValid() {
        if (!"APPROVED".equals(this.status)) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return !today.isBefore(this.startDate) && !today.isAfter(this.endDate);
    }

    /**
     * Check if the pass has expired
     */
    public boolean isExpired() {
        if (this.endDate == null) {
            return false;
        }
        return LocalDate.now().isAfter(this.endDate);
    }

    /**
     * Check if pass is pending
     */
    public boolean isPending() {
        return "PENDING".equals(this.status);
    }

    /**
     * Check if pass is approved
     */
    public boolean isApproved() {
        return "APPROVED".equals(this.status);
    }

    /**
     * Check if pass is rejected
     */
    public boolean isRejected() {
        return "REJECTED".equals(this.status);
    }
}