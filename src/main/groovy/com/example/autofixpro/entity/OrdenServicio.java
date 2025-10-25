package com.example.autofixpro.entity;

import com.example.autofixpro.enumeration.EstadoOrden;
import com.example.autofixpro.enumeration.Prioridad;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ordenes_servicio")
public class OrdenServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ordenId;

    @Column(nullable = false)
    private LocalDateTime fechaIngreso;

    @Column
    private LocalDateTime fechaEntrega;

    @Column(columnDefinition = "TEXT")
    private String descripcionProblema;

    @Column
    private Double costoEstimado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoOrden estadoOrden;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridad prioridad;

    // Relación con Vehículo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id")
    private Vehiculo vehiculo;

    // Relación con Técnico
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_id")
    private Tecnico tecnico;

    // Relación con Servicios
    @OneToMany(mappedBy = "ordenServicio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Servicio> servicios;

    // Relación con Estados de Vehículo
    @OneToMany(mappedBy = "ordenServicio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<EstadoVehiculo> estadosVehiculo;

    // Constructores
    public OrdenServicio() {
        this.fechaIngreso = LocalDateTime.now();
        this.estadoOrden = EstadoOrden.RECIBIDO;
        this.prioridad = Prioridad.NORMAL;
    }

    // Métodos de negocio
    public void crearOrden() {
        // CU03: Crear orden de servicios
    }

    public void asignarTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }

    public void calcularCostoTotal() {
        // Calcular costo basado en servicios
    }

    public void completarOrden() {
        this.estadoOrden = EstadoOrden.COMPLETADO;
        this.fechaEntrega = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getOrdenId() { return ordenId; }
    public void setOrdenId(Long ordenId) { this.ordenId = ordenId; }

    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDateTime fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public String getDescripcionProblema() { return descripcionProblema; }
    public void setDescripcionProblema(String descripcionProblema) { this.descripcionProblema = descripcionProblema; }

    public Double getCostoEstimado() { return costoEstimado; }
    public void setCostoEstimado(Double costoEstimado) { this.costoEstimado = costoEstimado; }

    public EstadoOrden getEstadoOrden() { return estadoOrden; }
    public void setEstadoOrden(EstadoOrden estadoOrden) { this.estadoOrden = estadoOrden; }

    public Prioridad getPrioridad() { return prioridad; }
    public void setPrioridad(Prioridad prioridad) { this.prioridad = prioridad; }

    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }

    public Tecnico getTecnico() { return tecnico; }
    public void setTecnico(Tecnico tecnico) { this.tecnico = tecnico; }

    public List<Servicio> getServicios() { return servicios; }
    public void setServicios(List<Servicio> servicios) { this.servicios = servicios; }

    public List<EstadoVehiculo> getEstadosVehiculo() { return estadosVehiculo; }
    public void setEstadosVehiculo(List<EstadoVehiculo> estadosVehiculo) { this.estadosVehiculo = estadosVehiculo; }
}