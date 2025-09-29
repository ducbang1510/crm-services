package com.tdbang.crm.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long pk;

    @NotBlank
    private String name;

    @NotBlank
    private String username;

    @NotBlank
    @JsonIgnore
    private String password;

    @Email
    private String email;

    @NotBlank
    private String phone;

    private Boolean isAdmin;

    private Boolean isActive;

    private Date createdTime;

    public UserDTO(Long pk, String name, String email, String phone, Boolean isAdmin, Boolean isActive, Date createdTime) {
        this.pk = pk;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.isAdmin = isAdmin;
        this.isActive = isActive;
        this.createdTime = createdTime;
    }
}
