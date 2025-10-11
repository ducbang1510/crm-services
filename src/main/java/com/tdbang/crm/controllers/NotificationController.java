package com.tdbang.crm.controllers;

import java.util.List;

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

import com.tdbang.crm.dtos.NotificationMessageDTO;
import com.tdbang.crm.services.NotificationService;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController extends BaseController {
    private final NotificationService notificationService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public MappingJacksonValue retrieveNotifications(@RequestParam(defaultValue = "0") int pageNumber,
                                                     @RequestParam(defaultValue = "10") int pageSize) {
        log.info("Into retrieveNotifications");
        Long userPk = getPkUserLogged();

        List<NotificationMessageDTO> result = notificationService.retrieveNotifications(userPk, pageNumber, pageSize);
        MappingJacksonValue mapping = new MappingJacksonValue(result);
        log.info("End retrieveNotifications");

        return mapping;
    }
}
