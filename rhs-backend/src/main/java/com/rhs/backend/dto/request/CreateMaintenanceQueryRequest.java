package com.rhs.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMaintenanceQueryRequest {

    @NotBlank(message = "Room ID is required")
    private String roomId;

    @NotBlank(message = "Query title is required")
    private String queryTitle;

    @NotBlank(message = "Query description is required")
    private String queryDescription;

    private String category;

    private String priority;

    private List<String> photoUrls;
}