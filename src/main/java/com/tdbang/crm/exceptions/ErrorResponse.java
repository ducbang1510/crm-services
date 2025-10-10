package com.tdbang.crm.exceptions;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {

    // Getters & Setters
    private int status;
    private String errorCode;
    private String message;
    private String path; // Request path (optional but helpful)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private Object details; // Can hold validation errors, custom fields, etc.

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String errorCode, String message, String path) {
        this();
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.path = path;
    }

    public ErrorResponse(int status, String errorCode, String message, String path, Object details) {
        this(status, errorCode, message, path);
        this.details = details;
    }

}

