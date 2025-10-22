package com.example.autofixpro.controller;

import com.example.autofixpro.service.AwsSnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Controlador REST para interactuar con el servicio AWS SNS (Simple Notification Service).
 * Proporciona endpoints para enviar SMS, crear y publicar en topics, y suscribir correos electrónicos.
 */
@RestController
@RequestMapping("/api/sns")
@CrossOrigin(origins = "*")
public class SnsController {

    @Autowired
    private AwsSnsService awsSnsService;

    /**
     * Obtiene el estado actual del servicio SNS.
     * @return ResponseEntity con el estado del servicio.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> obtenerEstadoSns() {
        Map<String, Object> status = new HashMap<>();
        status.put("snsEnabled", awsSnsService.esSnsHabilitado());
        status.put("topicArn", awsSnsService.obtenerTopicArn());
        status.put("service", "AWS SNS AutoFixPro");
        return ResponseEntity.ok(status);
    }

    /**
     * Envía un mensaje SMS a un número de teléfono.
     * @param request Un mapa que contiene el "telefono" y el "mensaje".
     * @return ResponseEntity con el ID del mensaje si es exitoso, o un error.
     */
    @PostMapping("/sms")
    public ResponseEntity<Map<String, String>> enviarSms(@RequestBody Map<String, String> request) {
        String telefono = request.get("telefono");
        String mensaje = request.get("mensaje");

        if (telefono == null || mensaje == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Teléfono y mensaje son requeridos");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            CompletableFuture<String> future = awsSnsService.enviarNotificacionSMS(telefono, mensaje);
            String messageId = future.join(); // Esperar el resultado

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("messageId", messageId);
            response.put("telefono", telefono);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error enviando SMS: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Crea un nuevo topic de SNS.
     * @param request Un mapa que contiene el "nombre" del topic.
     * @return ResponseEntity con el ARN del topic si es exitoso, o un error.
     */
    @PostMapping("/topic")
    public ResponseEntity<Map<String, String>> crearTopic(@RequestBody Map<String, String> request) {
        String nombreTopic = request.get("nombre");

        if (nombreTopic == null || nombreTopic.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Nombre del topic es requerido");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            CompletableFuture<String> future = awsSnsService.crearTopic(nombreTopic);
            String topicArn = future.join();

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("topicArn", topicArn);
            response.put("nombre", nombreTopic);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error creando topic: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Publica un mensaje en un topic de SNS.
     * @param request Un mapa que contiene el "mensaje" y el "asunto".
     * @return ResponseEntity con el ID del mensaje si es exitoso, o un error.
     */
    @PostMapping("/topic/publish")
    public ResponseEntity<Map<String, String>> publicarEnTopic(@RequestBody Map<String, String> request) {
        String mensaje = request.get("mensaje");
        String asunto = request.get("asunto");

        if (mensaje == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Mensaje es requerido");
            return ResponseEntity.badRequest().body(error);
        }

        if (asunto == null) {
            asunto = "Notificación AutoFixPro";
        }

        try {
            CompletableFuture<String> future = awsSnsService.publicarEnTopic(mensaje, asunto);
            String messageId = future.join();

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("messageId", messageId);
            response.put("asunto", asunto);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error publicando en topic: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Suscribe un correo electrónico a un topic de SNS.
     * @param request Un mapa que contiene el "topicArn" y el "email".
     * @return ResponseEntity con el ARN de la suscripción si es exitoso, o un error.
     */
    @PostMapping("/topic/subscribe")
    public ResponseEntity<Map<String, String>> suscribirEmail(@RequestBody Map<String, String> request) {
        String topicArn = request.get("topicArn");
        String email = request.get("email");

        if (topicArn == null || email == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "TopicArn y email son requeridos");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            CompletableFuture<String> future = awsSnsService.suscribirEmail(topicArn, email);
            String subscriptionArn = future.join();

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("subscriptionArn", subscriptionArn);
            response.put("email", email);
            response.put("topicArn", topicArn);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error suscribiendo email: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}