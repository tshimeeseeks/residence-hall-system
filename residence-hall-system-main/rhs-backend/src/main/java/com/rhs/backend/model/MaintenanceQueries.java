package com.rhs.backend.model;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document(collection = "maintenance_queries")
public class MaintenanceQueries {
  @Id
  private String id;

  private String title;
  private String description;
  private Status status;

  private String createdByUserId; // user id

  @CreatedDate
  private Date createdAt;
  @LastModifiedDate
  private Date updatedAt;

  public enum Status { OPEN, IN_PROGRESS, RESOLVED }
}
