package com.courtmaster.api.exception; // Asegúrate de que está en tu paquete de excepciones

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarNotFound(ResourceNotFoundException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", HttpStatus.NOT_FOUND.value()); // 404
        respuesta.put("error", "Not Found");
        respuesta.put("mensaje", ex.getMessage());
        return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> manejarBadRequest(BadRequestException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", HttpStatus.BAD_REQUEST.value()); // 400
        respuesta.put("error", "Bad Request");
        respuesta.put("mensaje", ex.getMessage());
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> manejarConflict(ConflictException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", HttpStatus.CONFLICT.value()); // 409
        respuesta.put("error", "Conflicto con regla de negocio");
        respuesta.put("mensaje", ex.getMessage());
        return new ResponseEntity<>(respuesta, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarGlobal(Exception ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()); // 500
        respuesta.put("error", "Error interno del servidor");
        respuesta.put("mensaje", "Ocurrió un error inesperado: " + ex.getMessage());
        return new ResponseEntity<>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}