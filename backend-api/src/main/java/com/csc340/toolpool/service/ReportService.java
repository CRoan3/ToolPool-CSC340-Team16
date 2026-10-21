package com.csc340.toolpool.service;

import com.csc340.toolpool.dto.ReportRequest;
import com.csc340.toolpool.dto.ReportResponse;
import com.csc340.toolpool.dto.ReportStatusUpdateRequest;
import com.csc340.toolpool.entity.Report;
import com.csc340.toolpool.entity.User;
import com.csc340.toolpool.repository.ReportRepository;
import com.csc340.toolpool.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

// Business rules for reports (issue #42). The controller calls this class; only this class
// (and other services) call the repositories.
@Service
@Transactional
public class ReportService {

    private static final Set<String> STATUSES = Set.of("OPEN", "RESOLVED");

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public ReportService(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    // A user reports another user. 400 if they report themselves, 404 if either user is missing.
    public ReportResponse createReport(Long reportedUserId, ReportRequest request) {
        if (reportedUserId.equals(request.reporterId())) {
            throw new BadRequestException("Users cannot report themselves.");
        }

        User reportedUser = findUser(reportedUserId);
        User reporter = findUser(request.reporterId());

        Report report = new Report();
        report.setReason(request.reason().trim());
        report.setReporter(reporter);
        report.setReportedUser(reportedUser);

        // Server-controlled fields: new reports always start OPEN.
        report.setStatus("OPEN");
        report.setReportDate(LocalDateTime.now());

        return ReportResponse.from(reportRepository.save(report));
    }

    // Admin: list reports, newest first. Optional status filter, e.g. ?status=OPEN.
    @Transactional(readOnly = true)
    public List<ReportResponse> listReports(String status) {
        List<Report> reports = status == null
                ? reportRepository.findAllByOrderByReportDateDesc()
                : reportRepository.findByStatusOrderByReportDateDesc(validStatus(status));
        return reports.stream().map(ReportResponse::from).toList();
    }

    // Admin: mark a report OPEN or RESOLVED. 400 if the status is invalid, 404 if missing.
    public ReportResponse updateStatus(Long id, ReportStatusUpdateRequest request) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Report " + id + " was not found."));
        report.setStatus(validStatus(request.status()));
        return ReportResponse.from(reportRepository.save(report));
    }

    // Accepts "resolved" or "RESOLVED"; anything other than OPEN/RESOLVED is a 400.
    private String validStatus(String status) {
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        if (!STATUSES.contains(normalized)) {
            throw new BadRequestException("status must be OPEN or RESOLVED.");
        }
        return normalized;
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User " + id + " was not found."));
    }
}
