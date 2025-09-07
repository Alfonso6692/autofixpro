package com.example.autofixpro.service;

import com.example.autofixpro.dao.OrdenServicioDAO;
import com.example.autofixpro.entity.OrdenServicio;
import com.example.autofixpro.entity.Vehiculo;
import com.example.autofixpro.entity.Tecnico;
import com.example.autofixpro.enumeration.EstadoOrden;
import com.example.autofixpro.util.EstadoVehiculoManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrdenServicioService implements GenericService<OrdenServicio, Long> {

    @Autowired
    private OrdenServicioDAO ordenServicioDAO;

    @Autowired
    private NotificacionServicio notificationService;

    @Autowired
    private EstadoVehiculoManager estadoVehiculoManager;

    @Override
    public OrdenServicio save(OrdenServicio ordenServicio) {
        return ordenServicioDAO.save(ordenServicio);
    }

    @Override
    public Optional<OrdenServicio> findById(Long id) {
        return ordenServicioDAO.findById(id);
    }

    @Override
    public List<OrdenServicio> findAll() {
        return ordenServicioDAO.findAll();
    }

    @Override
    public OrdenServicio update(OrdenServicio ordenServicio) {
        return ordenServicioDAO.save(ordenServicio);
    }

    @Override
    public void deleteById(Long id) {
        ordenServicioDAO.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return ordenServicioDAO.existsById(id);
    }

    @Override
    public long count() {
        return ordenServicioDAO.count();
    }

    // Métodos específicos del negocio

    // CU03: Crear orden de servicios
    public OrdenServicio crearOrdenServicio(Vehiculo vehiculo, String descripcionProblema) {
        OrdenServicio orden = new OrdenServicio();
        orden.setVehiculo(vehiculo);
        orden.setDescripcionProblema(descripcionProblema);
        orden.setFechaIngreso(LocalDateTime.now());
        orden.setEstadoOrden(EstadoOrden.RECIBIDO);

        OrdenServicio savedOrden = ordenServicioDAO.save(orden);

        // Crear estado inicial del vehículo
        estadoVehiculoManager.crearEstadoInicial(savedOrden);

        // Enviar notificación de ingreso
        notificationService.notificarIngresoVehiculo(savedOrden);

        return savedOrden;
    }

    // CU02: Actualizar progreso
    public OrdenServicio actualizarProgreso(Long ordenId, EstadoOrden nuevoEstado, String observaciones) {
        Optional<OrdenServicio> ordenOpt = ordenServicioDAO.findById(ordenId);

        if (ordenOpt.isPresent()) {
            OrdenServicio orden = ordenOpt.get();
            EstadoOrden estadoAnterior = orden.getEstadoOrden();

            orden.setEstadoOrden(nuevoEstado);
            OrdenServicio updatedOrden = ordenServicioDAO.save(orden);

            // Actualizar estado del vehículo
            estadoVehiculoManager.actualizarEstado(updatedOrden, nuevoEstado, observaciones);

            // Enviar notificación de actualización
            notificationService.notificarActualizacionEstado(updatedOrden, estadoAnterior, nuevoEstado);

            return updatedOrden;
        }

        throw new RuntimeException("Orden de servicio no encontrada: " + ordenId);
    }

    public List<OrdenServicio> findByEstado(EstadoOrden estado) {
        return ordenServicioDAO.findByEstadoOrden(estado);
    }

    public List<OrdenServicio> findByTecnico(Long tecnicoId) {
        return ordenServicioDAO.findByTecnicoTecnicoId(tecnicoId);
    }

    public List<OrdenServicio> findByVehiculo(Long vehiculoId) {
        return ordenServicioDAO.findByVehiculoVehiculoId(vehiculoId);
    }

    // Para reportes (CU06)
    public List<OrdenServicio> findByPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ordenServicioDAO.findByFechaIngresoBetween(fechaInicio, fechaFin);
    }
}