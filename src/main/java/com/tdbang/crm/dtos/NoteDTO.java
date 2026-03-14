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
public class NoteDTO {
    private Long pk;
    @NotBlank(message = "Entity Type is required")
    private String entityType;
    @NotNull(message = "Entity FK is required")
    private Long entityFk;
    @NotBlank(message = "Note Type is required")
    private String noteType;
    @NotBlank(message = "Content is required")
    private String content;
    private String createdByName;
    private Long createdByFk;
    private Date createdOn;
    private Date updatedOn;
}
