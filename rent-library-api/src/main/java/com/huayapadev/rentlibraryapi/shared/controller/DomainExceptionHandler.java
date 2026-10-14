package com.huayapadev.rentlibraryapi.shared.controller;

import com.huayapadev.rentlibraryapi.shared.exception.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DomainExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public  ResponseEntity<ProblemDetail> resourceNotFound(ResourceNotFoundException ex) {
        ProblemDetail errorBody = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );

        errorBody.setTitle("Recurso no encontrado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody);
    }

    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<ProblemDetail> invalidState(InvalidStateException ex) {
        ProblemDetail errorBody = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            ex.getMessage()
        );

        errorBody.setTitle("Estado Invalido");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> alReadyExists(AlreadyExistsException ex) {
        ProblemDetail errorBody = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            ex.getMessage()
        );

        errorBody.setTitle("Recurso duplicado");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody);
    }
}
