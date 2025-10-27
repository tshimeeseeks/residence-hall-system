package com.rhs.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceStatsResponse {
    private Long total;
    private Long pending;
    private Long inProgress;
    private Long resolved;
    private Long cancelled;
    private Double averageResolutionTimeInHours;
}