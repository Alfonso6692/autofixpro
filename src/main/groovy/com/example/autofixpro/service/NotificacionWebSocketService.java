package com.example.autofixpro.service;

import com.example.autofixpro.dto.NotificacionDTO;
import com.example.autofixpro.entity.OrdenServicio;
import com.example.autofixpro.entity.Vehiculo;
import com.example.autofixpro.enumeration.EstadoOrden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Servicio para enviar notificaciones en tiempo real a los clientes
 * usando WebSocket/STOMP.
 */
@Service
public class NotificacionWebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Enviar notificación a un usuario específico.
     * @param username El nombre de usuario del cliente.
     * @param notificacion La notificación a enviar.
     */
    public void enviarNotificacionAUsuario(String username, NotificacionDTO notificacion) {
        messagingTemplate.convertAndSendToUser(
            username,
            "/queue/notificaciones",
            notificacion
        );
        System.out.println("📨 Notificación enviada a usuario: " + username + " - " + notificacion.getTitulo());
    }

    /**
     * Enviar notificación broadcast a todos los clientes conectados.
     * @param notificacion La notificación a enviar.
     */
    public void enviarNotificacionBroadcast(NotificacionDTO notificacion) {
        messagingTemplate.convertAndSend(
            "/topic/notificaciones",
            notificacion
        );
        System.out.println("📢 Notificación broadcast enviada: " + notificacion.getTitulo());
    }

    /**
     * Notificar cambio de estado de una orden de servicio.
     * @param orden La orden de servicio actualizada.
     * @param estadoAnterior El estado anterior de la orden.
     */
    public void notificarCambioEstado(OrdenServicio orden, EstadoOrden estadoAnterior) {
        if (orden.getVehiculo() == null || orden.getVehiculo().getCliente() == null) {
            return;
        }

        String username = obtenerUsernameDelCliente(orden.getVehiculo());
        if (username == null) {
            return;
        }

        Integer progreso = calcularPorcentajeProgreso(orden.getEstadoOrden());
        String mensajeEstado = formatearEstado(orden.getEstadoOrden());

        NotificacionDTO notificacion = NotificacionDTO.builder()
            .tipo("ESTADO_ACTUALIZADO")
            .titulo("🔧 Estado Actualizado")
            .mensaje(String.format("Tu vehículo %s %s ahora está en estado: %s",
                    orden.getVehiculo().getMarca(),
                    orden.getVehiculo().getModelo(),
                    mensajeEstado))
            .ordenId(orden.getOrdenId())
            .vehiculoId(orden.getVehiculo().getVehiculoId())
            .estadoAnterior(estadoAnterior != null ? estadoAnterior.name() : null)
            .estadoNuevo(orden.getEstadoOrden().name())
            .porcentajeProgreso(progreso)
            .build();

        enviarNotificacionAUsuario(username, notificacion);
    }

    /**
     * Notificar cuando una orden está completada.
     * @param orden La orden de servicio completada.
     */
    public void notificarOrdenCompletada(OrdenServicio orden) {
        if (orden.getVehiculo() == null || orden.getVehiculo().getCliente() == null) {
            return;
        }

        String username = obtenerUsernameDelCliente(orden.getVehiculo());
        if (username == null) {
            return;
        }

        NotificacionDTO notificacion = NotificacionDTO.builder()
            .tipo("ORDEN_COMPLETADA")
            .titulo("✅ ¡Reparación Completada!")
            .mensaje(String.format("Tu vehículo %s %s está listo. Puedes pasar a recogerlo.",
                    orden.getVehiculo().getMarca(),
                    orden.getVehiculo().getModelo()))
            .ordenId(orden.getOrdenId())
            .vehiculoId(orden.getVehiculo().getVehiculoId())
            .estadoNuevo("COMPLETADO")
            .porcentajeProgreso(100)
            .build();

        enviarNotificacionAUsuario(username, notificacion);
    }

    /**
     * Notificar cuando hay un nuevo mensaje o actualización.
     * @param orden La orden de servicio.
     * @param mensaje El mensaje personalizado.
     */
    public void notificarMensajePersonalizado(OrdenServicio orden, String titulo, String mensaje) {
        if (orden.getVehiculo() == null || orden.getVehiculo().getCliente() == null) {
            return;
        }

        String username = obtenerUsernameDelCliente(orden.getVehiculo());
        if (username == null) {
            return;
        }

        NotificacionDTO notificacion = NotificacionDTO.builder()
            .tipo("MENSAJE_PERSONALIZADO")
            .titulo(titulo)
            .mensaje(mensaje)
            .ordenId(orden.getOrdenId())
            .vehiculoId(orden.getVehiculo().getVehiculoId())
            .build();

        enviarNotificacionAUsuario(username, notificacion);
    }

    /**
     * Obtener el username del cliente propietario del vehículo.
     */
    private String obtenerUsernameDelCliente(Vehiculo vehiculo) {
        if (vehiculo.getCliente() != null && vehiculo.getCliente().getUsuario() != null) {
            return vehiculo.getCliente().getUsuario().getUsername();
        }
        return null;
    }

    /**
     * Calcular porcentaje de progreso según el estado.
     */
    private Integer calcularPorcentajeProgreso(EstadoOrden estado) {
        switch (estado) {
            case RECIBIDO: return 10;
            case EN_DIAGNOSTICO: return 25;
            case EN_REPARACION: return 50;
            case EN_PRUEBAS: return 80;
            case COMPLETADO: return 100;
            case ENTREGADO: return 100;
            default: return 0;
        }
    }

    /**
     * Formatear el estado para mostrar al usuario.
     */
    private String formatearEstado(EstadoOrden estado) {
        switch (estado) {
            case RECIBIDO: return "Recibido";
            case EN_DIAGNOSTICO: return "En Diagnóstico";
            case EN_REPARACION: return "En Reparación";
            case EN_PRUEBAS: return "En Pruebas";
            case COMPLETADO: return "Completado";
            case ENTREGADO: return "Entregado";
            default: return estado.name();
        }
    }
}
