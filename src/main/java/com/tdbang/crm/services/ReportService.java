/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.services;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tdbang.crm.dtos.DailySalesReportDTO;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.entities.DailySalesReport;
import com.tdbang.crm.enums.CollectionType;
import com.tdbang.crm.exceptions.CRMException;
import com.tdbang.crm.mappers.DailySalesReportMapper;
import com.tdbang.crm.repositories.DailySalesReportRepository;
import com.tdbang.crm.utils.MessageConstants;

@Log4j2
@Service
public class ReportService {

    @Autowired
    private DailySalesReportRepository dailySalesReportRepository;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private DailySalesReportMapper dailySalesReportMapper;

    public ResponseDTO listReports(LocalDate from, LocalDate to) {
        try {
            List<DailySalesReport> reports = dailySalesReportRepository
                .findByReportDateBetweenOrderByReportDateDesc(from, to);
            List<DailySalesReportDTO> reportDTOs = dailySalesReportMapper.mappingToListDTO(reports);
            return new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_REPORTS_SUCCESS, reportDTOs);
        } catch (Exception e) {
            throw new CRMException(HttpStatus.INTERNAL_SERVER_ERROR,
                MessageConstants.INTERNAL_ERROR_CODE, MessageConstants.FETCHING_REPORTS_ERROR, e.getMessage());
        }
    }

    public byte[] downloadReport(LocalDate date) {
        DailySalesReport report = dailySalesReportRepository.findByReportDate(date)
            .orElseThrow(() -> new CRMException(HttpStatus.NOT_FOUND, MessageConstants.NOT_FOUND_CODE,
                "No report found for date: " + date));

        if (report.getMongoFileId() == null) {
            throw new CRMException(HttpStatus.NOT_FOUND, MessageConstants.NOT_FOUND_CODE,
                "Report file not available for date: " + date);
        }

        try {
            String collectionName = CollectionType.SALES_REPORT.getName();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            fileStorageService.downloadFile(collectionName, report.getMongoFileId(), outputStream);
            return outputStream.toByteArray();
        } catch (CRMException e) {
            throw e;
        } catch (Exception e) {
            throw new CRMException(HttpStatus.INTERNAL_SERVER_ERROR,
                MessageConstants.INTERNAL_ERROR_CODE, MessageConstants.DOWNLOADING_REPORT_ERROR, e.getMessage());
        }
    }
}
