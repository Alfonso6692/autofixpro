package com.example.autofixpro.util;

import com.example.autofixpro.entity.EstadoVehiculo;
import com.example.autofixpro.entity.OrdenServicio;
import com.example.autofixpro.enumeration.EstadoOrden;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EstadoVehiculoManager {

    public void crearEstadoInicial(OrdenServicio orden) {
        EstadoVehiculo estadoInicial = new EstadoVehiculo();
        estadoInicial.setOrdenServicio(orden);
        estadoInicial.setEstado(EstadoOrden.RECIBIDO.name());
        estadoInicial.setDescripcionEstado("Vehículo recibido en el taller");
        estadoInicial.setFechaActualizacion(LocalDateTime.now());
        estadoInicial.setPorcentajeAvance(0);
        estadoInicial.setObservaciones("Vehículo ingresado para diagnóstico");

        // Aquí se guardaría en la base de datos
        // estadoVehiculoDAO.save(estadoInicial);
    }

    public void actualizarEstado(OrdenServicio orden, EstadoOrden nuevoEstado, String observaciones) {
        EstadoVehiculo nuevoEstadoVehiculo = new EstadoVehiculo();
        nuevoEstadoVehiculo.setOrdenServicio(orden);
        nuevoEstadoVehiculo.setEstado(nuevoEstado.name());
        nuevoEstadoVehiculo.setDescripcionEstado(nuevoEstado.getDescripcion());
        nuevoEstadoVehiculo.setFechaActualizacion(LocalDateTime.now());
        nuevoEstadoVehiculo.setPorcentajeAvance(calcularPorcentajeAvance(nuevoEstado));
        nuevoEstadoVehiculo.setObservaciones(observaciones);

        // Aquí se guardaría en la base de datos
        // estadoVehiculoDAO.save(nuevoEstadoVehiculo);
    }

    private Integer calcularPorcentajeAvance(EstadoOrden estado) {
        return switch (estado) {
            case RECIBIDO -> 10;
            case EN_DIAGNOSTICO -> 25;
            case EN_REPARACION -> 50;
            case EN_PRUEBAS -> 80;
            case COMPLETADO -> 100;
            case ENTREGADO -> 100;
            default -> 0;
        };
    }

    public void notificarCambiosEstado(OrdenServicio orden) {
        // Lógica para notificar cambios de estado
        // Se integraría con el SistemaNotificaciones
    }
}