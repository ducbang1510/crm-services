package com.tdbang.crm.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

import com.tdbang.crm.entities.Contact;
import com.tdbang.crm.entities.User;
import com.tdbang.crm.enums.SalesOrderStatus;

@Getter
@Setter
public class SalesOrderDTO {
    private Long pk;
    private String subject;
    private Contact contact;
    private SalesOrderStatus status;
    private BigDecimal total;
    private User assignedTo;
    private User creator;
    private String description;
    private Date createdOn;
    private Date updatedOn;
}
