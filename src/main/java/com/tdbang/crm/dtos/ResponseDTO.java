package com.tdbang.crm.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {
    private int status;
    private String msg;
    private Object data;

    public ResponseDTO(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}
