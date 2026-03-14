/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.controllers;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.services.ReportService;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/report")
@Tag(name = "CRM Report APIs")
public class ReportController extends BaseController {

    private final ReportService reportService;

    @GetMapping("/daily-sales")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public MappingJacksonValue listReports(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        log.info("Start listReports from={} to={}", from, to);
        ResponseDTO responseDTO = reportService.listReports(from, to);
        log.info("End listReports");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/daily-sales/download")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<byte[]> downloadReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Start downloadReport date={}", date);
        byte[] reportBytes = reportService.downloadReport(date);
        String fileName = "daily_sales_report_" + date + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(reportBytes.length);

        log.info("End downloadReport");
        return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);
    }
}
