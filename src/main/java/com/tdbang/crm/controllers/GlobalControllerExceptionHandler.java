/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.controllers;

import java.util.HashMap;
import java.util.Map;

import com.corundumstudio.socketio.handler.SocketIOException;
import com.fasterxml.jackson.core.JacksonException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.tdbang.crm.exceptions.CRMException;
import com.tdbang.crm.exceptions.ErrorResponse;
import com.tdbang.crm.utils.MessageConstants;

/**
 * Exception Handler class for global controller
 */
@Log4j2
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    /**
     * Handle Data Integrity Violation Exception method.
     *
     * @param e       DataIntegrityViolationException
     * @param request HttpServletRequest
     * @return Response Entity
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(),
                MessageConstants.CONFLICT_CODE, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handle Entity Not Found Exception method.
     *
     * @param e       EntityNotFoundException
     * @param request HttpServletRequest
     * @return Response Entity
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                MessageConstants.NOT_FOUND_CODE, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handle IllegalArgumentException Exception method.
     *
     * @param e       IllegalArgumentException
     * @param request HttpServletRequest
     * @return Response Entity
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                MessageConstants.BAD_REQUEST_CODE, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle JacksonException Exception method.
     *
     * @param e       JacksonException
     * @param request HttpServletRequest
     * @return Response Entity
     */
    @ExceptionHandler(JacksonException.class)
    public ResponseEntity<Object> handleJsonException(JacksonException e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                MessageConstants.BAD_REQUEST_CODE, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle AccessDeniedException Exception method.
     *
     * @param e       AccessDeniedException
     * @param request HttpServletRequest
     * @return Response Entity
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.error(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                MessageConstants.FORBIDDEN_CODE, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * Handle Generic Exception method.
     *
     * @param e       Exception
     * @param request HttpServletRequest
     * @return Response Entity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                MessageConstants.INTERNAL_ERROR_CODE, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handle CRMException Exception method.
     *
     * @param e       Exception
     * @param request HttpServletRequest
     * @return Response Entity
     */
    @ExceptionHandler(CRMException.class)
    public ResponseEntity<ErrorResponse> handleCRMException(CRMException e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(e.getStatus().value(),
                e.getErrorCode(), e.getMessage(), request.getRequestURI(), e.getDetails());
        return ResponseEntity.status(e.getStatus()).body(errorResponse);
    }

    /**
     * Handle Entity Http Client Error Exception method.
     *
     * @param e       Exception
     * @param request HttpServletRequest
     * @return Response Entity
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientErrorException(HttpClientErrorException e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(e.getStatusCode().value(),
                MessageConstants.ERROR_CODE, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
    }

    /**
     * Handle Method Argument Not Valid Exception method.
     *
     * @param ex      Exception
     * @param request HttpServletRequest
     * @return Response Entity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                error -> validationErrors.put(error.getField(), error.getDefaultMessage())
        );
        log.error("Validation failed: {}", validationErrors.toString());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                MessageConstants.VALIDATION_FAILED_CODE,
                MessageConstants.VALIDATION_FAILED_MESSAGE,
                request.getRequestURI(),
                validationErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle SocketIOException Exception method.
     *
     * @param ex      Exception
     * @param request HttpServletRequest
     * @return Response Entity
     */
    @ExceptionHandler(SocketIOException.class)
    public ResponseEntity<Object> handleSocketIOException(SocketIOException ex, WebRequest request) {
        log.error("Socket IO error: {}", ex.getMessage());
        return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Handle Entity Not Found Exception method.
     *
     * @param e       EntityNotFoundException
     * @param request HttpServletRequest
     * @return Response Entity
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(NoResourceFoundException e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                MessageConstants.NOT_FOUND_CODE, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

}
 
