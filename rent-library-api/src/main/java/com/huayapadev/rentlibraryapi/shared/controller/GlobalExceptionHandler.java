package com.huayapadev.rentlibraryapi.shared.controller;

import com.huayapadev.rentlibraryapi.shared.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {
    @Autowired
    private Environment environment;
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> dataIntegrity(DataIntegrityViolationException ex) {
        ProblemDetail errorBody = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            ex.getMessage()
        );
        errorBody.setTitle("Conflictos de datos");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> globalException(Exception ex) throws Exception {
        if (ex instanceof AuthenticationException || ex instanceof AccessDeniedException) throw ex;
        boolean isDev = environment.acceptsProfiles(Profiles.of("dev"));
        if(isDev) {
            log.error("Excepción no controlada: {}", ex.getClass().getName(), ex);
        }

        String message = "Ocurrió un error inesperado en el servidor. Por favor, contacta al soporte técnico.";

        ProblemDetail errorBody = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ex.getMessage().isBlank() ? message : ex.getMessage()
        );

        errorBody.setTitle("Error Interno del servidor");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
    }
}
