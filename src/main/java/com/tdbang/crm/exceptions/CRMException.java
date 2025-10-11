/*
 * Copyright © 2022 ICON Clinical Research Ltd.
 * All rights reserved.
 */

package com.tdbang.crm.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Generic Exception for CRM Application.
 */
@Setter
@Getter
public class CRMException extends RuntimeException {

    private HttpStatus status;
    private String errorCode;
    private Object details;

    public CRMException(HttpStatus status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public CRMException(HttpStatus status, String errorCode, String message, Object details) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
        this.details = details;
    }

}
