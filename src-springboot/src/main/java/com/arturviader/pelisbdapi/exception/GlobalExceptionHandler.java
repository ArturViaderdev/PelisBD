package com.arturviader.pelisbdapi.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({EmailNotConfirmed.class})
    public ResponseEntity<ErrorResponse> unauthorized(RuntimeException ex, WebRequest request){
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ErrorResponse body = new ErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), path);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler({UserNotFound.class, TokenNotFoundException.class, TokenExpiredException.class})
    public ResponseEntity<ErrorResponse> notFound(RuntimeException ex, WebRequest request){
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ErrorResponse body = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), path);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler({AlreadyRegistreredEmail.class, AlreadyRegistreredUserName.class})
    public ResponseEntity<ErrorResponse> conflict(RuntimeException ex, WebRequest request){
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ErrorResponse body = new ErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), path);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, IncorrectTimeWidow.class})
    public ResponseEntity<ErrorResponse> badRequest(MethodArgumentNotValidException ex, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Petición inválida");

        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST, message, path);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        String message = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .findFirst()
                .orElse("Petición inválida");

        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST, message, path);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
