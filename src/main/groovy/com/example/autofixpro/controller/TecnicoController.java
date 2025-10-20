package com.example.autofixpro.controller;

import com.example.autofixpro.entity.Tecnico;
import com.example.autofixpro.service.TecnicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para gestionar las operaciones de técnicos.
 */
@RestController
@RequestMapping("/api/tecnicos")
@CrossOrigin(origins = "*")
public class TecnicoController extends BaseController {

    @Autowired
    private TecnicoService tecnicoService;

    /**
     * Obtiene una lista de todos los técnicos.
     * @return ResponseEntity con la lista de técnicos.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listarTecnicos() {
        try {
            List<Tecnico> tecnicos = tecnicoService.findAll();
            return createSuccessResponse(tecnicos, "Técnicos obtenidos exitosamente");
        } catch (Exception e) {
            return createErrorResponse("Error al obtener técnicos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene un técnico por su ID.
     * @param id El ID del técnico.
     * @return ResponseEntity con el técnico encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerTecnico(@PathVariable Long id) {
        try {
            Optional<Tecnico> tecnico = tecnicoService.findById(id);
            if (tecnico.isPresent()) {
                return createSuccessResponse(tecnico.get(), "Técnico encontrado");
            } else {
                return createErrorResponse("Técnico no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return createErrorResponse("Error al obtener técnico: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene solo los técnicos activos.
     * @return ResponseEntity con la lista de técnicos activos.
     */
    @GetMapping("/activos")
    public ResponseEntity<Map<String, Object>> listarTecnicosActivos() {
        try {
            List<Tecnico> tecnicos = tecnicoService.findByEstadoActivo(true);
            return createSuccessResponse(tecnicos, "Técnicos activos obtenidos exitosamente");
        } catch (Exception e) {
            return createErrorResponse("Error al obtener técnicos activos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene técnicos disponibles (activos y con capacidad).
     * @return ResponseEntity con la lista de técnicos disponibles.
     */
    @GetMapping("/disponibles")
    public ResponseEntity<Map<String, Object>> listarTecnicosDisponibles() {
        try {
            List<Tecnico> tecnicos = tecnicoService.findTecnicosDisponibles();
            return createSuccessResponse(tecnicos, "Técnicos disponibles obtenidos exitosamente");
        } catch (Exception e) {
            return createErrorResponse("Error al obtener técnicos disponibles: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene técnicos por especialidad.
     * @param especialidad La especialidad a filtrar.
     * @return ResponseEntity con la lista de técnicos de esa especialidad.
     */
    @GetMapping("/especialidad/{especialidad}")
    public ResponseEntity<Map<String, Object>> obtenerPorEspecialidad(@PathVariable String especialidad) {
        try {
            List<Tecnico> tecnicos = tecnicoService.findByEspecialidad(especialidad);
            return createSuccessResponse(tecnicos, "Técnicos de la especialidad obtenidos exitosamente");
        } catch (Exception e) {
            return createErrorResponse("Error al obtener técnicos por especialidad: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Registra un nuevo técnico.
     * @param tecnico El técnico a registrar.
     * @return ResponseEntity con el técnico creado.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> registrarTecnico(@RequestBody Tecnico tecnico) {
        try {
            Tecnico nuevoTecnico = tecnicoService.registrarTecnico(tecnico);
            return createResponse(nuevoTecnico, "Técnico registrado exitosamente", HttpStatus.CREATED);
        } catch (Exception e) {
            return createErrorResponse("Error al registrar técnico: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Actualiza un técnico existente.
     * @param id El ID del técnico a actualizar.
     * @param tecnico Los nuevos datos del técnico.
     * @return ResponseEntity con el técnico actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarTecnico(@PathVariable Long id, @RequestBody Tecnico tecnico) {
        try {
            if (tecnicoService.existsById(id)) {
                tecnico.setTecnicoId(id);
                Tecnico tecnicoActualizado = tecnicoService.update(tecnico);
                return createSuccessResponse(tecnicoActualizado, "Técnico actualizado exitosamente");
            } else {
                return createErrorResponse("Técnico no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return createErrorResponse("Error al actualizar técnico: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Desactiva un técnico.
     * @param id El ID del técnico a desactivar.
     * @return ResponseEntity con mensaje de éxito.
     */
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Map<String, Object>> desactivarTecnico(@PathVariable Long id) {
        try {
            tecnicoService.desactivarTecnico(id);
            return createSuccessResponse(null, "Técnico desactivado exitosamente");
        } catch (Exception e) {
            return createErrorResponse("Error al desactivar técnico: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Activa un técnico.
     * @param id El ID del técnico a activar.
     * @return ResponseEntity con mensaje de éxito.
     */
    @PutMapping("/{id}/activar")
    public ResponseEntity<Map<String, Object>> activarTecnico(@PathVariable Long id) {
        try {
            tecnicoService.activarTecnico(id);
            return createSuccessResponse(null, "Técnico activado exitosamente");
        } catch (Exception e) {
            return createErrorResponse("Error al activar técnico: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
