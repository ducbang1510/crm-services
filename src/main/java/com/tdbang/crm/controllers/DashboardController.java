/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.services.DashboardService;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "CRM Dashboard APIs")
public class DashboardController extends BaseController {

    private final DashboardService dashboardService;

    @GetMapping("/revenue-trend")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public MappingJacksonValue getRevenueTrend(@RequestParam(defaultValue = "12") int months) {
        log.info("Start getRevenueTrend months={}", months);
        ResponseDTO responseDTO = dashboardService.getRevenueTrend(months);
        log.info("End getRevenueTrend");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/pipeline-summary")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public MappingJacksonValue getPipelineSummary() {
        log.info("Start getPipelineSummary");
        ResponseDTO responseDTO = dashboardService.getPipelineSummary();
        log.info("End getPipelineSummary");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/top-users")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public MappingJacksonValue getTopUsers(@RequestParam(defaultValue = "5") int limit) {
        log.info("Start getTopUsers limit={}", limit);
        ResponseDTO responseDTO = dashboardService.getTopUsers(limit);
        log.info("End getTopUsers");
        return new MappingJacksonValue(responseDTO);
    }
}
