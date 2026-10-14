package com.huayapadev.rentlibraryapi.shared.controller;

import com.huayapadev.rentlibraryapi.shared.validation.EnumExceptionUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ClientExceptionHandler {
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ProblemDetail> handleNumberFormat(NumberFormatException ex) {
        ProblemDetail errorBody = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "El valor no es un numero valido"
        );

        errorBody.setTitle("Formato numérico inválido");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("El parámetro '%s' debe ser de tipo %s y enviaste: '%s'",
            ex.getName(), ex.getRequiredType(), ex.getValue());

        ProblemDetail errorBody = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            message
        );

        errorBody.setTitle("Solicitud Incorrecta");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> noResourceFound(NoResourceFoundException ex) {
        String message = String.format("La ruta '%s' no existe en este servidor.", ex.getResourcePath());

        ProblemDetail errorBody = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            message
        );

        errorBody.setTitle("Ruta No Encontrada");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody);
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        ProblemDetail errorBody = ProblemDetail.forStatusAndDetail(
            HttpStatus.METHOD_NOT_ALLOWED,
            "Método no soportado"
        );

        errorBody.setTitle("Método sin soporte");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorBody);
    }

    // NOTE. Manejar errores de campos @Query o @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> methodArgumentNotValid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(err -> resolveErrorMessage(err, ex.getBindingResult().getFieldType(err.getField())))
            .collect(Collectors.joining(", "));

        ProblemDetail errorBody = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            message
        );

        errorBody.setTitle("Error de Validación");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
    }

    // NOTE. Manejar errores JSON o tipo de dato incompatible
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> invalidHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        ProblemDetail errorBody = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "solicitud del body invalido o ausente"
        );

        if(cause instanceof InvalidFormatException invalidFormat && invalidFormat.getTargetType().isEnum()) {
            Class<?> enumType = invalidFormat.getTargetType();
            String message = EnumExceptionUtils.buildInvalidEnumMessage(enumType, enumType.getSimpleName());
            errorBody.setDetail(message);
        }

        errorBody.setTitle("Estado invalido");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
    }

    private String resolveErrorMessage(FieldError err, Class<?> typeField) {
        if(!err.isBindingFailure() || err.getRejectedValue() == null) {
            return err.getDefaultMessage();
        }

        if(typeField != null && EnumExceptionUtils.isEnum(typeField.getName())) {
            return EnumExceptionUtils.buildInvalidEnumMessage(typeField, err.getField());
        }

        return String.format("%s no acepta este formato: %s", err.getField(), err.getRejectedValue());
    }
}
