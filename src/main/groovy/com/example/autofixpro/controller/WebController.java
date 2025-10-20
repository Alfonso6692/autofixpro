package com.example.autofixpro.controller;

import com.example.autofixpro.entity.Cliente;
import com.example.autofixpro.entity.OrdenServicio;
import com.example.autofixpro.entity.Vehiculo;
import com.example.autofixpro.enumeration.EstadoOrden;
import com.example.autofixpro.service.ClienteService;
import com.example.autofixpro.service.VehiculoService;
import com.example.autofixpro.service.OrdenServicioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para manejar las vistas web principales de la aplicación.
 * Se encarga de renderizar las páginas del dashboard, clientes, vehículos, etc.
 */
@Controller
public class WebController {

    private static final Logger log = LoggerFactory.getLogger(WebController.class);

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private OrdenServicioService ordenServicioService;

    /**
     * Muestra el dashboard principal con métricas clave del taller.
     * @param model El modelo para la vista.
     * @return El nombre de la vista 'dashboard'.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("title", "AutoFixPro - Dashboard");
        model.addAttribute("message", "Sistema de Gestión de Taller Mecánico");

        // Obtener datos de clientes
        List<Cliente> clientes = clienteService.findAll();
        model.addAttribute("clientes", clientes);
        model.addAttribute("totalClientes", clientes.size());

        // Obtener datos de vehículos
        long totalVehiculos = vehiculoService.count();
        model.addAttribute("totalVehiculos", totalVehiculos);

        // Obtener órdenes activas (todas excepto COMPLETADO y ENTREGADO)
        List<OrdenServicio> todasOrdenes = ordenServicioService.findAll();
        long ordenesActivas = todasOrdenes.stream()
            .filter(orden -> orden.getEstadoOrden() != EstadoOrden.COMPLETADO &&
                           orden.getEstadoOrden() != EstadoOrden.ENTREGADO)
            .count();
        model.addAttribute("ordenesActivas", ordenesActivas);

        // Contar servicios completados (órdenes en estado COMPLETADO o ENTREGADO)
        long serviciosCompletados = todasOrdenes.stream()
            .filter(orden -> orden.getEstadoOrden() == EstadoOrden.COMPLETADO ||
                           orden.getEstadoOrden() == EstadoOrden.ENTREGADO)
            .count();
        model.addAttribute("serviciosCompletados", serviciosCompletados);

        return "dashboard";
    }

    /**
     * Muestra la página de inicio.
     * @param model El modelo para la vista.
     * @return El nombre de la vista 'index'.
     */
    @GetMapping("/web")
    public String home(Model model) {
        model.addAttribute("title", "AutoFixPro - Inicio");
        return "index";
    }

    /**
     * Muestra una lista de todos los vehículos registrados.
     * @param model El modelo para la vista.
     * @return El nombre de la vista 'vehiculos' o una redirección si ocurre un error.
     */
    @GetMapping("/vehiculos")
    public String listarVehiculos(Model model) {
        try {
            List<Vehiculo> vehiculos = vehiculoService.findAll();
            model.addAttribute("title", "AutoFixPro - Vehículos");
            model.addAttribute("vehiculos", vehiculos);
            model.addAttribute("totalVehiculos", vehiculos.size());
            return "vehiculos";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar vehículos: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }

    /**
     * Muestra la página de detalles de un cliente específico.
     * @param id El ID del cliente a visualizar.
     * @param model El modelo para la vista.
     * @param redirectAttributes Atributos para la redirección en caso de error.
     * @return El nombre de la vista 'cliente-detalle' o una redirección si el cliente no se encuentra.
     */
    @GetMapping("/clientes/{id}")
    public String verDetalleCliente(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        log.info("=== Accediendo a detalle del cliente ID: {} ===", id);
        try {
            log.info("Buscando cliente con ID: {}", id);
            Optional<Cliente> clienteOpt = clienteService.findById(id);

            if (clienteOpt.isPresent()) {
                Cliente cliente = clienteOpt.get();
                log.info("Cliente encontrado: {} {} (ID: {})", cliente.getNombres(), cliente.getApellidos(), cliente.getClienteId());
                model.addAttribute("title", "AutoFixPro - Detalle Cliente");
                model.addAttribute("cliente", cliente);
                log.info("Retornando vista: cliente-detalle");
                return "cliente-detalle";
            } else {
                log.warn("Cliente con ID {} NO encontrado", id);
                redirectAttributes.addFlashAttribute("error", "Cliente no encontrado");
                return "redirect:/dashboard";
            }
        } catch (Exception e) {
            log.error("ERROR al obtener cliente ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al obtener cliente: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }

    /**
     * Muestra la lista de vehículos pertenecientes a un cliente específico.
     * @param id El ID del cliente.
     * @param model El modelo para la vista.
     * @param redirectAttributes Atributos para la redirección en caso de error.
     * @return El nombre de la vista 'cliente-vehiculos' o una redirección si el cliente no se encuentra.
     */
    @GetMapping("/clientes/{id}/vehiculos")
    public String verVehiculosCliente(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Cliente> clienteOpt = clienteService.consultarClienteConVehiculos(id);
            if (clienteOpt.isPresent()) {
                Cliente cliente = clienteOpt.get();
                model.addAttribute("title", "AutoFixPro - Vehículos del Cliente");
                model.addAttribute("cliente", cliente);
                // Manejar caso cuando vehiculos es null
                model.addAttribute("vehiculos", cliente.getVehiculos() != null ? cliente.getVehiculos() : List.of());
                return "cliente-vehiculos";
            } else {
                redirectAttributes.addFlashAttribute("error", "Cliente no encontrado");
                return "redirect:/dashboard";
            }
        } catch (Exception e) {
            // Log del error completo
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al obtener vehículos: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }

    /**
     * Muestra el formulario para crear una nueva orden de servicio.
     * @param model El modelo para la vista.
     * @return El nombre de la vista 'nueva-orden'.
     */
    @GetMapping("/ordenes/nueva")
    public String nuevaOrdenForm(Model model) {
        model.addAttribute("title", "AutoFixPro - Nueva Orden de Servicio");
        return "nueva-orden";
    }

    /**
     * Muestra la lista de todas las órdenes de servicio.
     * @param model El modelo para la vista.
     * @return El nombre de la vista 'ordenes'.
     */
    @GetMapping("/ordenes")
    public String listarOrdenes(Model model) {
        model.addAttribute("title", "AutoFixPro - Órdenes de Servicio");
        return "ordenes";
    }

    /**
     * Muestra el detalle de una orden de servicio específica.
     * @param id El ID de la orden.
     * @param model El modelo para la vista.
     * @param redirectAttributes Atributos para la redirección en caso de error.
     * @return El nombre de la vista 'orden-detalle' o una redirección si la orden no se encuentra.
     */
    @GetMapping("/ordenes/{id}")
    public String verDetalleOrden(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<OrdenServicio> ordenOpt = ordenServicioService.findById(id);
            if (ordenOpt.isPresent()) {
                OrdenServicio orden = ordenOpt.get();
                model.addAttribute("title", "AutoFixPro - Orden #" + id);
                model.addAttribute("orden", orden);
                return "orden-detalle";
            } else {
                redirectAttributes.addFlashAttribute("error", "Orden de servicio no encontrada");
                return "redirect:/ordenes";
            }
        } catch (Exception e) {
            log.error("ERROR al obtener orden ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al obtener la orden: " + e.getMessage());
            return "redirect:/ordenes";
        }
    }
}