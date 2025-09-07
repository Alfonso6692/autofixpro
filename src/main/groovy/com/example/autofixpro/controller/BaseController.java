package com.example.autofixpro.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseController {

    protected ResponseEntity<Map<String, Object>> createResponse(Object data, String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("message", message);
        response.put("status", status.value());
        response.put("timestamp", java.time.LocalDateTime.now());

        return new ResponseEntity<>(response, status);
    }

    protected ResponseEntity<Map<String, Object>> createSuccessResponse(Object data, String message) {
        return createResponse(data, message, HttpStatus.OK);
    }

    protected ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        return createResponse(null, message, status);
    }
}