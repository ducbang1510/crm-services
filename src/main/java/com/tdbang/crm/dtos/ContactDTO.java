package com.tdbang.crm.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.tdbang.crm.entities.User;
import com.tdbang.crm.enums.LeadSource;
import com.tdbang.crm.enums.Salutation;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactDTO {
    private Long pk;
    private String contactName;
    private Salutation salutation;
    private String mobilePhone;
    private String email;
    private String organization;
    private LeadSource leadSrc;
    private String assignedTo;
    private Long assignedToUserFk;
    private User creator;
    private String address;
    private String description;

    public ContactDTO(Long pk, String contactName, Salutation salutation, String mobilePhone, String email,
                      String organization, LeadSource leadSrc, String assignedTo, User creator, String address, String description) {
        this.pk = pk;
        this.contactName = contactName;
        this.salutation = salutation;
        this.mobilePhone = mobilePhone;
        this.email = email;
        this.organization = organization;
        this.leadSrc = leadSrc;
        this.assignedTo = assignedTo;
        this.creator = creator;
        this.address = address;
        this.description = description;
    }
}
