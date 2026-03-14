/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.dtos;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDTO {
    private Long pk;
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Task Type is required")
    private String taskType;
    private String entityType;
    private Long entityFk;
    private String status;
    private String priority;
    private Date dueDate;
    private String description;
    private String assignedTo;
    @NotNull(message = "Assigned To User FK is required")
    private Long assignedToUserFk;
    private String createdBy;
    private Long createdByFk;
    private Date createdOn;
    private Date updatedOn;
}
