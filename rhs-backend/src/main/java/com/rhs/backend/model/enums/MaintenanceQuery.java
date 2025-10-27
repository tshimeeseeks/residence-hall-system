package com.rhs.backend.model.enums;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "maintenance_queries")
public class MaintenanceQuery {
    @Id
    private ObjectId id;

    private ObjectId studentId;
    private ObjectId roomId;
    private String issueType;
    private String description;
    private PriorityLevel priority;
    private Status status;
    private List<String> photos = new ArrayList<>();
    private LocalDateTime reportedAt;
    private LocalDateTime resolvedAt;
    private ObjectId assignedTo;
    private String notes;

}