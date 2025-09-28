/*
 * Copyright © 2022 ICON Clinical Research Ltd.
 * All rights reserved.
 */

package com.tdbang.crm.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Generic Exception for Employee Portal Application.
 */
public class GenericException extends RuntimeException {

    private HttpStatus status;
    private String errorCode;

    public GenericException(HttpStatus status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
    
}
