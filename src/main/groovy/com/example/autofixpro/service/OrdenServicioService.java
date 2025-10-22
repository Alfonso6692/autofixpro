package com.example.autofixpro.service;

import com.example.autofixpro.dao.OrdenServicioDAO;
import com.example.autofixpro.entity.OrdenServicio;
import com.example.autofixpro.entity.Vehiculo;
import com.example.autofixpro.enumeration.EstadoOrden;
import com.example.autofixpro.util.EstadoVehiculoManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar la lógica de negocio de las órdenes de servicio.
 * Encapsula las operaciones relacionadas con la creación, actualización y consulta de órdenes.
 */
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

    /**
     * Crea una nueva orden de servicio para un vehículo.
     * Corresponde al CU03: Crear orden de servicios.
     * @param vehiculo El vehículo para el cual se crea la orden.
     * @param descripcionProblema La descripción del problema reportado por el cliente.
     * @return La orden de servicio creada y guardada.
     */
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

    /**
     * Actualiza el progreso de una orden de servicio existente.
     * Corresponde al CU02: Actualizar progreso.
     * @param ordenId El ID de la orden a actualizar.
     * @param nuevoEstado El nuevo estado de la orden.
     * @param observaciones Observaciones adicionales sobre la actualización.
     * @return La orden de servicio actualizada.
     * @throws RuntimeException si la orden de servicio no se encuentra.
     */
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

    /**
     * Busca todas las órdenes de servicio que se encuentran en un estado específico.
     * @param estado El estado de la orden a buscar.
     * @return Una lista de órdenes de servicio que coinciden con el estado.
     */
    public List<OrdenServicio> findByEstado(EstadoOrden estado) {
        return ordenServicioDAO.findByEstadoOrden(estado);
    }

    /**
     * Busca todas las órdenes de servicio asignadas a un técnico específico.
     * @param tecnicoId El ID del técnico.
     * @return Una lista de órdenes de servicio asignadas al técnico.
     */
    public List<OrdenServicio> findByTecnico(Long tecnicoId) {
        return ordenServicioDAO.findByTecnicoTecnicoId(tecnicoId);
    }

    /**
     * Busca todas las órdenes de servicio asociadas a un vehículo específico.
     * @param vehiculoId El ID del vehículo.
     * @return Una lista de órdenes de servicio para el vehículo.
     */
    public List<OrdenServicio> findByVehiculo(Long vehiculoId) {
        return ordenServicioDAO.findByVehiculoVehiculoId(vehiculoId);
    }

    /**
     * Busca órdenes de servicio dentro de un período de tiempo específico.
     * Útil para generar reportes (CU06).
     * @param fechaInicio La fecha de inicio del período.
     * @param fechaFin La fecha de fin del período.
     * @return Una lista de órdenes de servicio creadas dentro del período.
     */
    public List<OrdenServicio> findByPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ordenServicioDAO.findByFechaIngresoBetween(fechaInicio, fechaFin);
    }
}