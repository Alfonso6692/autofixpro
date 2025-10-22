package com.example.autofixpro.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@ControllerAdvice
public class GlobalExceptionHandler implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        String errorMessage = (String) request.getAttribute("jakarta.servlet.error.message");
        String requestUri = (String) request.getAttribute("jakarta.servlet.error.request_uri");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        errorResponse.put("status", statusCode != null ? statusCode : 500);
        errorResponse.put("message", errorMessage != null ? errorMessage : "Error interno del servidor");
        errorResponse.put("path", requestUri);
        errorResponse.put("timestamp", LocalDateTime.now());

        HttpStatus status = statusCode != null ? HttpStatus.valueOf(statusCode) : HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleNotFound(NoHandlerFoundException ex, HttpServletRequest request) {
        // Solo responder JSON si la petición es para API
        if (request.getRequestURI().startsWith("/api/")) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("status", 404);
            errorResponse.put("message", "Endpoint no encontrado: " + ex.getRequestURL());
            errorResponse.put("timestamp", LocalDateTime.now());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        // Para páginas HTML, redirigir al dashboard con mensaje
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public Object handleGenericException(Exception ex, HttpServletRequest request, Model model) {
        // Log del error para debugging
        ex.printStackTrace();

        // Detectar si la petición espera JSON (API) o HTML (vistas)
        String acceptHeader = request.getHeader("Accept");
        boolean expectsJson = request.getRequestURI().startsWith("/api/") ||
                            (acceptHeader != null && acceptHeader.contains("application/json"));

        if (expectsJson) {
            // Respuesta JSON para APIs
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("status", 500);
            errorResponse.put("message", "Error interno: " + ex.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } else {
            // Respuesta HTML para vistas - redirigir al dashboard con mensaje de error
            model.addAttribute("error", "Ha ocurrido un error: " + ex.getMessage());
            model.addAttribute("errorDetails", ex.getClass().getSimpleName());
            return "redirect:/dashboard";
        }
    }
}