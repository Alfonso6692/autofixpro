package com.example.autofixpro.service;

import com.example.autofixpro.entity.Cliente;
import com.example.autofixpro.entity.OrdenServicio;
import com.example.autofixpro.entity.Notification;
import com.example.autofixpro.enumeration.EstadoOrden;
import com.example.autofixpro.enumeration.TipoNotificacion;
import com.example.autofixpro.util.SistemaNotificaciones;
import org.springframework.stereotype.Service;

@Service
public class NotificacionServicio {

    // Singleton para sistema de notificaciones
    private final SistemaNotificaciones sistemaNotificaciones = SistemaNotificaciones.getInstance();

    public void enviarNotificacionRegistro(Cliente cliente) {
        String mensaje = String.format("Bienvenido %s %s. Su registro ha sido completado exitosamente.",
                cliente.getNombres(), cliente.getApellidos());

        Notification notification = new Notification();
        notification.setTipoNotificacion(TipoNotificacion.INGRESO);
        notification.setMensajes(mensaje);
        notification.setEstadoEnvio("PENDIENTE");

        sistemaNotificaciones.enviarNotificacion(cliente.getEmail(), notification);
    }

    public void notificarIngresoVehiculo(OrdenServicio orden) {
        Cliente cliente = orden.getVehiculo().getCliente();
        String mensaje = String.format("Su vehículo con placa %s ha ingresado al taller. " +
                        "Número de orden: %d. Estado: %s",
                orden.getVehiculo().getPlaca(),
                orden.getOrdenId(),
                orden.getEstadoOrden().getDescripcion());

        Notification notification = new Notification();
        notification.setTipoNotificacion(TipoNotificacion.INGRESO);
        notification.setMensajes(mensaje);

        sistemaNotificaciones.enviarNotificacion(cliente.getEmail(), notification);
        sistemaNotificaciones.enviarSMS(cliente.getTelefono(), mensaje);
    }

    public void notificarActualizacionEstado(OrdenServicio orden, EstadoOrden estadoAnterior, EstadoOrden nuevoEstado) {
        Cliente cliente = orden.getVehiculo().getCliente();
        String mensaje = String.format("Actualización de su vehículo %s: %s → %s. " +
                        "Orden #%d",
                orden.getVehiculo().getPlaca(),
                estadoAnterior.getDescripcion(),
                nuevoEstado.getDescripcion(),
                orden.getOrdenId());

        Notification notification = new Notification();
        notification.setTipoNotificacion(TipoNotificacion.ACTUALIZACION);
        notification.setMensajes(mensaje);

        sistemaNotificaciones.enviarNotificacion(cliente.getEmail(), notification);

        // Enviar SMS solo para estados importantes
        if (nuevoEstado == EstadoOrden.COMPLETADO || nuevoEstado == EstadoOrden.EN_REPARACION) {
            sistemaNotificaciones.enviarSMS(cliente.getTelefono(), mensaje);
        }
    }

    public void notificarCompletado(OrdenServicio orden) {
        Cliente cliente = orden.getVehiculo().getCliente();
        String mensaje = String.format("¡Su vehículo %s está listo! " +
                        "Puede recogerlo en el horario de atención. Orden #%d",
                orden.getVehiculo().getPlaca(),
                orden.getOrdenId());

        Notification notification = new Notification();
        notification.setTipoNotificacion(TipoNotificacion.COMPLETADO);
        notification.setMensajes(mensaje);

        sistemaNotificaciones.enviarNotificacion(cliente.getEmail(), notification);
        sistemaNotificaciones.enviarSMS(cliente.getTelefono(), mensaje);
    }
}