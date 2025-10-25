package com.example.autofixpro.dto;

import com.example.autofixpro.entity.Cliente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferir información de clientes sin referencias circulares.
 * Evita problemas de serialización JSON y mejora el rendimiento.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    private Long clienteId;
    private String nombres;
    private String apellidos;
    private String dni;
    private String telefono;
    private String email;
    private UsuarioInfo usuario;
    private int totalVehiculos;

    /**
     * Información resumida del usuario asociado.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsuarioInfo {
        private Long id;
        private String username;
        private String email;
        private String role;
        private Boolean activo;

        public UsuarioInfo(com.example.autofixpro.entity.Usuario usuario) {
            if (usuario != null) {
                this.id = usuario.getId();
                this.username = usuario.getUsername();
                this.email = usuario.getEmail();
                this.role = usuario.getRole() != null ? usuario.getRole().name() : null;
                this.activo = usuario.getActivo();
            }
        }
    }

    /**
     * Constructor que convierte una entidad Cliente a DTO.
     * @param cliente La entidad Cliente a convertir.
     */
    public ClienteDTO(Cliente cliente) {
        if (cliente != null) {
            this.clienteId = cliente.getClienteId();
            this.nombres = cliente.getNombres();
            this.apellidos = cliente.getApellidos();
            this.dni = cliente.getDni();
            this.telefono = cliente.getTelefono();
            this.email = cliente.getEmail();

            // Convertir usuario si existe
            if (cliente.getUsuario() != null) {
                this.usuario = new UsuarioInfo(cliente.getUsuario());
            }

            // Contar vehículos sin cargarlos completamente
            if (cliente.getVehiculos() != null) {
                this.totalVehiculos = cliente.getVehiculos().size();
            } else {
                this.totalVehiculos = 0;
            }
        }
    }

    /**
     * Obtiene el nombre completo del cliente.
     * @return Nombres y apellidos concatenados.
     */
    public String getNombreCompleto() {
        return this.nombres + " " + this.apellidos;
    }
}
