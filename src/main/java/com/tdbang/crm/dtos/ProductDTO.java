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
public class ProductDTO {
    private Long pk;
    @NotBlank(message = "Name is required")
    private String name;
    @NotNull(message = "Price is required")
    private BigDecimal price;
    private Boolean isActive;
    private String description;
    private Date createdTime;
    private Date updatedTime;
}
