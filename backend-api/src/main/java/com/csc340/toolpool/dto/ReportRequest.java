package com.csc340.toolpool.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// The JSON body for POST /api/users/{id}/reports.
// The reported user comes from the URL; status and reportDate are set by the server.
public record ReportRequest(
        @NotNull Long reporterId,
        @NotBlank @Size(max = 500) String reason) {
}
