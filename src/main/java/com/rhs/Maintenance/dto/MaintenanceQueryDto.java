package com.rhs.Maintenance.dto;


import lombok.*;
import org.bson.types.ObjectId;

import com.rhs.Maintenance.model.enums.PriorityLevel;
import com.rhs.Maintenance.model.enums.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceQueryDto {
private String id; // optional for updates


@NotNull
private String studentId;


@NotNull
private String roomId;


@NotBlank
private String issueType;


private String description;


@NotNull
private PriorityLevel priority;


private Status status;


private List<String> photos;
}