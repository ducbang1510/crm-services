/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tdbang.crm.entities.DailySalesReport;
import com.tdbang.crm.enums.ReportStatus;

@Repository
public interface DailySalesReportRepository extends JpaRepository<DailySalesReport, Long> {

    /**
     * Finds the report record for a specific calendar day.
     * Used by the scheduler to skip re-running a job when a SUCCESS report
     * already exists for the requested date.
     */
    Optional<DailySalesReport> findByReportDate(LocalDate reportDate);

    /**
     * Checks whether a report with the given status already exists for a date.
     */
    boolean existsByReportDateAndStatus(LocalDate reportDate, ReportStatus status);

    List<DailySalesReport> findByReportDateBetweenOrderByReportDateDesc(LocalDate from, LocalDate to);
}
