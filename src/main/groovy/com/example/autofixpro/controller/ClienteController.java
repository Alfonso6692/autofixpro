package com.example.autofixpro.controller;

import com.example.autofixpro.entity.Cliente;
import com.example.autofixpro.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController extends BaseController {

    @Autowired
    private ClienteService clienteService;

    // CU04: Gestionar usuarios (parte de clientes)
    @GetMapping
    public ResponseEntity<Map<String, Object>> listarClientes() {
        try {
            List<Cliente> clientes = clienteService.findAll();
            return createSuccessResponse(clientes, "Clientes obtenidos exitosamente");
        } catch (Exception e) {
            return createErrorResponse("Error al obtener clientes: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerCliente(@PathVariable Long id) {
        try {
            Optional<Cliente> cliente = clienteService.findById(id);
            if (cliente.isPresent()) {
                return createSuccessResponse(cliente.get(), "Cliente encontrado");
            } else {
                return createErrorResponse("Cliente no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return createErrorResponse("Error al obtener cliente: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearCliente(@RequestBody Cliente cliente) {
        try {
            Cliente nuevoCliente = clienteService.save(cliente);
            return createResponse(nuevoCliente, "Cliente creado exitosamente", HttpStatus.CREATED);
        } catch (Exception e) {
            return createErrorResponse("Error al crear cliente: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarCliente(@PathVariable Long id, @RequestBody Cliente cliente) {
        try {
            if (clienteService.existsById(id)) {
                cliente.setClienteId(id);
                Cliente clienteActualizado = clienteService.update(cliente);
                return createSuccessResponse(clienteActualizado, "Cliente actualizado exitosamente");
            } else {
                return createErrorResponse("Cliente no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return createErrorResponse("Error al actualizar cliente: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarCliente(@PathVariable Long id) {
        try {
            if (clienteService.existsById(id)) {
                clienteService.deleteById(id);
                return createSuccessResponse(null, "Cliente eliminado exitosamente");
            } else {
                return createErrorResponse("Cliente no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return createErrorResponse("Error al eliminar cliente: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // CU01: Consultar estado del vehículo (desde cliente)
    @GetMapping("/{id}/vehiculos")
    public ResponseEntity<Map<String, Object>> consultarVehiculosCliente(@PathVariable Long id) {
        try {
            Optional<Cliente> cliente = clienteService.consultarClienteConVehiculos(id);
            if (cliente.isPresent()) {
                return createSuccessResponse(cliente.get().getVehiculos(), "Vehículos del cliente obtenidos exitosamente");
            } else {
                return createErrorResponse("Cliente no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return createErrorResponse("Error al consultar vehículos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> buscarClientesPorNombre(@RequestParam String nombre) {
        try {
            List<Cliente> clientes = clienteService.buscarPorNombre(nombre);
            return createSuccessResponse(clientes, "Búsqueda completada");
        } catch (Exception e) {
            return createErrorResponse("Error en búsqueda: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<Map<String, Object>> buscarClientePorDni(@PathVariable String dni) {
        try {
            Optional<Cliente> cliente = clienteService.findByDni(dni);
            if (cliente.isPresent()) {
                return createSuccessResponse(cliente.get(), "Cliente encontrado por DNI");
            } else {
                return createErrorResponse("Cliente con DNI " + dni + " no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return createErrorResponse("Error al buscar por DNI: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}