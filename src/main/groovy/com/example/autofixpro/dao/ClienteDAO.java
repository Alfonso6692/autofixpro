package com.example.autofixpro.dao;

import com.example.autofixpro.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ClienteDAO extends JpaRepository<Cliente, Long> {

    // Consultas personalizadas para casos de uso
    Optional<Cliente> findByDni(String dni);

    Optional<Cliente> findByEmail(String email);

    @Query("SELECT c FROM Cliente c WHERE c.telefono = :telefono")
    Optional<Cliente> findByTelefono(@Param("telefono") String telefono);

    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nombres) LIKE LOWER(CONCAT('%', :nombre, '%')) " +
            "OR LOWER(c.apellidos) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Cliente> findByNombreContaining(@Param("nombre") String nombre);

    // Para el caso de uso CU01: Consultar estado del vehículo
    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.vehiculos v WHERE c.clienteId = :clienteId")
    Optional<Cliente> findByIdWithVehiculos(@Param("clienteId") Long clienteId);
}