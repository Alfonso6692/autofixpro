package com.example.autofixpro.controller;

import com.example.autofixpro.dao.ClienteDAO;
import com.example.autofixpro.entity.Cliente;
import com.example.autofixpro.entity.OrdenServicio;
import com.example.autofixpro.entity.Usuario;
import com.example.autofixpro.entity.Vehiculo;
import com.example.autofixpro.service.OrdenServicioService;
import com.example.autofixpro.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para el dashboard de clientes (vistas HTML).
 */
@Controller
public class ClienteDashboardController {

    private static final Logger log = LoggerFactory.getLogger(ClienteDashboardController.class);

    private final UsuarioService usuarioService;
    private final ClienteDAO clienteDAO;
    private final OrdenServicioService ordenServicioService;
    private final PasswordEncoder passwordEncoder;

    public ClienteDashboardController(UsuarioService usuarioService, ClienteDAO clienteDAO, OrdenServicioService ordenServicioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.clienteDAO = clienteDAO;
        this.ordenServicioService = ordenServicioService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/cliente-dashboard")
    public String clienteDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Usuario usuario = usuarioService.buscarPorUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Cliente cliente = clienteDAO.findByEmail(usuario.getEmail()).orElse(null);

        List<Vehiculo> vehiculos = new ArrayList<>();
        if (cliente != null) {
            vehiculos = cliente.getVehiculos();
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("cliente", cliente);
        model.addAttribute("vehiculos", vehiculos);

        return "cliente-dashboard";
    }

    @GetMapping("/cliente/orden/{id}")
    public String verDetallesOrden(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        OrdenServicio orden = ordenServicioService.findById(id)
            .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        if (orden.getVehiculo() != null && orden.getVehiculo().getCliente() != null) {
            if (orden.getVehiculo().getCliente().getUsuario() != null &&
                !orden.getVehiculo().getCliente().getUsuario().getUsername().equals(username)) {
                log.warn("Usuario {} intentó acceder a orden {} que no le pertenece", username, id);
                return "redirect:/cliente-dashboard";
            }
        }

        model.addAttribute("title", "Detalles de la Orden #" + id);
        model.addAttribute("orden", orden);

        return "cliente-orden-detalle";
    }

    @GetMapping("/cliente/perfil")
    public String perfilCliente(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Usuario usuario = usuarioService.buscarPorUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Cliente cliente = clienteDAO.findByEmail(usuario.getEmail()).orElse(null);

        int totalVehiculos = 0;
        int serviciosActivos = 0;
        int serviciosCompletados = 0;

        if (cliente != null && cliente.getVehiculos() != null) {
            totalVehiculos = cliente.getVehiculos().size();
            for (Vehiculo vehiculo : cliente.getVehiculos()) {
                if (vehiculo.getOrdenesServicio() != null) {
                    for (OrdenServicio orden : vehiculo.getOrdenesServicio()) {
                        if (orden.getEstadoOrden().toString().equals("COMPLETADO") ||
                            orden.getEstadoOrden().toString().equals("ENTREGADO")) {
                            serviciosCompletados++;
                        } else {
                            serviciosActivos++;
                        }
                    }
                }
            }
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("cliente", cliente);
        model.addAttribute("totalVehiculos", totalVehiculos);
        model.addAttribute("serviciosActivos", serviciosActivos);
        model.addAttribute("serviciosCompletados", serviciosCompletados);

        return "perfil-cliente";
    }

    @PostMapping("/cliente/perfil/cambiar-password")
    public String cambiarPasswordCliente(@RequestParam String passwordActual,
                                        @RequestParam String nuevaPassword,
                                        @RequestParam String confirmarPassword,
                                        RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            Usuario usuario = usuarioService.buscarPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
                redirectAttributes.addFlashAttribute("error", "La contraseña actual es incorrecta");
                return "redirect:/cliente/perfil";
            }

            if (!nuevaPassword.equals(confirmarPassword)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas nuevas no coinciden");
                return "redirect:/cliente/perfil";
            }

            if (nuevaPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres");
                return "redirect:/cliente/perfil";
            }

            usuarioService.cambiarPassword(usuario.getId(), nuevaPassword);

            redirectAttributes.addFlashAttribute("message", "Contraseña actualizada exitosamente");
            return "redirect:/cliente/perfil";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar la contraseña: " + e.getMessage());
            return "redirect:/cliente/perfil";
        }
    }
}
