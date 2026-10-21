package com.csc340.toolpool.dto;

import jakarta.validation.constraints.NotBlank;

// The JSON body for PUT /api/reports/{id}: { "status": "RESOLVED" }
public record ReportStatusUpdateRequest(
        @NotBlank String status) {
}
