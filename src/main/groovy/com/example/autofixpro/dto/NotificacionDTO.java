package com.example.autofixpro.dto;

import java.time.LocalDateTime;

/**
 * DTO para enviar notificaciones en tiempo real a los clientes.
 */
public class NotificacionDTO {
    private String tipo;  // "ESTADO_ACTUALIZADO", "NUEVO_MENSAJE", "ORDEN_COMPLETADA", etc.
    private String titulo;
    private String mensaje;
    private Long ordenId;
    private Long vehiculoId;
    private String estadoAnterior;
    private String estadoNuevo;
    private Integer porcentajeProgreso;
    private LocalDateTime timestamp;

    public NotificacionDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public NotificacionDTO(String tipo, String titulo, String mensaje) {
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now();
    }

    // Builder pattern para construcción fluida
    public static class Builder {
        private NotificacionDTO notificacion = new NotificacionDTO();

        public Builder tipo(String tipo) {
            notificacion.tipo = tipo;
            return this;
        }

        public Builder titulo(String titulo) {
            notificacion.titulo = titulo;
            return this;
        }

        public Builder mensaje(String mensaje) {
            notificacion.mensaje = mensaje;
            return this;
        }

        public Builder ordenId(Long ordenId) {
            notificacion.ordenId = ordenId;
            return this;
        }

        public Builder vehiculoId(Long vehiculoId) {
            notificacion.vehiculoId = vehiculoId;
            return this;
        }

        public Builder estadoAnterior(String estadoAnterior) {
            notificacion.estadoAnterior = estadoAnterior;
            return this;
        }

        public Builder estadoNuevo(String estadoNuevo) {
            notificacion.estadoNuevo = estadoNuevo;
            return this;
        }

        public Builder porcentajeProgreso(Integer porcentajeProgreso) {
            notificacion.porcentajeProgreso = porcentajeProgreso;
            return this;
        }

        public NotificacionDTO build() {
            return notificacion;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters y Setters
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public Long getOrdenId() { return ordenId; }
    public void setOrdenId(Long ordenId) { this.ordenId = ordenId; }

    public Long getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(Long vehiculoId) { this.vehiculoId = vehiculoId; }

    public String getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(String estadoAnterior) { this.estadoAnterior = estadoAnterior; }

    public String getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(String estadoNuevo) { this.estadoNuevo = estadoNuevo; }

    public Integer getPorcentajeProgreso() { return porcentajeProgreso; }
    public void setPorcentajeProgreso(Integer porcentajeProgreso) { this.porcentajeProgreso = porcentajeProgreso; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
