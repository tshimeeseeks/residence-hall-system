package com.rhs.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.rhs.backend.model.enums.QueryStatus;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "maintenance_queries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceQuery {
    @Id
    private String id;

    @DBRef
    @Field("student")
    private Student student;

    @Field("room_id")
    @Indexed
    private String roomId;

    @Field("query_title")
    private String queryTitle;

    @Field("query_description")
    private String queryDescription;

    @Field("photo_urls")
    private List<String> photoUrls;

    @Field("status")
    @Indexed
    private QueryStatus status;

    @Field("priority")
    private String priority;

    @DBRef
    @Field("assigned_to")
    private Admin assignedTo;

    @Field("resolution_notes")
    private String resolutionNotes;

    @Field("resolved_at")
    private LocalDateTime resolvedAt;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}
