package com.example.autofixpro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para health checks y monitoreo de la aplicación
 * Usado por AWS Load Balancer para verificar el estado de la aplicación
 */
@RestController
public class HealthController extends BaseController {

    @Autowired
    private DataSource dataSource;

    /**
     * Endpoint básico de health check
     * Usado por AWS ALB Target Group
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("application", "AutoFixPro");
        status.put("version", "1.0.0");
        status.put("timestamp", java.time.LocalDateTime.now());

        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    /**
     * Health check detallado que incluye verificación de base de datos
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        Map<String, Object> checks = new HashMap<>();

        // Verificar aplicación
        checks.put("application", Map.of(
            "status", "UP",
            "message", "Application is running"
        ));

        // Verificar base de datos
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                checks.put("database", Map.of(
                    "status", "UP",
                    "message", "Database connection is healthy"
                ));
            } else {
                checks.put("database", Map.of(
                    "status", "DOWN",
                    "message", "Database connection is invalid"
                ));
            }
        } catch (SQLException e) {
            checks.put("database", Map.of(
                "status", "DOWN",
                "message", "Database connection failed: " + e.getMessage()
            ));
        }

        // Determinar estado general
        boolean allUp = checks.values().stream()
            .allMatch(check -> "UP".equals(((Map<?, ?>) check).get("status")));

        health.put("status", allUp ? "UP" : "DOWN");
        health.put("checks", checks);
        health.put("timestamp", java.time.LocalDateTime.now());

        HttpStatus responseStatus = allUp ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return new ResponseEntity<>(health, responseStatus);
    }

    /**
     * Endpoint de información de la aplicación
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", Map.of(
            "name", "AutoFixPro",
            "description", "Sistema de Gestión de Taller Mecánico",
            "version", "1.0.0",
            "environment", System.getProperty("spring.profiles.active", "default")
        ));

        info.put("build", Map.of(
            "java.version", System.getProperty("java.version"),
            "spring.version", org.springframework.core.SpringVersion.getVersion()
        ));

        return createSuccessResponse(info, "Application information retrieved successfully");
    }
}