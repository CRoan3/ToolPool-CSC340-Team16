package com.csc340.toolpool.dto;

import com.csc340.toolpool.entity.Report;

import java.time.LocalDateTime;

// What the API sends back for a report. The two users are flattened to id + name,
// so there is no nested User (no password, no JSON loop).
public record ReportResponse(
        Long id,
        String reason,
        String status,
        LocalDateTime reportDate,
        Long reporterId,
        String reporterName,
        Long reportedUserId,
        String reportedUserName) {

    // Copies the fields we want to expose from a Report entity.
    // Call this inside the service, while the lazy users can still be loaded.
    public static ReportResponse from(Report report) {
        return new ReportResponse(report.getId(), report.getReason(), report.getStatus(),
                report.getReportDate(),
                report.getReporter().getId(), report.getReporter().getName(),
                report.getReportedUser().getId(), report.getReportedUser().getName());
    }
}
