package com.example.autofixpro.dao;

import com.example.autofixpro.entity.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TecnicoDAO extends JpaRepository<Tecnico, Long> {

    Optional<Tecnico> findByDni(String dni);

    List<Tecnico> findByEspecialidad(String especialidad);

    List<Tecnico> findByEstadoActivo(Boolean estadoActivo);

    @Query("SELECT t FROM Tecnico t WHERE t.estadoActivo = true " +
            "AND SIZE(t.ordenesAsignadas) < 5")
    List<Tecnico> findTecnicosDisponibles();

    // Para asignación de técnicos en CU03
    @Query("SELECT t FROM Tecnico t LEFT JOIN FETCH t.ordenesAsignadas " +
            "WHERE t.tecnicoId = :tecnicoId AND t.estadoActivo = true")
    Optional<Tecnico> findByIdWithOrdenes(@Param("tecnicoId") Long tecnicoId);
}