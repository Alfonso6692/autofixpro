package com.example.autofixpro.controller;

import com.example.autofixpro.entity.*;
import com.example.autofixpro.enumeration.Prioridad;
import com.example.autofixpro.service.OrdenServicioService;
import com.example.autofixpro.service.TecnicoService;
import com.example.autofixpro.service.UsuarioService;
import com.example.autofixpro.service.VehiculoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cliente")
public class ClienteApiController {

    private static final Logger log = LoggerFactory.getLogger(ClienteApiController.class);

    private final UsuarioService usuarioService;
    private final OrdenServicioService ordenServicioService;
    private final VehiculoService vehiculoService;
    private final TecnicoService tecnicoService;
    private final SimpMessagingTemplate messagingTemplate;

    public ClienteApiController(UsuarioService usuarioService, OrdenServicioService ordenServicioService, VehiculoService vehiculoService, TecnicoService tecnicoService, SimpMessagingTemplate messagingTemplate) {
        this.usuarioService = usuarioService;
        this.ordenServicioService = ordenServicioService;
        this.vehiculoService = vehiculoService;
        this.tecnicoService = tecnicoService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/solicitar-reparacion")
    public ResponseEntity<Map<String, Object>> solicitarReparacion(@RequestBody Map<String, Object> solicitud) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            log.info("Solicitud de reparación recibida de usuario: {}", username);

            if (!solicitud.containsKey("vehiculoId") || !solicitud.containsKey("descripcionProblema")) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Faltan datos requeridos"));
            }

            Long vehiculoId = Long.valueOf(solicitud.get("vehiculoId").toString());
            String descripcionProblema = solicitud.get("descripcionProblema").toString();
            String prioridadStr = solicitud.getOrDefault("prioridad", "NORMAL").toString();

            Vehiculo vehiculo = vehiculoService.findById(vehiculoId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

            Usuario usuario = usuarioService.buscarPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (vehiculo.getCliente() == null || vehiculo.getCliente().getUsuario() == null || !vehiculo.getCliente().getUsuario().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("success", false, "message", "No tienes permiso"));
            }

            OrdenServicio nuevaOrden = ordenServicioService.crearOrdenServicio(vehiculo, descripcionProblema);
            nuevaOrden.setPrioridad(Prioridad.valueOf(prioridadStr));

            Tecnico tecnicoAsignado = asignarTecnicoAutomaticamente();
            if (tecnicoAsignado != null) {
                nuevaOrden.setTecnico(tecnicoAsignado);
            }

            OrdenServicio ordenGuardada = ordenServicioService.save(nuevaOrden);
            log.info("Orden de servicio creada: #{}", ordenGuardada.getOrdenId());

            enviarNotificacionCliente(username, ordenGuardada);
            enviarNotificacionAdministrador(ordenGuardada, vehiculo.getCliente());

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("success", true);
            respuesta.put("message", "Solicitud creada exitosamente");
            respuesta.put("ordenId", ordenGuardada.getOrdenId());

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            log.error("Error al procesar solicitud de reparación", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/registrar-vehiculo")
    public ResponseEntity<Map<String, Object>> registrarVehiculo(@RequestBody Map<String, Object> datosVehiculo) {
        Map<String, Object> respuesta = new HashMap<>();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Usuario usuario = usuarioService.buscarPorUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Cliente cliente = usuario.getCliente();
            if (cliente == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "No se encontró cliente asociado"));
            }

            String placa = (String) datosVehiculo.get("placa");
            if (vehiculoService.findByPlaca(placa.toUpperCase()).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Placa ya registrada"));
            }

            Vehiculo nuevoVehiculo = new Vehiculo();
            nuevoVehiculo.setPlaca(placa.toUpperCase());
            nuevoVehiculo.setMarca((String) datosVehiculo.get("marca"));
            nuevoVehiculo.setModelo((String) datosVehiculo.get("modelo"));
            nuevoVehiculo.setYear(String.valueOf(datosVehiculo.get("anio")));
            nuevoVehiculo.setColor((String) datosVehiculo.get("color"));
            nuevoVehiculo.setKilometraje((Integer) datosVehiculo.getOrDefault("kilometraje", 0));
            nuevoVehiculo.setCliente(cliente);

            Vehiculo vehiculoGuardado = vehiculoService.save(nuevoVehiculo);

            respuesta.put("success", true);
            respuesta.put("message", "Vehículo registrado exitosamente");
            respuesta.put("vehiculo", vehiculoGuardado);

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            log.error("Error al registrar vehículo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private Tecnico asignarTecnicoAutomaticamente() {
        List<Tecnico> tecnicosDisponibles = tecnicoService.findTecnicosDisponibles();
        if (tecnicosDisponibles.isEmpty()) {
            return null;
        }
        return tecnicosDisponibles.stream()
            .min(Comparator.comparingLong(tecnico -> tecnico.getOrdenesAsignadas() != null ? tecnico.getOrdenesAsignadas().stream()
                .filter(o -> o.getEstadoOrden() != com.example.autofixpro.enumeration.EstadoOrden.COMPLETADO &&
                             o.getEstadoOrden() != com.example.autofixpro.enumeration.EstadoOrden.ENTREGADO)
                .count() : 0L))
            .orElse(null);
    }

    private void enviarNotificacionCliente(String username, OrdenServicio orden) {
        Map<String, Object> notificacion = new HashMap<>();
        notificacion.put("tipo", "SOLICITUD_CREADA");
        notificacion.put("titulo", "Solicitud de Reparación Creada");
        notificacion.put("mensaje", "Tu solicitud de reparación #" + orden.getOrdenId() + " ha sido creada.");
        notificacion.put("ordenId", orden.getOrdenId());
        notificacion.put("timestamp", new Date().getTime());
        messagingTemplate.convertAndSendToUser(username, "/queue/notificaciones", notificacion);
    }

    private void enviarNotificacionAdministrador(OrdenServicio orden, Cliente cliente) {
        List<Usuario> administradores = usuarioService.listarTodos().stream()
            .filter(u -> u.getRole() != null && u.getRole().name().contains("ADMIN"))
            .toList();

        for (Usuario admin : administradores) {
            Map<String, Object> notificacion = new HashMap<>();
            notificacion.put("tipo", "NUEVA_SOLICITUD");
            notificacion.put("titulo", "Nueva Solicitud de Reparación");
            notificacion.put("mensaje", "Nueva solicitud #" + orden.getOrdenId() + " de " + cliente.getNombres());
            notificacion.put("ordenId", orden.getOrdenId());
            messagingTemplate.convertAndSendToUser(admin.getUsername(), "/queue/notificaciones", notificacion);
        }
    }
}