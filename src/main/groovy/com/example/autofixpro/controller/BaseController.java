package com.example.autofixpro.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase base abstracta para controladores que proporciona métodos de utilidad
 * para crear respuestas HTTP estandarizadas.
 */
public abstract class BaseController {

    /**
     * Crea una respuesta HTTP genérica con datos, mensaje y estado.
     *
     * @param data    Los datos a incluir en la respuesta (puede ser nulo).
     * @param message Un mensaje descriptivo.
     * @param status  El estado HTTP de la respuesta.
     * @return Un ResponseEntity que contiene el mapa de respuesta.
     */
    protected ResponseEntity<Map<String, Object>> createResponse(Object data, String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("message", message);
        response.put("status", status.value());
        response.put("timestamp", java.time.LocalDateTime.now());

        return new ResponseEntity<>(response, status);
    }

    /**
     * Crea una respuesta HTTP exitosa (200 OK).
     *
     * @param data    Los datos a incluir en la respuesta.
     * @param message Un mensaje descriptivo.
     * @return Un ResponseEntity de éxito.
     */
    protected ResponseEntity<Map<String, Object>> createSuccessResponse(Object data, String message) {
        return createResponse(data, message, HttpStatus.OK);
    }

    /**
     * Crea una respuesta HTTP de error.
     *
     * @param message El mensaje de error.
     * @param status  El estado HTTP del error.
     * @return Un ResponseEntity de error.
     */
    protected ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        return createResponse(null, message, status);
    }
}