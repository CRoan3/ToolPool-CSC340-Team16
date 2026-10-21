package com.csc340.toolpool.controller;

import com.csc340.toolpool.dto.ReportRequest;
import com.csc340.toolpool.dto.ReportResponse;
import com.csc340.toolpool.dto.ReportStatusUpdateRequest;
import com.csc340.toolpool.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

// Maps report requests to the report service (issue #42).
// Creating a report is nested under the reported user; admin actions are under /api/reports.
@RestController
@RequestMapping("/api")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // POST /api/users/{id}/reports -> 201, 400 if reason blank or self-report, 404 if a user is missing.
    @PostMapping("/users/{id}/reports")
    public ResponseEntity<ReportResponse> createReport(@PathVariable Long id,
            @Valid @RequestBody ReportRequest request) {
        ReportResponse report = reportService.createReport(id, request);
        return ResponseEntity.created(URI.create("/api/reports/" + report.id())).body(report);
    }

    // GET /api/reports -> 200. Optional ?status=OPEN or ?status=RESOLVED (400 if anything else).
    @GetMapping("/reports")
    public List<ReportResponse> listReports(@RequestParam(required = false) String status) {
        return reportService.listReports(status);
    }

    // PUT /api/reports/{id} -> 200, 400 if invalid status, 404 if missing.
    @PutMapping("/reports/{id}")
    public ReportResponse updateStatus(@PathVariable Long id,
            @Valid @RequestBody ReportStatusUpdateRequest request) {
        return reportService.updateStatus(id, request);
    }
}
