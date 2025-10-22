package com.example.autofixpro.service;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica para servicios que proporcionan operaciones CRUD (Crear, Leer, Actualizar, Eliminar).
 * Define un contrato estándar para la gestión de entidades, promoviendo la reutilización de código.
 *
 * @param <T>  El tipo de la entidad que manejará el servicio.
 * @param <ID> El tipo del identificador de la entidad (por ejemplo, Long, String).
 */
public interface GenericService<T, ID> {

    /**
     * Guarda una entidad en la base de datos.
     * @param entity La entidad a guardar.
     * @return La entidad guardada (puede incluir el ID generado).
     */
    T save(T entity);

    /**
     * Busca una entidad por su identificador único.
     * @param id El ID de la entidad a buscar.
     * @return Un Optional que contiene la entidad si se encuentra, o un Optional vacío si no.
     */
    Optional<T> findById(ID id);

    /**
     * Obtiene todas las entidades de un tipo específico.
     * @return Una lista con todas las entidades.
     */
    List<T> findAll();

    /**
     * Actualiza una entidad existente.
     * @param entity La entidad con los datos actualizados.
     * @return La entidad actualizada.
     */
    T update(T entity);

    /**
     * Elimina una entidad por su identificador único.
     * @param id El ID de la entidad a eliminar.
     */
    void deleteById(ID id);

    /**
     * Verifica si una entidad con el ID especificado existe.
     * @param id El ID a verificar.
     * @return true si la entidad existe, false en caso contrario.
     */
    boolean existsById(ID id);

    /**
     * Cuenta el número total de entidades.
     * @return El número total de entidades.
     */
    long count();
}