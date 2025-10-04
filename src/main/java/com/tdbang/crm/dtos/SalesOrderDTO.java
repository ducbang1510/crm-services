package com.tdbang.crm.dtos;

import java.math.BigDecimal;
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
public class SalesOrderDTO {
    private Long pk;
    private String subject;
    private String contactName;
    private String status;
    private BigDecimal total;
    private String assignedTo;
    private String creator;
    private String description;
    private Date createdTime;
    private Date updatedTime;
}
