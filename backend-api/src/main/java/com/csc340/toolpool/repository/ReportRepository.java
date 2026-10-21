package com.csc340.toolpool.repository;

import com.csc340.toolpool.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Data access for the reports table.
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // All reports, newest first.
    List<Report> findAllByOrderByReportDateDesc();

    // Reports with one status (e.g. OPEN), newest first.
    List<Report> findByStatusOrderByReportDateDesc(String status);

    // "Did this user file a report, or get reported?" Used before deleting a user.
    boolean existsByReporterIdOrReportedUserId(Long reporterId, Long reportedUserId);
}
