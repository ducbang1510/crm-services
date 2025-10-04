package com.tdbang.crm.dtos;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    private String contactName;
    private String salutation;
    private String mobilePhone;
    private String email;
    private String organization;
    private Date dob;
    private String leadSrc;

    private String assignedTo;
    private Long assignedToUserFk;

    private String creator;
    private Long creatorFk;

    private String address;
    private String description;
    private Date createdTime;
    private Date updatedTime;
}
