package com.example.autofixpro.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Table(name = "vehiculos")
public class Vehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehiculoId;

    @Column(unique = true, nullable = false, length = 10)
    private String placa;

    @Column(nullable = false, length = 50)
    private String marca;

    @Column(nullable = false, length = 50)
    private String modelo;

    @Column(name = "año", nullable = false)
    private String year;

    @Column(length = 30)
    private String color;

    @Column
    private Integer kilometraje;

    // Relación con Cliente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    @JsonBackReference
    private Cliente cliente;

    // Relación con Órdenes de Servicio
    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OrdenServicio> ordenesServicio;

    // Constructores
    public Vehiculo() {}

    public Vehiculo(String placa, String marca, String modelo, String year) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.year = year;
    }

    // Métodos de negocio
    public void obtenerHistorialServicios() {
        // CU09: Ver historial de servicio
    }

    // Getters y Setters
    public Long getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(Long vehiculoId) { this.vehiculoId = vehiculoId; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Integer getKilometraje() { return kilometraje; }
    public void setKilometraje(Integer kilometraje) { this.kilometraje = kilometraje; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public List<OrdenServicio> getOrdenesServicio() { return ordenesServicio; }
    public void setOrdenesServicio(List<OrdenServicio> ordenesServicio) { this.ordenesServicio = ordenesServicio; }
}
