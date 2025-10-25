package com.example.autofixpro.controller;

import com.example.autofixpro.entity.Usuario;
import com.example.autofixpro.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de usuarios.
 * Proporciona endpoints para CRUD de usuarios y cambio de contraseñas.
 *
 * Seguridad:
 * - Solo administradores pueden gestionar usuarios
 * - Métodos protegidos con @PreAuthorize
 */
@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Obtiene la lista de todos los usuarios.
     * GET /api/usuarios
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioService.listarTodos();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", usuarios);
            response.put("total", usuarios.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener usuarios: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Busca un usuario por su username.
     * GET /api/usuarios/{username}
     */
    @GetMapping("/{username}")
    public ResponseEntity<Map<String, Object>> buscarUsuario(@PathVariable String username) {
        try {
            return usuarioService.buscarPorUsername(username)
                    .map(usuario -> {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("data", usuario);
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        Map<String, Object> error = new HashMap<>();
                        error.put("success", false);
                        error.put("message", "Usuario no encontrado: " + username);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                    });
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al buscar usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Cambia la contraseña de un usuario.
     * POST /api/usuarios/cambiar-password
     *
     * Body JSON:
     * {
     *   "username": "admin",
     *   "newPassword": "nuevaPassword123"
     * }
     */
    @PostMapping("/cambiar-password")
    public ResponseEntity<Map<String, Object>> cambiarPassword(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String newPassword = request.get("newPassword");

            if (username == null || username.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "El campo 'username' es requerido");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "El campo 'newPassword' es requerido");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            if (newPassword.length() < 6) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "La contraseña debe tener al menos 6 caracteres");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            usuarioService.cambiarPasswordPorUsername(username, newPassword);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Contraseña actualizada exitosamente para el usuario: " + username);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al cambiar la contraseña: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Activa o desactiva un usuario.
     * PATCH /api/usuarios/{username}/toggle-activo
     */
    @PatchMapping("/{username}/toggle-activo")
    public ResponseEntity<Map<String, Object>> toggleActivo(@PathVariable String username) {
        try {
            return usuarioService.buscarPorUsername(username)
                    .map(usuario -> {
                        usuarioService.toggleActivo(usuario.getId());
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("message", "Estado del usuario actualizado");
                        response.put("username", username);
                        response.put("activo", !usuario.getActivo());
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        Map<String, Object> error = new HashMap<>();
                        error.put("success", false);
                        error.put("message", "Usuario no encontrado: " + username);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                    });
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al actualizar estado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Elimina un usuario por su username.
     * DELETE /api/usuarios/{username}
     */
    @DeleteMapping("/{username}")
    public ResponseEntity<Map<String, Object>> eliminarUsuario(@PathVariable String username) {
        try {
            return usuarioService.buscarPorUsername(username)
                    .map(usuario -> {
                        usuarioService.eliminar(usuario.getId());
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("message", "Usuario eliminado exitosamente: " + username);
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        Map<String, Object> error = new HashMap<>();
                        error.put("success", false);
                        error.put("message", "Usuario no encontrado: " + username);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                    });
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al eliminar usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Actualiza los datos del Cliente asociado a un Usuario.
     * PUT /api/usuarios/{username}/cliente
     *
     * Body JSON:
     * {
     *   "dni": "12345678",
     *   "nombres": "Juan Carlos",
     *   "apellidos": "Pérez Gómez",
     *   "telefono": "987654321",
     *   "email": "juan@example.com"
     * }
     */
    @PutMapping("/{username}/cliente")
    public ResponseEntity<Map<String, Object>> actualizarCliente(
            @PathVariable String username,
            @RequestBody Map<String, String> clienteData) {
        try {
            return usuarioService.buscarPorUsername(username)
                    .map(usuario -> {
                        if (usuario.getRole() != Usuario.Role.USER) {
                            Map<String, Object> error = new HashMap<>();
                            error.put("success", false);
                            error.put("message", "Solo los usuarios con role USER tienen un cliente asociado");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                        }

                        // Obtener o crear cliente
                        com.example.autofixpro.entity.Cliente cliente = usuario.getCliente();
                        if (cliente == null) {
                            cliente = new com.example.autofixpro.entity.Cliente();
                            cliente.setUsuario(usuario);
                            usuario.setCliente(cliente);
                        }

                        // Actualizar datos
                        if (clienteData.containsKey("dni")) {
                            cliente.setDni(clienteData.get("dni"));
                        }
                        if (clienteData.containsKey("nombres")) {
                            cliente.setNombres(clienteData.get("nombres"));
                        }
                        if (clienteData.containsKey("apellidos")) {
                            cliente.setApellidos(clienteData.get("apellidos"));
                        }
                        if (clienteData.containsKey("telefono")) {
                            cliente.setTelefono(clienteData.get("telefono"));
                        }
                        if (clienteData.containsKey("email")) {
                            cliente.setEmail(clienteData.get("email"));
                        }

                        // Guardar
                        usuarioService.actualizar(usuario);

                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("message", "Datos del cliente actualizados exitosamente");
                        response.put("cliente", cliente);
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        Map<String, Object> error = new HashMap<>();
                        error.put("success", false);
                        error.put("message", "Usuario no encontrado: " + username);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                    });
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al actualizar cliente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
