/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.controllers;

import java.util.Arrays;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tdbang.crm.commons.AuditAction;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.dtos.TaskDTO;
import com.tdbang.crm.enums.TaskPriority;
import com.tdbang.crm.enums.TaskStatus;
import com.tdbang.crm.enums.TaskType;
import com.tdbang.crm.services.TaskService;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/task")
@Tag(name = "CRM Task APIs")
public class TaskController extends BaseController {

    private final TaskService taskService;

    @PostMapping("")
    @AuditAction(value = "CREATE_TASK", description = "Create new task")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public MappingJacksonValue createTask(@RequestBody @Valid TaskDTO taskDTO) {
        log.info("Start createTask");
        ResponseDTO responseDTO = taskService.createTask(taskDTO, getPkUserLogged());
        log.info("End createTask");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public MappingJacksonValue getTaskDetails(@PathVariable Long id) {
        log.info("Start getTaskDetails {}", id);
        ResponseDTO responseDTO = taskService.getTaskDetails(id);
        log.info("End getTaskDetails");
        return new MappingJacksonValue(responseDTO);
    }

    @PutMapping("/{id}")
    @AuditAction(value = "UPDATE_TASK", description = "Update existing task")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public MappingJacksonValue updateTask(@PathVariable Long id, @RequestBody @Valid TaskDTO taskDTO) {
        log.info("Start updateTask {}", id);
        ResponseDTO responseDTO = taskService.updateTask(id, taskDTO, getPkUserLogged());
        log.info("End updateTask");
        return new MappingJacksonValue(responseDTO);
    }

    @DeleteMapping("/{id}")
    @AuditAction(value = "DELETE_TASK", description = "Delete existing task")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public MappingJacksonValue deleteTask(@PathVariable Long id) {
        log.info("Start deleteTask {}", id);
        ResponseDTO responseDTO = taskService.deleteTask(id, getPkUserLogged());
        log.info("End deleteTask");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public MappingJacksonValue listMyTasks(@RequestParam int pageNumber, @RequestParam int pageSize) {
        log.info("Start listMyTasks");
        ResponseDTO responseDTO = taskService.listMyTasks(getPkUserLogged(), pageSize, pageNumber);
        log.info("End listMyTasks");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public MappingJacksonValue listTasksByEntity(@RequestParam String entityType, @RequestParam Long entityFk) {
        log.info("Start listTasksByEntity");
        ResponseDTO responseDTO = taskService.listTasksByEntity(entityType, entityFk);
        log.info("End listTasksByEntity");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/summary")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public MappingJacksonValue getTaskSummary() {
        log.info("Start getTaskSummary");
        ResponseDTO responseDTO = taskService.getTaskSummary(getPkUserLogged());
        log.info("End getTaskSummary");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/type")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public MappingJacksonValue listTaskTypes() {
        log.info("Start listTaskTypes");
        List<TaskType> taskTypes = Arrays.stream(TaskType.values()).toList();
        log.info("End listTaskTypes");
        return new MappingJacksonValue(taskTypes);
    }

    @GetMapping("/status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public MappingJacksonValue listTaskStatuses() {
        log.info("Start listTaskStatuses");
        List<TaskStatus> taskStatuses = Arrays.stream(TaskStatus.values()).toList();
        log.info("End listTaskStatuses");
        return new MappingJacksonValue(taskStatuses);
    }

    @GetMapping("/priority")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public MappingJacksonValue listTaskPriorities() {
        log.info("Start listTaskPriorities");
        List<TaskPriority> taskPriorities = Arrays.stream(TaskPriority.values()).toList();
        log.info("End listTaskPriorities");
        return new MappingJacksonValue(taskPriorities);
    }
}
