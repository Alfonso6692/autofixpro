package com.example.autofixpro.controller;

import com.example.autofixpro.entity.OrdenServicio;
import com.example.autofixpro.service.NotificacionServicio;
import com.example.autofixpro.service.OrdenServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    // 2. Inyecta SnsService, no NotificacionServicio
    private final NotificacionServicio notificacionServicio;
    private final OrdenServicioService ordenServicioService; // Para buscar la orden

    @Autowired
    public NotificationController(NotificacionServicio notificacionServicio, OrdenServicioService ordenServicioService) {
        this.notificacionServicio = notificacionServicio;
        this.ordenServicioService = ordenServicioService;
    }
    // Endpoint para probar la notificación de "trabajo completado"
    @PostMapping("/orden-completada/{ordenId}")
    public ResponseEntity<String> notificarOrdenCompletada(@PathVariable Long ordenId) {
        // 1. Busca la orden de servicio en la base de datos
        OrdenServicio orden = ordenServicioService.findById(ordenId).orElse(null);
        if (orden == null) {
            return ResponseEntity.notFound().build();
        }

        // 2. Llama al servicio de notificación
        notificacionServicio.notificarCompletado(orden);

        return ResponseEntity.ok("Notificación de orden completada enviada para la orden #" + ordenId);
    }
}

