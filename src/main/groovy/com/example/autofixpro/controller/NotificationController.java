package com.example.autofixpro.controller;

import com.example.autofixpro.entity.OrdenServicio;
import com.example.autofixpro.service.NotificacionServicio;
import com.example.autofixpro.service.OrdenServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestionar el envío de notificaciones.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificacionServicio notificacionServicio;
    private final OrdenServicioService ordenServicioService; // Para buscar la orden

    /**
     * Constructor para inyectar los servicios de notificación y orden de servicio.
     * @param notificacionServicio El servicio para enviar notificaciones.
     * @param ordenServicioService El servicio para obtener datos de las órdenes de servicio.
     */
    @Autowired
    public NotificationController(NotificacionServicio notificacionServicio, OrdenServicioService ordenServicioService) {
        this.notificacionServicio = notificacionServicio;
        this.ordenServicioService = ordenServicioService;
    }

    /**
     * Endpoint para enviar una notificación de "trabajo completado".
     * Busca la orden de servicio por su ID y, si la encuentra, invoca al servicio de notificación.
     * @param ordenId El ID de la orden de servicio completada.
     * @return ResponseEntity con un mensaje de éxito o un error 404 si la orden no se encuentra.
     */
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
