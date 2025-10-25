package com.rhs.Maintenance.dto;

import lombok.*;


import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportRequestDto {
private String reportType; // e.g., summary, recent
private String generatedBy;
private LocalDateTime start;
private LocalDateTime end;
}