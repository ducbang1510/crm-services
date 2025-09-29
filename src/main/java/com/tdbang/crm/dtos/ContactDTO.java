package com.tdbang.crm.dtos;

import lombok.Getter;
import lombok.Setter;

import com.tdbang.crm.entities.User;
import com.tdbang.crm.enums.LeadSource;
import com.tdbang.crm.enums.Salutation;

@Getter
@Setter
public class ContactDTO {
    private Long pk;
    private String contactName;
    private Salutation salutation;
    private String mobilePhone;
    private String email;
    private String organization;
    private LeadSource leadSrc;
    private User assignedTo;
    private User creator;
    private String address;
    private String description;
}
