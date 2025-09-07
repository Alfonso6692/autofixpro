package com.example.autofixpro.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "estados_vehiculo")
public class EstadoVehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long estadoId;

    @Column(nullable = false)
    private String estado;

    @Column
    private String descripcionEstado;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @Column
    private Integer porcentajeAvance;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    // Relación con Orden de Servicio
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_servicio_id")
    private OrdenServicio ordenServicio;

    // Constructores
    public EstadoVehiculo() {
        this.fechaActualizacion = LocalDateTime.now();
        this.porcentajeAvance = 0;
    }

    public EstadoVehiculo(String estado, String descripcion, OrdenServicio ordenServicio) {
        this();
        this.estado = estado;
        this.descripcionEstado = descripcion;
        this.ordenServicio = ordenServicio;
    }

    // Getters y Setters
    public Long getEstadoId() { return estadoId; }
    public void setEstadoId(Long estadoId) { this.estadoId = estadoId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getDescripcionEstado() { return descripcionEstado; }
    public void setDescripcionEstado(String descripcionEstado) { this.descripcionEstado = descripcionEstado; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public Integer getPorcentajeAvance() { return porcentajeAvance; }
    public void setPorcentajeAvance(Integer porcentajeAvance) { this.porcentajeAvance = porcentajeAvance; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public OrdenServicio getOrdenServicio() { return ordenServicio; }
    public void setOrdenServicio(OrdenServicio ordenServicio) { this.ordenServicio = ordenServicio; }
}