package com.tdbang.crm.dtos;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long pk;
    private String name;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Boolean isManager;
    private Boolean isActive;
    private Date createdTime;
}
