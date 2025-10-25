package com.example.autofixpro.service;

import com.example.autofixpro.dao.UsuarioDAO;
import com.example.autofixpro.entity.Usuario;
import com.example.autofixpro.entity.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de usuarios y la integración con Spring Security.
 * Implementa UserDetailsService para la autenticación basada en nombre de usuario y contraseña.
 */
@Service
@Transactional
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Carga los detalles de un usuario por su nombre de usuario para Spring Security.
     * @param username El nombre de usuario.
     * @return Un objeto UserDetails con la información del usuario.
     * @throws UsernameNotFoundException si el usuario no se encuentra o está inactivo.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        if (!usuario.getActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name())
                ))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!usuario.getActivo())
                .build();
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * @param usuario El usuario a registrar.
     * @return El usuario guardado con la contraseña encriptada.
     * @throws RuntimeException si el nombre de usuario o el email ya existen.
     */
    public Usuario registrarUsuario(Usuario usuario) {
        return registrarUsuario(usuario, null);
    }

    /**
     * Registra un nuevo usuario en el sistema con DNI.
     * @param usuario El usuario a registrar.
     * @param dni El DNI del cliente (opcional).
     * @return El usuario guardado con la contraseña encriptada.
     * @throws RuntimeException si el nombre de usuario o el email ya existen.
     */
    public Usuario registrarUsuario(Usuario usuario, String dni) {
        if (usuarioDAO.existsByUsername(usuario.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        if (usuarioDAO.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Guardar el usuario primero
        Usuario usuarioGuardado = usuarioDAO.save(usuario);

        // Si el usuario es de tipo USER, crear automáticamente un Cliente asociado
        if (usuario.getRole() == Usuario.Role.USER) {
            Cliente cliente = new Cliente();

            // Separar nombre completo en nombres y apellidos
            String nombreCompleto = usuario.getNombre();
            String[] partes = nombreCompleto.split(" ", 2);
            cliente.setNombres(partes.length > 0 ? partes[0] : nombreCompleto);
            cliente.setApellidos(partes.length > 1 ? partes[1] : "");

            // Usar datos del usuario
            cliente.setEmail(usuario.getEmail());
            cliente.setTelefono(usuario.getTelefono() != null ? usuario.getTelefono() : "");
            cliente.setDni(dni != null && !dni.trim().isEmpty() ? dni.trim() : "");

            // Establecer la relación bidireccional
            cliente.setUsuario(usuarioGuardado);
            usuarioGuardado.setCliente(cliente);

            // Guardar nuevamente para persistir la relación
            usuarioGuardado = usuarioDAO.save(usuarioGuardado);
        }

        return usuarioGuardado;
    }

    /**
     * Busca un usuario por su nombre de usuario.
     * @param username El nombre de usuario a buscar.
     * @return Un Optional que contiene al usuario si se encuentra.
     */
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioDAO.findByUsername(username);
    }

    /**
     * Busca un usuario por su dirección de email.
     * @param email El email a buscar.
     * @return Un Optional que contiene al usuario si se encuentra.
     */
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioDAO.findByEmail(email);
    }

    /**
     * Busca un usuario por su ID.
     * @param id El ID del usuario.
     * @return Un Optional que contiene al usuario si se encuentra.
     */
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioDAO.findById(id);
    }

    /**
     * Obtiene una lista de todos los usuarios registrados.
     * @return Una lista de todos los usuarios.
     */
    public List<Usuario> listarTodos() {
        return usuarioDAO.findAll();
    }

    /**
     * Actualiza los datos de un usuario existente.
     * @param usuario El usuario con los datos actualizados.
     * @return El usuario actualizado.
     */
    public Usuario actualizar(Usuario usuario) {
        return usuarioDAO.save(usuario);
    }

    /**
     * Elimina un usuario por su ID.
     * @param id El ID del usuario a eliminar.
     */
    public void eliminar(Long id) {
        usuarioDAO.deleteById(id);
    }

    /**
     * Cambia la contraseña de un usuario.
     * @param id El ID del usuario.
     * @param nuevaPassword La nueva contraseña (sin encriptar).
     */
    public void cambiarPassword(Long id, String nuevaPassword) {
        Usuario usuario = usuarioDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioDAO.save(usuario);
    }

    /**
     * Cambia la contraseña de un usuario por su nombre de usuario.
     * @param username El nombre de usuario.
     * @param nuevaPassword La nueva contraseña (sin encriptar).
     */
    public void cambiarPasswordPorUsername(String username, String nuevaPassword) {
        Usuario usuario = usuarioDAO.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioDAO.save(usuario);
    }

    /**
     * Cambia el estado de activación de un usuario (activo/inactivo).
     * @param id El ID del usuario.
     */
    public void toggleActivo(Long id) {
        Usuario usuario = usuarioDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(!usuario.getActivo());
        usuarioDAO.save(usuario);
    }
}