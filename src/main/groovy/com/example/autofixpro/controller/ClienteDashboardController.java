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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
 * Controlador para el dashboard de clientes.
 * Permite a los clientes ver sus vehículos y el progreso de los servicios.
 */
@Controller
public class ClienteDashboardController {

    private static final Logger log = LoggerFactory.getLogger(ClienteDashboardController.class);

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ClienteDAO clienteDAO;

    @Autowired
    private OrdenServicioService ordenServicioService;

    /**
     * Muestra el dashboard principal del cliente.
     * @param model El modelo para la vista.
     * @return La vista del dashboard del cliente.
     */
    @GetMapping("/cliente-dashboard")
    public String clienteDashboard(Model model) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Usuario usuario = usuarioService.buscarPorUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Buscar el cliente asociado al usuario por email
        Cliente cliente = clienteDAO.findByEmail(usuario.getEmail()).orElse(null);

        List<Vehiculo> vehiculos = new ArrayList<>();

        if (cliente != null) {
            // Obtener los vehículos del cliente
            vehiculos = cliente.getVehiculos();

            // Inicializar las órdenes de servicio para cada vehículo
            if (vehiculos != null) {
                vehiculos.forEach(vehiculo -> {
                    // Forzar la carga de las órdenes de servicio (lazy loading)
                    if (vehiculo.getOrdenesServicio() != null) {
                        vehiculo.getOrdenesServicio().size();

                        // Para cada orden, inicializar el técnico y estados si existen
                        vehiculo.getOrdenesServicio().forEach(orden -> {
                            if (orden.getTecnico() != null) {
                                orden.getTecnico().getNombres(); // Inicializar técnico
                            }
                            if (orden.getEstadosVehiculo() != null) {
                                orden.getEstadosVehiculo().size(); // Inicializar estados
                            }
                        });
                    }
                });
            }
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("cliente", cliente);
        model.addAttribute("vehiculos", vehiculos);

        return "cliente-dashboard";
    }

    /**
     * Muestra los detalles de una orden de servicio específica.
     * @param id El ID de la orden.
     * @param model El modelo para la vista.
     * @return La vista de detalles de la orden.
     */
    @GetMapping("/cliente/orden/{id}")
    public String verDetallesOrden(@PathVariable Long id, Model model) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Usuario usuario = usuarioService.buscarPorUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Buscar la orden
        OrdenServicio orden = ordenServicioService.findById(id)
            .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        // Verificar que el cliente es el propietario del vehículo de esta orden
        if (orden.getVehiculo() != null && orden.getVehiculo().getCliente() != null) {
            if (orden.getVehiculo().getCliente().getUsuario() != null &&
                !orden.getVehiculo().getCliente().getUsuario().getUsername().equals(username)) {
                // No es su orden, redirigir al dashboard
                log.warn("Usuario {} intentó acceder a orden {} que no le pertenece", username, id);
                return "redirect:/cliente-dashboard";
            }
        }

        model.addAttribute("title", "Detalles de la Orden #" + id);
        model.addAttribute("usuario", usuario);
        model.addAttribute("orden", orden);

        return "cliente-orden-detalle";
    }

    /**
     * Muestra el perfil del cliente.
     * @param model El modelo para la vista.
     * @return La vista del perfil del cliente.
     */
    @GetMapping("/cliente/perfil")
    public String perfilCliente(Model model) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Usuario usuario = usuarioService.buscarPorUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Buscar el cliente asociado
        Cliente cliente = clienteDAO.findByEmail(usuario.getEmail()).orElse(null);

        // Calcular estadísticas
        int totalVehiculos = 0;
        int serviciosActivos = 0;
        int serviciosCompletados = 0;

        if (cliente != null && cliente.getVehiculos() != null) {
            totalVehiculos = cliente.getVehiculos().size();

            for (Vehiculo vehiculo : cliente.getVehiculos()) {
                if (vehiculo.getOrdenesServicio() != null) {
                    for (com.example.autofixpro.entity.OrdenServicio orden : vehiculo.getOrdenesServicio()) {
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

    /**
     * Cambia la contraseña del cliente.
     * @param passwordActual La contraseña actual.
     * @param nuevaPassword La nueva contraseña.
     * @param confirmarPassword Confirmación de la nueva contraseña.
     * @param redirectAttributes Atributos para la redirección.
     * @return Redirección al perfil.
     */
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

            // Validaciones
            if (!nuevaPassword.equals(confirmarPassword)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas nuevas no coinciden");
                return "redirect:/cliente/perfil";
            }

            if (nuevaPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres");
                return "redirect:/cliente/perfil";
            }

            // Cambiar la contraseña
            usuarioService.cambiarPassword(usuario.getId(), nuevaPassword);

            redirectAttributes.addFlashAttribute("message", "Contraseña actualizada exitosamente");
            return "redirect:/cliente/perfil";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar la contraseña: " + e.getMessage());
            return "redirect:/cliente/perfil";
        }
    }
}
