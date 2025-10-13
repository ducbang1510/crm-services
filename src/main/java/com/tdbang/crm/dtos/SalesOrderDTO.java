package com.tdbang.crm.dtos;

import java.math.BigDecimal;
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
public class SalesOrderDTO {
    private Long pk;
    @NotBlank(message = "Subject is required")
    private String subject;
    @NotBlank(message = "Contact Name is required")
    private String contactName;
    private Long contactFk;
    @NotBlank(message = "Status is required")
    private String status;
    @NotNull(message = "Total is required")
    private BigDecimal total;
    @NotBlank(message = "Assigned To is required")
    private String assignedTo;
    private Long assignedToUserFk;
    private String creator;
    private Long creatorFk;
    private String description;
    private Date createdTime;
    private Date updatedTime;
}
