package com.example.autofixpro.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tecnicos")
public class Tecnico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tecnicoId;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(unique = true, nullable = false, length = 20)
    private String dni;

    @Column(nullable = false, length = 100)
    private String especialidad;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(nullable = false)
    private Boolean estadoActivo;

    // Relación con Órdenes de Servicio
    @OneToMany(mappedBy = "tecnico", fetch = FetchType.LAZY)
    private List<OrdenServicio> ordenesAsignadas;

    // Relación con Taller Mecánico
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taller_id")
    private TallerMecanico taller;

    // Constructores
    public Tecnico() {
        this.estadoActivo = true;
    }

    public Tecnico(String nombres, String apellidos, String dni, String especialidad, String telefono) {
        this();
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dni = dni;
        this.especialidad = especialidad;
        this.telefono = telefono;
    }

    // Métodos de negocio
    public void actualizarProgreso() {
        // CU02: Actualizar progreso
    }

    public void finalizarReparacion() {
        // Finalizar reparación asignada
    }

    // Getters y Setters
    public Long getTecnicoId() { return tecnicoId; }
    public void setTecnicoId(Long tecnicoId) { this.tecnicoId = tecnicoId; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Boolean getEstadoActivo() { return estadoActivo; }
    public void setEstadoActivo(Boolean estadoActivo) { this.estadoActivo = estadoActivo; }

    public List<OrdenServicio> getOrdenesAsignadas() { return ordenesAsignadas; }
    public void setOrdenesAsignadas(List<OrdenServicio> ordenesAsignadas) { this.ordenesAsignadas = ordenesAsignadas; }

    public TallerMecanico getTaller() { return taller; }
    public void setTaller(TallerMecanico taller) { this.taller = taller; }
}