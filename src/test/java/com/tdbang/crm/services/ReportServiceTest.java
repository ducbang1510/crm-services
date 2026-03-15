/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import com.tdbang.crm.dtos.DailySalesReportDTO;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.entities.DailySalesReport;
import com.tdbang.crm.enums.ReportStatus;
import com.tdbang.crm.exceptions.CRMException;
import com.tdbang.crm.mappers.DailySalesReportMapper;
import com.tdbang.crm.repositories.DailySalesReportRepository;
import com.tdbang.crm.utils.MessageConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private DailySalesReportRepository dailySalesReportRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private DailySalesReportMapper dailySalesReportMapper;

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportService();
        ReflectionTestUtils.setField(reportService, "dailySalesReportRepository", dailySalesReportRepository);
        ReflectionTestUtils.setField(reportService, "fileStorageService", fileStorageService);
        ReflectionTestUtils.setField(reportService, "dailySalesReportMapper", dailySalesReportMapper);
    }

    @Test
    void listReports_dateRange_returnsFilteredList() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 31);
        DailySalesReport report = buildReport(1L, LocalDate.of(2026, 1, 15));
        List<DailySalesReport> reports = List.of(report);

        DailySalesReportDTO dto = new DailySalesReportDTO();
        dto.setPk(1L);

        when(dailySalesReportRepository.findByReportDateBetweenOrderByReportDateDesc(from, to)).thenReturn(reports);
        when(dailySalesReportMapper.mappingToListDTO(reports)).thenReturn(List.of(dto));

        ResponseDTO result = reportService.listReports(from, to);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.FETCHING_REPORTS_SUCCESS, result.getMsg());
        @SuppressWarnings("unchecked")
        List<DailySalesReportDTO> data = (List<DailySalesReportDTO>) result.getData();
        assertEquals(1, data.size());
    }

    @Test
    void downloadReport_existingDate_returnsBytes() {
        LocalDate date = LocalDate.of(2026, 1, 15);
        DailySalesReport report = buildReport(1L, date);
        report.setMongoFileId("abc123def456");

        when(dailySalesReportRepository.findByReportDate(date)).thenReturn(Optional.of(report));
        doAnswer(invocation -> {
            java.io.OutputStream os = invocation.getArgument(2);
            os.write("test-excel-content".getBytes());
            return null;
        }).when(fileStorageService).downloadFile(anyString(), anyString(), any());

        byte[] result = reportService.downloadReport(date);

        assertNotNull(result);
        assertEquals("test-excel-content", new String(result));
    }

    @Test
    void downloadReport_noReport_throwsException() {
        LocalDate date = LocalDate.of(2026, 1, 15);

        when(dailySalesReportRepository.findByReportDate(date)).thenReturn(Optional.empty());

        CRMException ex = assertThrows(CRMException.class,
            () -> reportService.downloadReport(date));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    // --- Helper methods ---

    private DailySalesReport buildReport(Long pk, LocalDate reportDate) {
        DailySalesReport report = new DailySalesReport();
        report.setPk(pk);
        report.setReportDate(reportDate);
        report.setTotalRevenue(new BigDecimal("5000.00"));
        report.setTotalOrders(10);
        report.setOrdersCreated(5);
        report.setOrdersApproved(3);
        report.setOrdersDelivered(1);
        report.setOrdersCanceled(1);
        report.setStatus(ReportStatus.SUCCESS);
        return report;
    }
}
