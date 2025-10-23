package com.rhs.Maintenance.model;


import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "reports")
public class Report {
@Id
private ObjectId id;


private String reportType;
private Object reportData;
private Object parameters;
private ObjectId generatedBy;
private LocalDateTime generatedAt;
private String filePath;
}