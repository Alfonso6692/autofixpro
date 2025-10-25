package com.example.autofixpro.dto;

import com.example.autofixpro.entity.OrdenServicio;
import com.example.autofixpro.enumeration.EstadoOrden;
import com.example.autofixpro.enumeration.Prioridad;

import java.time.LocalDateTime;

/**
 * DTO para OrdenServicio que evita referencias circulares en JSON.
 * Incluye solo la información necesaria del vehículo y técnico.
 */
public class OrdenServicioDTO {
    private Long ordenId;
    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaEntrega;
    private String descripcionProblema;
    private Double costoEstimado;
    private EstadoOrden estadoOrden;
    private Prioridad prioridad;

    // Información del vehículo
    private VehiculoInfo vehiculo;

    // Información del técnico
    private TecnicoInfo tecnico;

    // Clases internas para info simplificada
    public static class VehiculoInfo {
        private Long vehiculoId;
        private String placa;
        private String marca;
        private String modelo;
        private String año;
        private String color;

        public VehiculoInfo(Long vehiculoId, String placa, String marca, String modelo, String año, String color) {
            this.vehiculoId = vehiculoId;
            this.placa = placa;
            this.marca = marca;
            this.modelo = modelo;
            this.año = año;
            this.color = color;
        }

        // Getters
        public Long getVehiculoId() { return vehiculoId; }
        public String getPlaca() { return placa; }
        public String getMarca() { return marca; }
        public String getModelo() { return modelo; }
        public String getAño() { return año; }
        public String getColor() { return color; }
    }

    public static class TecnicoInfo {
        private Long tecnicoId;
        private String nombres;
        private String apellidos;
        private String especialidad;

        public TecnicoInfo(Long tecnicoId, String nombres, String apellidos, String especialidad) {
            this.tecnicoId = tecnicoId;
            this.nombres = nombres;
            this.apellidos = apellidos;
            this.especialidad = especialidad;
        }

        // Getters
        public Long getTecnicoId() { return tecnicoId; }
        public String getNombres() { return nombres; }
        public String getApellidos() { return apellidos; }
        public String getEspecialidad() { return especialidad; }
    }

    /**
     * Constructor que convierte una OrdenServicio en DTO.
     */
    public OrdenServicioDTO(OrdenServicio orden) {
        this.ordenId = orden.getOrdenId();
        this.fechaIngreso = orden.getFechaIngreso();
        this.fechaEntrega = orden.getFechaEntrega();
        this.descripcionProblema = orden.getDescripcionProblema();
        this.costoEstimado = orden.getCostoEstimado();
        this.estadoOrden = orden.getEstadoOrden();
        this.prioridad = orden.getPrioridad();

        // Convertir vehículo si existe
        if (orden.getVehiculo() != null) {
            var v = orden.getVehiculo();
            this.vehiculo = new VehiculoInfo(
                v.getVehiculoId(),
                v.getPlaca(),
                v.getMarca(),
                v.getModelo(),
                v.getAño(),
                v.getColor()
            );
        }

        // Convertir técnico si existe
        if (orden.getTecnico() != null) {
            var t = orden.getTecnico();
            this.tecnico = new TecnicoInfo(
                t.getTecnicoId(),
                t.getNombres(),
                t.getApellidos(),
                t.getEspecialidad()
            );
        }
    }

    // Getters
    public Long getOrdenId() { return ordenId; }
    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public String getDescripcionProblema() { return descripcionProblema; }
    public Double getCostoEstimado() { return costoEstimado; }
    public EstadoOrden getEstadoOrden() { return estadoOrden; }
    public Prioridad getPrioridad() { return prioridad; }
    public VehiculoInfo getVehiculo() { return vehiculo; }
    public TecnicoInfo getTecnico() { return tecnico; }
}
