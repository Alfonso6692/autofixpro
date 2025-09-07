package com.example.autofixpro.entity;

import com.example.autofixpro.enumeration.TipoNotificacion;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNotificacion tipoNotificacion;

    @Column(nullable = false)
    private String mensajes;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column
    private LocalDateTime fechaEnvio;

    @Column(nullable = false)
    private String estadoEnvio;

    @Column
    private String destinatario;

    @Column
    private String canal; // EMAIL, SMS, PUSH

    // Constructores
    public Notification() {
        this.fechaCreacion = LocalDateTime.now();
        this.estadoEnvio = "PENDIENTE";
    }

    public Notification(TipoNotificacion tipo, String mensaje, String destinatario) {
        this();
        this.tipoNotificacion = tipo;
        this.mensajes = mensaje;
        this.destinatario = destinatario;
    }

    // Métodos de negocio
    public void marcarEnviado() {
        this.estadoEnvio = "ENVIADO";
        this.fechaEnvio = LocalDateTime.now();
    }

    public void marcarFallido() {
        this.estadoEnvio = "FALLIDO";
    }

    // Getters y Setters
    public Long getNotificationId() { return notificationId; }
    public void setNotificationId(Long notificationId) { this.notificationId = notificationId; }

    public TipoNotificacion getTipoNotificacion() { return tipoNotificacion; }
    public void setTipoNotificacion(TipoNotificacion tipoNotificacion) { this.tipoNotificacion = tipoNotificacion; }

    public String getMensajes() { return mensajes; }
    public void setMensajes(String mensajes) { this.mensajes = mensajes; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public String getEstadoEnvio() { return estadoEnvio; }
    public void setEstadoEnvio(String estadoEnvio) { this.estadoEnvio = estadoEnvio; }

    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }

    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }
}