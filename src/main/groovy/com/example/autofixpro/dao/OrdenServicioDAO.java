package com.example.autofixpro.dao;

import com.example.autofixpro.entity.OrdenServicio;
import com.example.autofixpro.enumeration.EstadoOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrdenServicioDAO extends JpaRepository<OrdenServicio, Long> {

    List<OrdenServicio> findByEstadoOrden(EstadoOrden estadoOrden);

    List<OrdenServicio> findByTecnicoTecnicoId(Long tecnicoId);

    List<OrdenServicio> findByVehiculoVehiculoId(Long vehiculoId);

    @Query("SELECT os FROM OrdenServicio os WHERE os.fechaIngreso BETWEEN :fechaInicio AND :fechaFin")
    List<OrdenServicio> findByFechaIngresoBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                  @Param("fechaFin") LocalDateTime fechaFin);

    // Para el caso de uso CU02: Actualizar progreso
    @Query("SELECT os FROM OrdenServicio os LEFT JOIN FETCH os.estadosVehiculo " +
            "WHERE os.ordenId = :ordenId")
    OrdenServicio findByIdWithEstados(@Param("ordenId") Long ordenId);

    // Para reportes (CU06)
    @Query("SELECT COUNT(os) FROM OrdenServicio os WHERE os.estadoOrden = :estado " +
            "AND os.fechaIngreso BETWEEN :fechaInicio AND :fechaFin")
    Long countByEstadoAndFecha(@Param("estado") EstadoOrden estado,
                               @Param("fechaInicio") LocalDateTime fechaInicio,
                               @Param("fechaFin") LocalDateTime fechaFin);
}