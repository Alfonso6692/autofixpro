package com.example.autofixpro.controller;

import com.example.autofixpro.entity.Cliente;
import com.example.autofixpro.entity.Vehiculo;
import com.example.autofixpro.service.VehiculoService;
import com.example.autofixpro.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin(origins = "*")
public class VehiculoController extends BaseController {

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> listarVehiculos() {
        try {
            List<Vehiculo> vehiculos = vehiculoService.findAll();
            return createSuccessResponse(vehiculos, "Vehículos obtenidos exitosamente");
        } catch (Exception e) {
            return createErrorResponse("Error al obtener vehículos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // CU07: Registrar vehículo
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

    // CU08: Consultar estado del vehículo
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

    // CU09: Ver historial de servicio
    @GetMapping("/{id}/historial")
    public ResponseEntity<Map<String, Object>> obtenerHistorialServicios(@PathVariable Long id) {
        try {
            Optional<Vehiculo> vehiculo = vehiculoService.obtenerHistorialServicios(id);
            if (vehiculo.isPresent()) {
                return createSuccessResponse(vehiculo.get().getOrdenesServicio(), "Historial de servicios obtenido exitosamente");
            } else {
                return createErrorResponse("Vehículo no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return createErrorResponse("Error al obtener historial: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Map<String, Object>> obtenerVehiculosPorCliente(@PathVariable Long clienteId) {
        try {
            List<Vehiculo> vehiculos = vehiculoService.findByCliente(clienteId);
            return createSuccessResponse(vehiculos, "Vehículos del cliente obtenidos exitosamente");
        } catch (Exception e) {
            return createErrorResponse("Error al obtener vehículos del cliente: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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
}