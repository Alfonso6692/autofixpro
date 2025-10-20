package com.example.autofixpro.controller;

import com.example.autofixpro.entity.OrdenServicio;
import com.example.autofixpro.entity.Vehiculo;
import com.example.autofixpro.entity.Tecnico;
import com.example.autofixpro.enumeration.EstadoOrden;
import com.example.autofixpro.enumeration.Prioridad;
import com.example.autofixpro.service.OrdenServicioService;
import com.example.autofixpro.service.VehiculoService;
import com.example.autofixpro.service.TecnicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para gestionar las operaciones de órdenes de servicio.
 * Proporciona endpoints para crear, consultar y actualizar órdenes de trabajo.
 */
@RestController
@RequestMapping("/api/ordenes")
@CrossOrigin(origins = "*")
public class OrdenServicioController extends BaseController {

    @Autowired
    private OrdenServicioService ordenServicioService;

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private TecnicoService tecnicoService;

    /**
     * Obtiene una lista de todas las órdenes de servicio.
     * @return ResponseEntity con la lista de órdenes.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listarOrdenes() {
        try {
            List<OrdenServicio> ordenes = ordenServicioService.findAll();
            return createSuccessResponse(ordenes, "Órdenes de servicio obtenidas exitosamente");
        } catch (Exception e) {
            return createErrorResponse("Error al obtener órdenes: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Crea una nueva orden de servicio para un vehículo.
     * Corresponde al CU03: Crear orden de servicios.
     * @param request Objeto con los datos de la orden (vehiculoId, descripcionProblema, prioridad, tecnicoId).
     * @return ResponseEntity con la orden creada.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearOrdenServicio(@RequestBody Map<String, Object> request) {
        try {
            // Validar y obtener vehiculoId
            if (!request.containsKey("vehiculoId")) {
                return createErrorResponse("El campo 'vehiculoId' es obligatorio", HttpStatus.BAD_REQUEST);
            }

            Long vehiculoId = Long.valueOf(request.get("vehiculoId").toString());
            String descripcionProblema = request.getOrDefault("descripcionProblema", "").toString();

            // Obtener el vehículo
            Optional<Vehiculo> vehiculoOpt = vehiculoService.findById(vehiculoId);
            if (!vehiculoOpt.isPresent()) {
                return createErrorResponse("Vehículo no encontrado con ID: " + vehiculoId, HttpStatus.NOT_FOUND);
            }

            // Crear la orden de servicio usando el servicio
            OrdenServicio nuevaOrden = ordenServicioService.crearOrdenServicio(
                vehiculoOpt.get(),
                descripcionProblema
            );

            // Configurar prioridad si se proporciona
            if (request.containsKey("prioridad")) {
                try {
                    Prioridad prioridad = Prioridad.valueOf(request.get("prioridad").toString());
                    nuevaOrden.setPrioridad(prioridad);
                } catch (IllegalArgumentException e) {
                    // Si la prioridad no es válida, se mantiene la por defecto (NORMAL)
                }
            }

            // Configurar costo estimado si se proporciona
            if (request.containsKey("costoEstimado")) {
                Double costoEstimado = Double.valueOf(request.get("costoEstimado").toString());
                nuevaOrden.setCostoEstimado(costoEstimado);
            }

            // Asignar técnico si se proporciona
            if (request.containsKey("tecnicoId")) {
                Long tecnicoId = Long.valueOf(request.get("tecnicoId").toString());
                Optional<Tecnico> tecnicoOpt = tecnicoService.findById(tecnicoId);
                if (tecnicoOpt.isPresent()) {
                    nuevaOrden.setTecnico(tecnicoOpt.get());
                }
            }

            // Guardar cambios adicionales
            OrdenServicio ordenGuardada = ordenServicioService.save(nuevaOrden);

            return createResponse(ordenGuardada, "Orden de servicio creada exitosamente", HttpStatus.CREATED);
        } catch (NumberFormatException e) {
            return createErrorResponse("Formato de ID inválido: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return createErrorResponse("Error al crear orden de servicio: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene una orden de servicio por su ID.
     * @param id El ID de la orden de servicio.
     * @return ResponseEntity con la orden encontrada.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerOrden(@PathVariable Long id) {
        try {
            Optional<OrdenServicio> orden = ordenServicioService.findById(id);
            if (orden.isPresent()) {
                return createSuccessResponse(orden.get(), "Orden de servicio encontrada");
            } else {
                return createErrorResponse("Orden de servicio no encontrada", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return createErrorResponse("Error al obtener orden: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Actualiza el progreso de una orden de servicio.
     * Corresponde al CU02: Actualizar progreso de reparación.
     * @param id El ID de la orden de servicio.
     * @param request Objeto con nuevoEstado y observaciones.
     * @return ResponseEntity con la orden actualizada.
     */
    @PutMapping("/{id}/progreso")
    public ResponseEntity<Map<String, Object>> actualizarProgreso(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            if (!request.containsKey("nuevoEstado")) {
                return createErrorResponse("El campo 'nuevoEstado' es obligatorio", HttpStatus.BAD_REQUEST);
            }

            EstadoOrden nuevoEstado = EstadoOrden.valueOf(request.get("nuevoEstado"));
            String observaciones = request.getOrDefault("observaciones", "");

            OrdenServicio ordenActualizada = ordenServicioService.actualizarProgreso(id, nuevoEstado, observaciones);
            return createSuccessResponse(ordenActualizada, "Progreso actualizado exitosamente");
        } catch (IllegalArgumentException e) {
            return createErrorResponse("Estado inválido: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return createErrorResponse("Error al actualizar progreso: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene todas las órdenes de servicio por estado.
     * @param estado El estado a filtrar (RECIBIDO, EN_DIAGNOSTICO, EN_REPARACION, etc.).
     * @return ResponseEntity con la lista de órdenes filtradas.
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<Map<String, Object>> obtenerOrdenesPorEstado(@PathVariable String estado) {
        try {
            EstadoOrden estadoOrden = EstadoOrden.valueOf(estado);
            List<OrdenServicio> ordenes = ordenServicioService.findByEstado(estadoOrden);
            return createSuccessResponse(ordenes, "Órdenes obtenidas exitosamente");
        } catch (IllegalArgumentException e) {
            return createErrorResponse("Estado inválido: " + estado, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return createErrorResponse("Error al obtener órdenes: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene todas las órdenes de servicio asignadas a un técnico.
     * @param tecnicoId El ID del técnico.
     * @return ResponseEntity con la lista de órdenes del técnico.
     */
    @GetMapping("/tecnico/{tecnicoId}")
    public ResponseEntity<Map<String, Object>> obtenerOrdenesPorTecnico(@PathVariable Long tecnicoId) {
        try {
            List<OrdenServicio> ordenes = ordenServicioService.findByTecnico(tecnicoId);
            return createSuccessResponse(ordenes, "Órdenes del técnico obtenidas exitosamente");
        } catch (Exception e) {
            return createErrorResponse("Error al obtener órdenes del técnico: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene todas las órdenes de servicio de un vehículo.
     * @param vehiculoId El ID del vehículo.
     * @return ResponseEntity con la lista de órdenes del vehículo.
     */
    @GetMapping("/vehiculo/{vehiculoId}")
    public ResponseEntity<Map<String, Object>> obtenerOrdenesPorVehiculo(@PathVariable Long vehiculoId) {
        try {
            List<OrdenServicio> ordenes = ordenServicioService.findByVehiculo(vehiculoId);
            return createSuccessResponse(ordenes, "Órdenes del vehículo obtenidas exitosamente");
        } catch (Exception e) {
            return createErrorResponse("Error al obtener órdenes del vehículo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Asigna un técnico a una orden de servicio.
     * @param id El ID de la orden de servicio.
     * @param request Objeto con el tecnicoId.
     * @return ResponseEntity con la orden actualizada.
     */
    @PutMapping("/{id}/asignar-tecnico")
    public ResponseEntity<Map<String, Object>> asignarTecnico(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request) {
        try {
            if (!request.containsKey("tecnicoId")) {
                return createErrorResponse("El campo 'tecnicoId' es obligatorio", HttpStatus.BAD_REQUEST);
            }

            Long tecnicoId = request.get("tecnicoId");

            Optional<OrdenServicio> ordenOpt = ordenServicioService.findById(id);
            if (!ordenOpt.isPresent()) {
                return createErrorResponse("Orden de servicio no encontrada", HttpStatus.NOT_FOUND);
            }

            Optional<Tecnico> tecnicoOpt = tecnicoService.findById(tecnicoId);
            if (!tecnicoOpt.isPresent()) {
                return createErrorResponse("Técnico no encontrado", HttpStatus.NOT_FOUND);
            }

            OrdenServicio orden = ordenOpt.get();
            orden.setTecnico(tecnicoOpt.get());
            OrdenServicio ordenActualizada = ordenServicioService.update(orden);

            return createSuccessResponse(ordenActualizada, "Técnico asignado exitosamente");
        } catch (Exception e) {
            return createErrorResponse("Error al asignar técnico: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Actualiza el costo estimado de una orden de servicio.
     * @param id El ID de la orden de servicio.
     * @param request Objeto con el costoEstimado.
     * @return ResponseEntity con la orden actualizada.
     */
    @PutMapping("/{id}/costo")
    public ResponseEntity<Map<String, Object>> actualizarCosto(
            @PathVariable Long id,
            @RequestBody Map<String, Double> request) {
        try {
            if (!request.containsKey("costoEstimado")) {
                return createErrorResponse("El campo 'costoEstimado' es obligatorio", HttpStatus.BAD_REQUEST);
            }

            Optional<OrdenServicio> ordenOpt = ordenServicioService.findById(id);
            if (!ordenOpt.isPresent()) {
                return createErrorResponse("Orden de servicio no encontrada", HttpStatus.NOT_FOUND);
            }

            OrdenServicio orden = ordenOpt.get();
            orden.setCostoEstimado(request.get("costoEstimado"));
            OrdenServicio ordenActualizada = ordenServicioService.update(orden);

            return createSuccessResponse(ordenActualizada, "Costo actualizado exitosamente");
        } catch (Exception e) {
            return createErrorResponse("Error al actualizar costo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Completa una orden de servicio.
     * @param id El ID de la orden de servicio.
     * @return ResponseEntity con la orden completada.
     */
    @PutMapping("/{id}/completar")
    public ResponseEntity<Map<String, Object>> completarOrden(@PathVariable Long id) {
        try {
            Optional<OrdenServicio> ordenOpt = ordenServicioService.findById(id);
            if (!ordenOpt.isPresent()) {
                return createErrorResponse("Orden de servicio no encontrada", HttpStatus.NOT_FOUND);
            }

            OrdenServicio orden = ordenOpt.get();
            orden.completarOrden();
            OrdenServicio ordenActualizada = ordenServicioService.update(orden);

            return createSuccessResponse(ordenActualizada, "Orden completada exitosamente");
        } catch (Exception e) {
            return createErrorResponse("Error al completar orden: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene órdenes de servicio por período de tiempo.
     * Útil para reportes (CU06).
     * @param fechaInicio Fecha de inicio en formato ISO (yyyy-MM-dd'T'HH:mm:ss).
     * @param fechaFin Fecha de fin en formato ISO (yyyy-MM-dd'T'HH:mm:ss).
     * @return ResponseEntity con la lista de órdenes en el período.
     */
    @GetMapping("/periodo")
    public ResponseEntity<Map<String, Object>> obtenerOrdenesPorPeriodo(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        try {
            LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
            LocalDateTime fin = LocalDateTime.parse(fechaFin);

            List<OrdenServicio> ordenes = ordenServicioService.findByPeriodo(inicio, fin);
            return createSuccessResponse(ordenes, "Órdenes del período obtenidas exitosamente");
        } catch (Exception e) {
            return createErrorResponse("Error al obtener órdenes por período: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
