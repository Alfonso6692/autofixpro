package com.example.autofixpro.service;

import com.example.autofixpro.dao.TecnicoDAO;
import com.example.autofixpro.entity.Tecnico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar la lógica de negocio de los técnicos del taller.
 * Proporciona operaciones CRUD y métodos específicos para la gestión de técnicos.
 */
@Service
@Transactional
public class TecnicoService implements GenericService<Tecnico, Long> {

    @Autowired
    private TecnicoDAO tecnicoDAO;

    @Override
    public Tecnico save(Tecnico tecnico) {
        return tecnicoDAO.save(tecnico);
    }

    @Override
    public Optional<Tecnico> findById(Long id) {
        return tecnicoDAO.findById(id);
    }

    @Override
    public List<Tecnico> findAll() {
        return tecnicoDAO.findAll();
    }

    @Override
    public Tecnico update(Tecnico tecnico) {
        return tecnicoDAO.save(tecnico);
    }

    @Override
    public void deleteById(Long id) {
        tecnicoDAO.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return tecnicoDAO.existsById(id);
    }

    @Override
    public long count() {
        return tecnicoDAO.count();
    }

    // Métodos específicos del negocio

    /**
     * Busca un técnico por su número de DNI.
     * @param dni El DNI del técnico a buscar.
     * @return Un Optional que contiene al técnico si se encuentra.
     */
    public Optional<Tecnico> findByDni(String dni) {
        return tecnicoDAO.findByDni(dni);
    }

    /**
     * Busca técnicos por su especialidad.
     * @param especialidad La especialidad a buscar (ej. "Mecánica General", "Electricidad").
     * @return Una lista de técnicos con esa especialidad.
     */
    public List<Tecnico> findByEspecialidad(String especialidad) {
        return tecnicoDAO.findByEspecialidad(especialidad);
    }

    /**
     * Busca técnicos según su estado de actividad.
     * @param estadoActivo true para activos, false para inactivos.
     * @return Una lista de técnicos que coinciden con el estado.
     */
    public List<Tecnico> findByEstadoActivo(Boolean estadoActivo) {
        return tecnicoDAO.findByEstadoActivo(estadoActivo);
    }

    /**
     * Encuentra todos los técnicos que están disponibles (activos).
     * @return Una lista de técnicos disponibles.
     */
    public List<Tecnico> findTecnicosDisponibles() {
        return tecnicoDAO.findTecnicosDisponibles();
    }

    /**
     * Busca un técnico y carga sus órdenes de servicio asociadas en la misma consulta.
     * @param tecnicoId El ID del técnico.
     * @return Un Optional que contiene al técnico con sus órdenes de servicio.
     */
    public Optional<Tecnico> findByIdWithOrdenes(Long tecnicoId) {
        return tecnicoDAO.findByIdWithOrdenes(tecnicoId);
    }

    /**
     * Registra un nuevo técnico, asegurando que su estado inicial sea activo.
     * @param tecnico El técnico a registrar.
     * @return El técnico guardado.
     */
    public Tecnico registrarTecnico(Tecnico tecnico) {
        if (tecnico.getEstadoActivo() == null) {
            tecnico.setEstadoActivo(true);
        }
        return tecnicoDAO.save(tecnico);
    }

    /**
     * Desactiva un técnico en el sistema.
     * @param tecnicoId El ID del técnico a desactivar.
     */
    public void desactivarTecnico(Long tecnicoId) {
        Optional<Tecnico> tecnicoOpt = tecnicoDAO.findById(tecnicoId);
        if (tecnicoOpt.isPresent()) {
            Tecnico tecnico = tecnicoOpt.get();
            tecnico.setEstadoActivo(false);
            tecnicoDAO.save(tecnico);
        }
    }

    /**
     * Activa un técnico en el sistema.
     * @param tecnicoId El ID del técnico a activar.
     */
    public void activarTecnico(Long tecnicoId) {
        Optional<Tecnico> tecnicoOpt = tecnicoDAO.findById(tecnicoId);
        if (tecnicoOpt.isPresent()) {
            Tecnico tecnico = tecnicoOpt.get();
            tecnico.setEstadoActivo(true);
            tecnicoDAO.save(tecnico);
        }
    }
}