package com.example.autofixpro.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;

    // Relación OneToOne con Cliente
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Cliente cliente;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        ultimaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        ultimaActualizacion = LocalDateTime.now();
    }

    public enum Role {
        USER,
        ADMIN,
        TECNICO,
        RECEPCIONISTA
    }
}
