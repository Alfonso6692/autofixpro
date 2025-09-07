package com.example.autofixpro.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "talleres_mecanicos")
public class TallerMecanico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tallerId;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(length = 100)
    private String email;

    @Column(length = 50)
    private String horarioAtencion;

    // Relación con Técnicos
    @OneToMany(mappedBy = "taller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tecnico> tecnicos;

    // Constructores
    public TallerMecanico() {}

    public TallerMecanico(String nombre, String direccion, String telefono) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    // Métodos de negocio
    public void gestionarOrdenes() {
        // CU06: Generar reportes del taller
    }

    public void gestionarReportes() {
        // Lógica para reportes del taller
    }

    // Getters y Setters
    public Long getTallerId() { return tallerId; }
    public void setTallerId(Long tallerId) { this.tallerId = tallerId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getHorarioAtencion() { return horarioAtencion; }
    public void setHorarioAtencion(String horarioAtencion) { this.horarioAtencion = horarioAtencion; }

    public List<Tecnico> getTecnicos() { return tecnicos; }
    public void setTecnicos(List<Tecnico> tecnicos) { this.tecnicos = tecnicos; }
}