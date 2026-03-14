/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.dtos.TaskDTO;
import com.tdbang.crm.entities.Task;
import com.tdbang.crm.entities.User;
import com.tdbang.crm.enums.TaskPriority;
import com.tdbang.crm.enums.TaskStatus;
import com.tdbang.crm.enums.TaskType;
import com.tdbang.crm.exceptions.CRMException;
import com.tdbang.crm.mappers.TaskMapper;
import com.tdbang.crm.repositories.TaskRepository;
import com.tdbang.crm.repositories.UserRepository;
import com.tdbang.crm.utils.MessageConstants;

@Log4j2
@Service
public class TaskService {

    private static final Set<String> VALID_ENTITY_TYPES = Set.of("CONTACT", "SALES_ORDER");

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskMapper taskMapper;

    @Transactional
    public ResponseDTO createTask(TaskDTO dto, Long creatorPk) {
        try {
            TaskType taskType = TaskType.fromName(dto.getTaskType());
            TaskStatus status = dto.getStatus() != null ? TaskStatus.fromName(dto.getStatus()) : TaskStatus.OPEN;
            TaskPriority priority = dto.getPriority() != null ? TaskPriority.fromName(dto.getPriority()) : TaskPriority.MEDIUM;

            if (dto.getEntityType() != null) {
                validateEntityType(dto.getEntityType());
            }

            User assignedTo = userRepository.findUserByPk(dto.getAssignedToUserFk());
            if (assignedTo == null) {
                throw new CRMException(HttpStatus.NOT_FOUND, MessageConstants.NOT_FOUND_CODE, "Assigned user not found");
            }

            User creator = userRepository.findUserByPk(creatorPk);

            Task task = new Task();
            task.setTitle(dto.getTitle());
            task.setTaskType(taskType);
            task.setEntityType(dto.getEntityType() != null ? dto.getEntityType().toUpperCase() : null);
            task.setEntityFk(dto.getEntityFk());
            task.setStatus(status);
            task.setPriority(priority);
            task.setDueDate(dto.getDueDate());
            task.setDescription(dto.getDescription());
            task.setAssignedTo(assignedTo);
            task.setCreatedBy(creator);

            Task savedTask = taskRepository.save(task);
            TaskDTO resultDto = taskMapper.mappingTaskEntityToTaskDTO(savedTask);

            return new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.CREATING_TASK_SUCCESS, resultDto);
        } catch (CRMException e) {
            throw e;
        } catch (Exception e) {
            throw new CRMException(HttpStatus.BAD_REQUEST, MessageConstants.BAD_REQUEST_CODE, MessageConstants.CREATING_TASK_ERROR, e.getMessage());
        }
    }

    public ResponseDTO getTaskDetails(Long pk) {
        Task task = taskRepository.findByPk(pk)
            .orElseThrow(() -> new CRMException(HttpStatus.NOT_FOUND, MessageConstants.NOT_FOUND_CODE, MessageConstants.NOT_FOUND_MESSAGE));

        TaskDTO dto = taskMapper.mappingTaskEntityToTaskDTO(task);
        return new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_TASK_SUCCESS, dto);
    }

    @Transactional
    public ResponseDTO updateTask(Long pk, TaskDTO dto, Long updaterPk) {
        Task task = taskRepository.findByPk(pk)
            .orElseThrow(() -> new CRMException(HttpStatus.NOT_FOUND, MessageConstants.NOT_FOUND_CODE, MessageConstants.NOT_FOUND_MESSAGE));

        try {
            if (dto.getTitle() != null) {
                task.setTitle(dto.getTitle());
            }
            if (dto.getTaskType() != null) {
                task.setTaskType(TaskType.fromName(dto.getTaskType()));
            }
            if (dto.getStatus() != null) {
                task.setStatus(TaskStatus.fromName(dto.getStatus()));
            }
            if (dto.getPriority() != null) {
                task.setPriority(TaskPriority.fromName(dto.getPriority()));
            }
            if (dto.getDueDate() != null) {
                task.setDueDate(dto.getDueDate());
            }
            if (dto.getDescription() != null) {
                task.setDescription(dto.getDescription());
            }
            if (dto.getAssignedToUserFk() != null) {
                User assignedTo = userRepository.findUserByPk(dto.getAssignedToUserFk());
                if (assignedTo == null) {
                    throw new CRMException(HttpStatus.NOT_FOUND, MessageConstants.NOT_FOUND_CODE, "Assigned user not found");
                }
                task.setAssignedTo(assignedTo);
            }

            Task updatedTask = taskRepository.save(task);
            TaskDTO resultDto = taskMapper.mappingTaskEntityToTaskDTO(updatedTask);

            return new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.UPDATING_TASK_SUCCESS, resultDto);
        } catch (CRMException e) {
            throw e;
        } catch (Exception e) {
            throw new CRMException(HttpStatus.BAD_REQUEST, MessageConstants.BAD_REQUEST_CODE, MessageConstants.UPDATING_TASK_ERROR, e.getMessage());
        }
    }

    @Transactional
    public ResponseDTO deleteTask(Long pk, Long deleterPk) {
        Task task = taskRepository.findByPk(pk)
            .orElseThrow(() -> new CRMException(HttpStatus.NOT_FOUND, MessageConstants.NOT_FOUND_CODE, MessageConstants.NOT_FOUND_MESSAGE));

        taskRepository.delete(task);
        return new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.DELETING_TASK_SUCCESS);
    }

    public ResponseDTO listMyTasks(Long userPk, int pageSize, int pageNumber) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Task> taskPage = taskRepository.findByAssignedToPkOrderByDueDateAsc(userPk, pageable);
            List<TaskDTO> taskDTOs = taskMapper.mappingToListTaskDTO(taskPage.getContent());

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("tasks", taskDTOs);
            resultMap.put("totalElements", taskPage.getTotalElements());
            resultMap.put("totalPages", taskPage.getTotalPages());
            resultMap.put("currentPage", taskPage.getNumber());

            return new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_TASKS_SUCCESS, resultMap);
        } catch (Exception e) {
            throw new CRMException(HttpStatus.INTERNAL_SERVER_ERROR,
                MessageConstants.INTERNAL_ERROR_CODE, MessageConstants.FETCHING_LIST_OF_TASKS_ERROR, e.getMessage());
        }
    }

    public ResponseDTO listTasksByEntity(String entityType, Long entityFk) {
        try {
            validateEntityType(entityType);
            List<Task> tasks = taskRepository.findByEntityTypeAndEntityFkOrderByCreatedOnDesc(entityType.toUpperCase(), entityFk);
            List<TaskDTO> taskDTOs = taskMapper.mappingToListTaskDTO(tasks);
            return new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_TASKS_SUCCESS, taskDTOs);
        } catch (CRMException e) {
            throw e;
        } catch (Exception e) {
            throw new CRMException(HttpStatus.INTERNAL_SERVER_ERROR,
                MessageConstants.INTERNAL_ERROR_CODE, MessageConstants.FETCHING_LIST_OF_TASKS_ERROR, e.getMessage());
        }
    }

    public ResponseDTO getTaskSummary(Long userPk) {
        try {
            long openCount = taskRepository.countByAssignedToPkAndStatus(userPk, TaskStatus.OPEN);
            long inProgressCount = taskRepository.countByAssignedToPkAndStatus(userPk, TaskStatus.IN_PROGRESS);
            long doneCount = taskRepository.countByAssignedToPkAndStatus(userPk, TaskStatus.DONE);

            Map<String, Long> summary = new HashMap<>();
            summary.put("open", openCount);
            summary.put("inProgress", inProgressCount);
            summary.put("done", doneCount);

            return new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_TASK_SUMMARY_SUCCESS, summary);
        } catch (Exception e) {
            throw new CRMException(HttpStatus.INTERNAL_SERVER_ERROR,
                MessageConstants.INTERNAL_ERROR_CODE, MessageConstants.FETCHING_TASK_SUMMARY_ERROR, e.getMessage());
        }
    }

    private void validateEntityType(String entityType) {
        if (entityType == null || !VALID_ENTITY_TYPES.contains(entityType.toUpperCase())) {
            throw new CRMException(HttpStatus.BAD_REQUEST, MessageConstants.BAD_REQUEST_CODE,
                "Invalid entity type. Must be one of: " + VALID_ENTITY_TYPES);
        }
    }
}
