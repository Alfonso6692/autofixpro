package com.example.autofixpro.controller;

import com.example.autofixpro.dto.ClienteDTO;
import com.example.autofixpro.entity.Cliente;
import com.example.autofixpro.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestionar las operaciones CRUD de los clientes.
 * Proporciona endpoints para listar, obtener, crear, actualizar y eliminar clientes.
 */
@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController extends BaseController {

    @Autowired
    private ClienteService clienteService;

    /**
     * Obtiene una lista de todos los clientes.
     * Corresponde al CU04: Gestionar usuarios.
     * @return ResponseEntity con la lista de clientes.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listarClientes() {
        try {
            List<Cliente> clientes = clienteService.findAll();
            // Convertir a DTO para evitar referencias circulares
            List<ClienteDTO> clientesDTO = clientes.stream()
                .map(ClienteDTO::new)
                .collect(Collectors.toList());
            return createSuccessResponse(clientesDTO, "Clientes obtenidos exitosamente");
        } catch (Exception e) {
            return createErrorResponse("Error al obtener clientes: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene un cliente por su ID.
     * @param id El ID del cliente.
     * @return ResponseEntity con el cliente encontrado o un error si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerCliente(@PathVariable Long id) {
        try {
            Optional<Cliente> cliente = clienteService.findById(id);
            if (cliente.isPresent()) {
                ClienteDTO clienteDTO = new ClienteDTO(cliente.get());
                return createSuccessResponse(clienteDTO, "Cliente encontrado");
            } else {
                return createErrorResponse("Cliente no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return createErrorResponse("Error al obtener cliente: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Crea un nuevo cliente.
     * @param cliente El cliente a crear.
     * @return ResponseEntity con el cliente creado.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearCliente(@RequestBody Cliente cliente) {
        try {
            Cliente nuevoCliente = clienteService.save(cliente);
            return createResponse(nuevoCliente, "Cliente creado exitosamente", HttpStatus.CREATED);
        } catch (Exception e) {
            return createErrorResponse("Error al crear cliente: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Actualiza un cliente existente.
     * @param id El ID del cliente a actualizar.
     * @param cliente Los nuevos datos del cliente.
     * @return ResponseEntity con el cliente actualizado.
     */
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

    /**
     * Elimina un cliente por su ID.
     * @param id El ID del cliente a eliminar.
     * @return ResponseEntity con un mensaje de éxito o error.
     */
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

    /**
     * Consulta los vehículos de un cliente específico.
     * Corresponde al CU01: Consultar estado del vehículo.
     * @param id El ID del cliente.
     * @return ResponseEntity con la lista de vehículos del cliente.
     */
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

    /**
     * Busca clientes por su nombre.
     * @param nombre El nombre a buscar.
     * @return ResponseEntity con la lista de clientes que coinciden con el nombre.
     */
    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> buscarClientesPorNombre(@RequestParam String nombre) {
        try {
            List<Cliente> clientes = clienteService.buscarPorNombre(nombre);
            List<ClienteDTO> clientesDTO = clientes.stream()
                .map(ClienteDTO::new)
                .collect(Collectors.toList());
            return createSuccessResponse(clientesDTO, "Búsqueda completada");
        } catch (Exception e) {
            return createErrorResponse("Error en búsqueda: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Busca un cliente por su DNI.
     * @param dni El DNI del cliente a buscar.
     * @return ResponseEntity con el cliente encontrado o un error si no existe.
     */
    @GetMapping("/dni/{dni}")
    public ResponseEntity<Map<String, Object>> buscarClientePorDni(@PathVariable String dni) {
        try {
            Optional<Cliente> cliente = clienteService.findByDni(dni);
            if (cliente.isPresent()) {
                ClienteDTO clienteDTO = new ClienteDTO(cliente.get());
                return createSuccessResponse(clienteDTO, "Cliente encontrado por DNI");
            } else {
                return createErrorResponse("Cliente con DNI " + dni + " no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return createErrorResponse("Error al buscar por DNI: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}