package com.example.autofixpro.controller;

import com.example.autofixpro.dto.OrdenServicioDTO;
import com.example.autofixpro.dto.VehiculoDTO;
import com.example.autofixpro.entity.Cliente;
import com.example.autofixpro.entity.OrdenServicio;
import com.example.autofixpro.entity.Vehiculo;
import com.example.autofixpro.service.VehiculoService;
import com.example.autofixpro.service.ClienteService;
import com.example.autofixpro.service.OrdenServicioService;
import com.example.autofixpro.service.sunarp.SunarpOrchestratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestionar las operaciones de los vehículos.
 * Proporciona endpoints para registrar, consultar y gestionar vehículos y su historial de servicios.
 */
@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin(origins = "*")
public class VehiculoController extends BaseController {

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private OrdenServicioService ordenServicioService;

    @Autowired
    private SunarpOrchestratorService sunarpService;

    /**
     * Obtiene una lista de todos los vehículos registrados.
     * @return ResponseEntity con la lista de vehículos.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listarVehiculos() {
        try {
            List<Vehiculo> vehiculos = vehiculoService.findAll();
            return createSuccessResponse(vehiculos, "Vehículos obtenidos exitosamente");
        } catch (Exception e) {
            return createErrorResponse("Error al obtener vehículos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Registra un nuevo vehículo para un cliente existente.
     * Corresponde al CU07: Registrar vehículo.
     * @param clienteId El ID del cliente al que pertenece el vehículo.
     * @param vehiculo El vehículo a registrar.
     * @return ResponseEntity con el vehículo registrado.
     */
    @PostMapping("/cliente/{clienteId}")
    public ResponseEntity<Map<String, Object>> registrarVehiculo(@PathVariable Long clienteId, @RequestBody Vehiculo vehiculo) {
        try {
            Optional<Cliente> clienteOpt = clienteService.findById(clienteId);
            if (clienteOpt.isPresent()) {
                Vehiculo nuevoVehiculo = vehiculoService.registrarVehiculo(vehiculo, clienteOpt.get());
                return createResponse(nuevoVehiculo, "Vehículo registrado exitosamente", HttpStatus.CREATED);
            } else {
                return createErrorResponse("Cliente no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return createErrorResponse("Error al registrar vehículo: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Consulta el estado de un vehículo por su placa.
     * Corresponde al CU08: Consultar estado del vehículo.
     * @param placa La placa del vehículo a consultar.
     * @return ResponseEntity con la información del estado del vehículo.
     */
    @GetMapping("/placa/{placa}/estado")
    public ResponseEntity<Map<String, Object>> consultarEstadoVehiculo(@PathVariable String placa) {
        try {
            Optional<Vehiculo> vehiculo = vehiculoService.consultarEstadoPorPlaca(placa);
            if (vehiculo.isPresent()) {
                return createSuccessResponse(vehiculo.get(), "Estado del vehículo obtenido exitosamente");
            } else {
                return createErrorResponse("Vehículo con placa " + placa + " no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return createErrorResponse("Error al consultar estado: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene el historial de servicios de un vehículo.
     * Corresponde al CU09: Ver historial de servicio.
     * @param id El ID del vehículo.
     * @return ResponseEntity con el historial de servicios del vehículo.
     */
    @GetMapping("/{id}/historial")
    public ResponseEntity<Map<String, Object>> obtenerHistorialServicios(@PathVariable Long id) {
        try {
            // Verificar que el vehículo existe
            if (!vehiculoService.existsById(id)) {
                return createErrorResponse("Vehículo no encontrado", HttpStatus.NOT_FOUND);
            }

            // Obtener órdenes directamente por vehículo ID para evitar MultipleBagFetchException
            List<OrdenServicioDTO> ordenesDTO = ordenServicioService.findHistorialByVehiculoId(id).stream()
                .map(OrdenServicioDTO::new)
                .collect(Collectors.toList());

            return createSuccessResponse(ordenesDTO, "Historial de servicios obtenido exitosamente");
        } catch (Exception e) {
            return createErrorResponse("Error al obtener historial: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene un vehículo por su ID.
     * @param id El ID del vehículo.
     * @return ResponseEntity con el vehículo encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerVehiculo(@PathVariable Long id) {
        try {
            Optional<Vehiculo> vehiculo = vehiculoService.findById(id);
            if (vehiculo.isPresent()) {
                return createSuccessResponse(vehiculo.get(), "Vehículo encontrado");
            } else {
                return createErrorResponse("Vehículo no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return createErrorResponse("Error al obtener vehículo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene todos los vehículos de un cliente específico.
     * @param clienteId El ID del cliente.
     * @return ResponseEntity con la lista de vehículos del cliente.
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Map<String, Object>> obtenerVehiculosPorCliente(@PathVariable Long clienteId) {
        try {
            List<Vehiculo> vehiculos = vehiculoService.findByCliente(clienteId);
            return createSuccessResponse(vehiculos, "Vehículos del cliente obtenidos exitosamente");
        } catch (Exception e) {
            return createErrorResponse("Error al obtener vehículos del cliente: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Actualiza la información de un vehículo existente.
     * @param id El ID del vehículo a actualizar.
     * @param vehiculo Los nuevos datos del vehículo.
     * @return ResponseEntity con el vehículo actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarVehiculo(@PathVariable Long id, @RequestBody Vehiculo vehiculo) {
        try {
            if (vehiculoService.existsById(id)) {
                vehiculo.setVehiculoId(id);
                Vehiculo vehiculoActualizado = vehiculoService.update(vehiculo);
                return createSuccessResponse(vehiculoActualizado, "Vehículo actualizado exitosamente");
            } else {
                return createErrorResponse("Vehículo no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return createErrorResponse("Error al actualizar vehículo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Consulta la información de un vehículo en SUNARP por su placa
     * Usa múltiples proveedores con fallback automático
     * @param placa La placa del vehículo a consultar.
     * @return ResponseEntity con los datos del vehículo desde SUNARP.
     */
    @GetMapping("/sunarp/{placa}")
    public ResponseEntity<Map<String, Object>> consultarPlacaSUNARP(@PathVariable String placa) {
        try {
            VehiculoDTO vehiculoDTO = sunarpService.consultarPlaca(placa);
            return createSuccessResponse(vehiculoDTO, "Datos del vehículo obtenidos desde SUNARP exitosamente");
        } catch (RuntimeException e) {
            return createErrorResponse("Error al consultar SUNARP: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return createErrorResponse("Error al consultar SUNARP: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene el estado de todos los proveedores SUNARP
     * @return ResponseEntity con información de cada proveedor
     */
    @GetMapping("/sunarp/providers/status")
    public ResponseEntity<Map<String, Object>> getProvidersStatus() {
        try {
            Map<String, Object> status = sunarpService.getProvidersStatus();
            List<String> available = sunarpService.getAvailableProviders();

            Map<String, Object> response = new HashMap<>();
            response.put("providers", status);
            response.put("available_providers", available);
            response.put("total_providers", status.size());
            response.put("available_count", available.size());

            return createSuccessResponse(response, "Estado de proveedores obtenido exitosamente");
        } catch (Exception e) {
            return createErrorResponse("Error al obtener estado de proveedores: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Consulta con un proveedor específico
     * @param placa La placa del vehículo
     * @param providerName Nombre del proveedor a usar
     * @return ResponseEntity con los datos del vehículo
     */
    @GetMapping("/sunarp/{placa}/provider/{providerName}")
    public ResponseEntity<Map<String, Object>> consultarConProveedor(
            @PathVariable String placa,
            @PathVariable String providerName) {
        try {
            VehiculoDTO vehiculoDTO = sunarpService.consultarConProveedor(placa, providerName);
            return createSuccessResponse(vehiculoDTO, "Datos obtenidos con proveedor: " + providerName);
        } catch (RuntimeException e) {
            return createErrorResponse("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return createErrorResponse("Error al consultar: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}