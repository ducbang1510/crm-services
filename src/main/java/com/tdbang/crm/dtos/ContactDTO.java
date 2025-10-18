/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.dtos;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactDTO {
    private Long pk;
    @NotBlank(message = "Contact Name is required")
    private String contactName;
    @NotBlank(message = "Salutation is required")
    private String salutation;
    private String mobilePhone;
    private String email;
    @NotBlank(message = "Organization is required")
    private String organization;
    private Date dob;
    private String leadSrc;

    @NotBlank(message = "Assign To is required")
    private String assignedTo;
    private Long assignedToUserFk;

    private String creator;
    private Long creatorFk;

    private String address;
    private String description;
    private Date createdTime;
    private Date updatedTime;
}
