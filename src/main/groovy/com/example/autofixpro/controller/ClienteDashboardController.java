package com.example.autofixpro.controller;

import com.example.autofixpro.dao.ClienteDAO;
import com.example.autofixpro.entity.Cliente;
import com.example.autofixpro.entity.OrdenServicio;
import com.example.autofixpro.entity.Tecnico;
import com.example.autofixpro.entity.Usuario;
import com.example.autofixpro.entity.Vehiculo;
import com.example.autofixpro.enumeration.Prioridad;
import com.example.autofixpro.service.OrdenServicioService;
import com.example.autofixpro.service.TecnicoService;
import com.example.autofixpro.service.UsuarioService;
import com.example.autofixpro.service.VehiculoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private TecnicoService tecnicoService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

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

    /**
     * Endpoint para que los clientes soliciten una reparación.
     * Crea una orden de servicio y asigna automáticamente un técnico disponible.
     * Envía notificaciones al administrador.
     *
     * @param solicitud Mapa con vehiculoId, descripcionProblema, prioridad, tipoProblema
     * @return ResponseEntity con el resultado de la operación
     */
    @PostMapping("/cliente/solicitar-reparacion")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> solicitarReparacion(@RequestBody Map<String, Object> solicitud) {
        try {
            // Obtener el usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            log.info("Solicitud de reparación recibida de usuario: {}", username);

            // Validar datos de la solicitud
            if (!solicitud.containsKey("vehiculoId") || !solicitud.containsKey("descripcionProblema")) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Faltan datos requeridos en la solicitud"
                ));
            }

            // Obtener datos de la solicitud
            Long vehiculoId = Long.valueOf(solicitud.get("vehiculoId").toString());
            String descripcionProblema = solicitud.get("descripcionProblema").toString();
            String prioridadStr = solicitud.getOrDefault("prioridad", "NORMAL").toString();

            // Obtener el vehículo
            Vehiculo vehiculo = vehiculoService.findById(vehiculoId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

            // Verificar que el vehículo pertenece al cliente autenticado
            Usuario usuario = usuarioService.buscarPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (vehiculo.getCliente() == null ||
                vehiculo.getCliente().getUsuario() == null ||
                !vehiculo.getCliente().getUsuario().getUsername().equals(username)) {
                log.warn("Usuario {} intentó solicitar reparación para vehículo {} que no le pertenece", username, vehiculoId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", "No tienes permiso para solicitar reparación de este vehículo"
                ));
            }

            // Crear la orden de servicio
            OrdenServicio nuevaOrden = ordenServicioService.crearOrdenServicio(vehiculo, descripcionProblema);

            // Configurar prioridad
            try {
                Prioridad prioridad = Prioridad.valueOf(prioridadStr);
                nuevaOrden.setPrioridad(prioridad);
            } catch (IllegalArgumentException e) {
                nuevaOrden.setPrioridad(Prioridad.NORMAL);
            }

            // Asignar técnico automáticamente según disponibilidad
            Tecnico tecnicoAsignado = asignarTecnicoAutomaticamente(nuevaOrden);
            if (tecnicoAsignado != null) {
                nuevaOrden.setTecnico(tecnicoAsignado);
                log.info("Técnico asignado automáticamente: {} {} (ID: {})",
                    tecnicoAsignado.getNombres(), tecnicoAsignado.getApellidos(), tecnicoAsignado.getTecnicoId());
            }

            // Guardar la orden
            OrdenServicio ordenGuardada = ordenServicioService.save(nuevaOrden);

            log.info("Orden de servicio creada exitosamente: #{}", ordenGuardada.getOrdenId());

            // Enviar notificación al cliente
            enviarNotificacionCliente(usuario.getUsername(), ordenGuardada);

            // Enviar notificación al administrador
            enviarNotificacionAdministrador(ordenGuardada, vehiculo.getCliente());

            // Preparar respuesta
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("success", true);
            respuesta.put("message", "Solicitud de reparación creada exitosamente");
            respuesta.put("ordenId", ordenGuardada.getOrdenId());
            respuesta.put("tecnicoAsignado", tecnicoAsignado != null ?
                tecnicoAsignado.getNombres() + " " + tecnicoAsignado.getApellidos() : "Por asignar");

            return ResponseEntity.ok(respuesta);

        } catch (NumberFormatException e) {
            log.error("Error de formato en los datos de la solicitud", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Formato de datos inválido"
            ));
        } catch (Exception e) {
            log.error("Error al procesar solicitud de reparación", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error al procesar la solicitud: " + e.getMessage()
            ));
        }
    }

    /**
     * Asigna automáticamente un técnico disponible a la orden de servicio.
     * Selecciona el técnico con menos órdenes activas asignadas.
     *
     * @param orden La orden de servicio a asignar
     * @return El técnico asignado o null si no hay técnicos disponibles
     */
    private Tecnico asignarTecnicoAutomaticamente(OrdenServicio orden) {
        try {
            // Obtener todos los técnicos disponibles (activos)
            List<Tecnico> tecnicosDisponibles = tecnicoService.findTecnicosDisponibles();

            if (tecnicosDisponibles.isEmpty()) {
                log.warn("No hay técnicos disponibles para asignación automática");
                return null;
            }

            // Encontrar el técnico con menos carga de trabajo
            Tecnico tecnicoMenosCargado = null;
            int menorCantidadOrdenes = Integer.MAX_VALUE;

            for (Tecnico tecnico : tecnicosDisponibles) {
                // Contar las órdenes activas del técnico (no completadas ni entregadas)
                long ordenesActivas = tecnicoService.findByIdWithOrdenes(tecnico.getTecnicoId())
                    .map(t -> {
                        if (t.getOrdenesAsignadas() == null) return 0L;
                        return t.getOrdenesAsignadas().stream()
                            .filter(o -> o.getEstadoOrden() != com.example.autofixpro.enumeration.EstadoOrden.COMPLETADO &&
                                         o.getEstadoOrden() != com.example.autofixpro.enumeration.EstadoOrden.ENTREGADO)
                            .count();
                    })
                    .orElse(0L);

                if (ordenesActivas < menorCantidadOrdenes) {
                    menorCantidadOrdenes = (int) ordenesActivas;
                    tecnicoMenosCargado = tecnico;
                }
            }

            if (tecnicoMenosCargado != null) {
                log.info("Técnico seleccionado para asignación automática: {} {} con {} órdenes activas",
                    tecnicoMenosCargado.getNombres(),
                    tecnicoMenosCargado.getApellidos(),
                    menorCantidadOrdenes);
            }

            return tecnicoMenosCargado;

        } catch (Exception e) {
            log.error("Error al asignar técnico automáticamente", e);
            return null;
        }
    }

    /**
     * Envía notificación al cliente sobre la creación de su solicitud.
     *
     * @param username El username del cliente
     * @param orden La orden creada
     */
    private void enviarNotificacionCliente(String username, OrdenServicio orden) {
        try {
            Map<String, Object> notificacion = new HashMap<>();
            notificacion.put("tipo", "SOLICITUD_CREADA");
            notificacion.put("titulo", "Solicitud de Reparación Creada");
            notificacion.put("mensaje", String.format(
                "Tu solicitud de reparación #%d ha sido creada exitosamente. %s",
                orden.getOrdenId(),
                orden.getTecnico() != null ?
                    "Técnico asignado: " + orden.getTecnico().getNombres() + " " + orden.getTecnico().getApellidos() :
                    "En breve se te asignará un técnico."
            ));
            notificacion.put("ordenId", orden.getOrdenId());
            notificacion.put("timestamp", new Date().getTime());

            messagingTemplate.convertAndSendToUser(
                username,
                "/queue/notificaciones",
                notificacion
            );

            log.info("Notificación enviada al cliente: {}", username);
        } catch (Exception e) {
            log.error("Error al enviar notificación al cliente", e);
        }
    }

    /**
     * Envía notificación al administrador sobre la nueva solicitud.
     *
     * @param orden La orden creada
     * @param cliente El cliente que solicita
     */
    private void enviarNotificacionAdministrador(OrdenServicio orden, Cliente cliente) {
        try {
            // Buscar usuarios administradores
            List<Usuario> administradores = usuarioService.listarTodos().stream()
                .filter(u -> u.getRole() != null &&
                           (u.getRole().name().equals("ROLE_ADMIN") || u.getRole().name().equals("ADMIN")))
                .collect(Collectors.toList());

            if (administradores.isEmpty()) {
                log.warn("No se encontraron administradores para enviar notificación");
                return;
            }

            for (Usuario admin : administradores) {
                Map<String, Object> notificacion = new HashMap<>();
                notificacion.put("tipo", "NUEVA_SOLICITUD");
                notificacion.put("titulo", "Nueva Solicitud de Reparación");
                notificacion.put("mensaje", String.format(
                    "Nueva solicitud de reparación #%d de %s %s para vehículo %s %s (Placa: %s). Prioridad: %s",
                    orden.getOrdenId(),
                    cliente.getNombres(),
                    cliente.getApellidos(),
                    orden.getVehiculo().getMarca(),
                    orden.getVehiculo().getModelo(),
                    orden.getVehiculo().getPlaca(),
                    orden.getPrioridad()
                ));
                notificacion.put("ordenId", orden.getOrdenId());
                notificacion.put("prioridad", orden.getPrioridad().toString());
                notificacion.put("clienteNombre", cliente.getNombres() + " " + cliente.getApellidos());
                notificacion.put("vehiculoInfo", orden.getVehiculo().getMarca() + " " + orden.getVehiculo().getModelo());
                notificacion.put("timestamp", new Date().getTime());

                messagingTemplate.convertAndSendToUser(
                    admin.getUsername(),
                    "/queue/notificaciones",
                    notificacion
                );

                log.info("Notificación enviada al administrador: {}", admin.getUsername());
            }
        } catch (Exception e) {
            log.error("Error al enviar notificación al administrador", e);
        }
    }
}
