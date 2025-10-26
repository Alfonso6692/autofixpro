package com.example.autofixpro.dao;

import com.example.autofixpro.entity.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface VehiculoDAO extends JpaRepository<Vehiculo, Long> {

    Optional<Vehiculo> findByPlaca(String placa);

    List<Vehiculo> findByClienteClienteId(Long clienteId);

    List<Vehiculo> findByMarcaAndModelo(String marca, String modelo);

    @Query("SELECT v FROM Vehiculo v WHERE v.year >= :yearMinimo")
    List<Vehiculo> findByAñoGreaterThanEqual(@Param("yearMinimo") String yearMinimo);

    // Para el caso de uso CU08: Consultar estado del vehículo
    @Query("SELECT v FROM Vehiculo v LEFT JOIN FETCH v.ordenesServicio os " +
            "LEFT JOIN FETCH os.estadosVehiculo WHERE v.placa = :placa")
    Optional<Vehiculo> findByPlacaWithOrdenesAndEstados(@Param("placa") String placa);

    // Para obtener historial de servicios (CU09)
    @Query("SELECT v FROM Vehiculo v LEFT JOIN FETCH v.ordenesServicio os " +
            "LEFT JOIN FETCH os.servicios WHERE v.vehiculoId = :vehiculoId")
    Optional<Vehiculo> findByIdWithHistorialServicios(@Param("vehiculoId") Long vehiculoId);
}