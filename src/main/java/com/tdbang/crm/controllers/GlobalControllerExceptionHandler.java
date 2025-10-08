package com.tdbang.crm.controllers;

import com.corundumstudio.socketio.handler.SocketIOException;
import com.fasterxml.jackson.core.JacksonException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.tdbang.crm.exceptions.GenericException;
import com.tdbang.crm.utils.AppConstants;

/**
 * Exception Handler class for global controller
 */
@Log4j2
@ControllerAdvice
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle Data Integrity Violation Exception method.
     *
     * @param e       DataIntegrityViolationException
     * @param request WebRequest
     * @return Response Entity
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(
            DataIntegrityViolationException e, WebRequest request) {

        log.error(e.getMessage(), e);
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    /**
     * Handle Entity Not Found Exception method.
     *
     * @param e       EntityNotFoundException
     * @param request WebRequest
     * @return Response Entity
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(
            EntityNotFoundException e, WebRequest request) {

        log.error(e.getMessage(), e);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Handle IllegalArgumentException Exception method.
     *
     * @param e       IllegalArgumentException
     * @param request WebRequest
     * @return Response Entity
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(
            IllegalArgumentException e, WebRequest request) {

        log.error(e.getMessage(), e);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle JacksonException Exception method.
     *
     * @param e       JacksonException
     * @param request WebRequest
     * @return Response Entity
     */
    @ExceptionHandler(JacksonException.class)
    public ResponseEntity<Object> handleJsonException(
            JacksonException e, WebRequest request) {

        log.error(e.getMessage(), e);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle AccessDeniedException Exception method.
     *
     * @param e       AccessDeniedException
     * @param request WebRequest
     * @return Response Entity
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(
            AccessDeniedException e, WebRequest request) {

        log.error(e.getMessage());
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /**
     * Handle Entity Generic Exception method.
     *
     * @param e       Exception
     * @param request WebRequest
     * @return Response Entity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(
            Exception e, WebRequest request) {

        log.error(e.getMessage(), e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle Entity Generic Exception method.
     *
     * @param e       Exception
     * @param request WebRequest
     * @return Response Entity
     */
    @ExceptionHandler(GenericException.class)
    public ResponseEntity handleGenericException(
            GenericException e, WebRequest request) {
        log.error(e.getMessage(), e);
        MultiValueMap headers = new LinkedMultiValueMap();
        headers.add(AppConstants.ERROR_CODE_HEADER, e.getErrorCode());
        return new ResponseEntity(headers, e.getStatus());
    }

    /**
     * Handle Entity Http Client Error Exception method.
     *
     * @param e       Exception
     * @param request WebRequest
     * @return Response Entity
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity handleHttpClientErrorException(
            HttpClientErrorException e, WebRequest request) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(e.getStatusCode());
    }

    /**
     * Handle Method Argument Not Valid Exception method.
     *
     * @param ex      Exception
     * @param headers Headers
     * @param status  HttpStatus
     * @param request WebRequest
     * @return Response Entity
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        StringBuilder errorMessage = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errorMessage.append(error.getDefaultMessage()).append(";");
        }
        headers.add(AppConstants.ERROR_CODE_HEADER, errorMessage.toString());
        log.error("Validation failed: {}", errorMessage);

        return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle SocketIOException Exception method.
     *
     * @param ex      Exception
     * @param request WebRequest
     * @return Response Entity
     */
    @ExceptionHandler(SocketIOException.class)
    public ResponseEntity<Object> handleSocketIOException(SocketIOException ex, WebRequest request) {
        log.error("Socket IO error: {}", ex.getMessage());
        return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }

}
 
