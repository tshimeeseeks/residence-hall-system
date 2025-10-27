package com.rhs.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMaintenanceStatusRequest {

    @NotBlank(message = "Status is required")
    private String status;

    private String notes;
}