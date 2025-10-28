package com.rhs.backend.model;

import com.rhs.backend.model.embedded.GuestDetails;
import com.rhs.backend.model.enums.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "sleepover_passes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SleepOverPass {

    @Id
    private String id;

    @Field("student_id")
    private String studentId;

    @Field("student_name")
    private String studentName;

    @Field("student_email")
    private String studentEmail;

    @Field("room_id")
    private String roomId;

    // Changed from guestDetails to visitor
    @Field("visitor")
    private GuestDetails visitor;

    // Changed from checkInDate/checkOutDate to startDate/endDate
    @Field("start_date")
    private LocalDate startDate;

    @Field("end_date")
    private LocalDate endDate;

    // Changed from approvalStatus to status (String)
    @Field("status")
    private String status;

    @Field("admin_comments")
    private String adminComments;

    @Field("approved_by")
    private String approvedBy;

    @Field("approved_at")
    private LocalDateTime approvedAt;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    // Business logic methods that the service expects
    public void approve(String adminId) {
        this.status = "APPROVED";
        this.approvedBy = adminId;
        this.approvedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void reject(String adminId, String comments) {
        this.status = "REJECTED";
        this.approvedBy = adminId;
        this.adminComments = comments;
        this.updatedAt = LocalDateTime.now();
    }
}