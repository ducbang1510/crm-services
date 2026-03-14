/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.tdbang.crm.dtos.TaskDTO;
import com.tdbang.crm.entities.Task;

@Component
public class TaskMapper {

    public TaskDTO mappingTaskEntityToTaskDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setPk(task.getPk());
        dto.setTitle(task.getTitle());
        dto.setTaskType(task.getTaskType().getName());
        dto.setEntityType(task.getEntityType());
        dto.setEntityFk(task.getEntityFk());
        dto.setStatus(task.getStatus().getName());
        dto.setPriority(task.getPriority().getName());
        dto.setDueDate(task.getDueDate());
        dto.setDescription(task.getDescription());
        dto.setAssignedTo(task.getAssignedTo().getName());
        dto.setAssignedToUserFk(task.getAssignedTo().getPk());
        dto.setCreatedBy(task.getCreatedBy().getName());
        dto.setCreatedByFk(task.getCreatedBy().getPk());
        dto.setCreatedOn(task.getCreatedOn());
        dto.setUpdatedOn(task.getUpdatedOn());
        return dto;
    }

    public List<TaskDTO> mappingToListTaskDTO(List<Task> tasks) {
        return tasks.stream().map(this::mappingTaskEntityToTaskDTO).toList();
    }
}
