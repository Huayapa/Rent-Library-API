package com.huayapadev.rentlibraryapi.auth.controller;

import com.huayapadev.rentlibraryapi.shared.exception.InvalidCredentialsException;
import com.huayapadev.rentlibraryapi.shared.exception.InvalidRefreshTokenException;
import com.huayapadev.rentlibraryapi.shared.exception.UnauthorizedException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityExceptionHandler {
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUsernameNotFound(UsernameNotFoundException ex) {
        ProblemDetail errorBody = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            "Error de Autenticación"
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorBody);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentials(BadCredentialsException ex) {
        ProblemDetail errorBody = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            "Credenciales inválidas"
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorBody);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ProblemDetail> invalidCredentials(InvalidCredentialsException ex) {
        ProblemDetail errorBody = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            ex.getMessage()
        );
        errorBody.setTitle("Credenciales invalidas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorBody);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ProblemDetail> handleUnauthorized(UnauthorizedException ex) {
        String message = "Error de autorización";

        ProblemDetail errorBody = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            ex.getMessage().isBlank() ? message : ex.getMessage()
        );

        errorBody.setTitle("Error de autorización");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorBody);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ProblemDetail> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        String message = "Refresh Token invalido";

        ProblemDetail errorBody = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            ex.getMessage().isBlank() ? message : ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorBody);
    }
}
