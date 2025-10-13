package com.tdbang.crm.dtos;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonFilter("UserDTOFilter")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private Long pk;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "First Name is required")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    private String lastName;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    private Boolean isAdmin;

    private Boolean isStaff;

    private Boolean isActive;

    private Date createdTime;

    public UserDTO(Long pk, String name, String email, String phone, Boolean isAdmin, Boolean isStaff, Boolean isActive, Date createdTime) {
        this.pk = pk;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.isAdmin = isAdmin;
        this.isActive = isActive;
        this.createdTime = createdTime;
    }
}
