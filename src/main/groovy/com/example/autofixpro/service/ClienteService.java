package com.example.autofixpro.service;

import com.example.autofixpro.dao.ClienteDAO;
import com.example.autofixpro.entity.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio que maneja las operaciones de negocio relacionadas con los clientes del taller mecánico.
 * Implementa el patrón de servicio para encapsular la lógica de negocio y actuar como intermediario
 * entre los controladores y la capa de acceso a datos.
 * 
 * @author AutoFixPro Team
 * @version 1.0
 * @since 1.0
 */
@Service // Marca esta clase como un servicio de Spring para inyección de dependencias
@Transactional // Asegura que todas las operaciones de base de datos sean transaccionales
public class ClienteService implements GenericService<Cliente, Long> {

    // Inyección de dependencias de Spring
    @Autowired
    private ClienteDAO clienteDAO; // DAO para operaciones CRUD básicas de cliente

    @Autowired
    private NotificacionServicio notificationService; // Servicio para envío de notificaciones

    /**
     * Guarda un nuevo cliente en el sistema y envía notificación de bienvenida.
     * Implementa el caso de uso CU07: Registrar vehículo (parte del cliente)
     * 
     * @param cliente El cliente a guardar
     * @return El cliente guardado con su ID asignado
     */
    @Override
    public Cliente save(Cliente cliente) {
        // CU07: Registrar vehículo (parte del cliente)
        Cliente savedCliente = clienteDAO.save(cliente);

        // Enviar notificación de registro automática al cliente
        notificationService.enviarNotificacionRegistro(savedCliente);

        return savedCliente;
    }

    /**
     * Busca un cliente por su ID único.
     * 
     * @param id El ID del cliente a buscar
     * @return Optional que contiene el cliente si existe, vacío si no se encuentra
     */
    @Override
    public Optional<Cliente> findById(Long id) {
        return clienteDAO.findById(id);
    }

    /**
     * Obtiene todos los clientes registrados en el sistema.
     * 
     * @return Lista completa de clientes
     */
    @Override
    public List<Cliente> findAll() {
        return clienteDAO.findAll();
    }

    /**
     * Actualiza los datos de un cliente existente.
     * 
     * @param cliente El cliente con los datos actualizados
     * @return El cliente actualizado
     */
    @Override
    public Cliente update(Cliente cliente) {
        return clienteDAO.save(cliente);
    }

    /**
     * Elimina un cliente del sistema por su ID.
     * 
     * @param id El ID del cliente a eliminar
     */
    @Override
    public void deleteById(Long id) {
        clienteDAO.deleteById(id);
    }

    /**
     * Verifica si existe un cliente con el ID especificado.
     * 
     * @param id El ID a verificar
     * @return true si el cliente existe, false en caso contrario
     */
    @Override
    public boolean existsById(Long id) {
        return clienteDAO.existsById(id);
    }

    /**
     * Cuenta el número total de clientes registrados.
     * 
     * @return Número total de clientes en el sistema
     */
    @Override
    public long count() {
        return clienteDAO.count();
    }

    // ==================== MÉTODOS ESPECÍFICOS DEL NEGOCIO ====================
    
    /**
     * Busca un cliente por su número de DNI (documento de identidad).
     * El DNI es único en el sistema, por lo que devuelve un Optional.
     * 
     * @param dni El DNI del cliente a buscar
     * @return Optional que contiene el cliente si existe
     */
    public Optional<Cliente> findByDni(String dni) {
        return clienteDAO.findByDni(dni);
    }

    /**
     * Busca un cliente por su dirección de email.
     * El email es único en el sistema.
     * 
     * @param email El email del cliente a buscar
     * @return Optional que contiene el cliente si existe
     */
    public Optional<Cliente> findByEmail(String email) {
        return clienteDAO.findByEmail(email);
    }

    /**
     * Busca clientes cuyo nombre contenga la cadena especificada.
     * Útil para implementar funcionalidad de búsqueda con autocompletado.
     * 
     * @param nombre Parte del nombre a buscar (búsqueda parcial)
     * @return Lista de clientes que coinciden con el criterio de búsqueda
     */
    public List<Cliente> buscarPorNombre(String nombre) {
        return clienteDAO.findByNombreContaining(nombre);
    }

    /**
     * Consulta un cliente junto con todos sus vehículos asociados.
     * Implementa el caso de uso CU01: Consultar estado del vehículo.
     * Utiliza fetch join para cargar los vehículos en una sola consulta y evitar el problema N+1.
     * 
     * @param clienteId El ID del cliente
     * @return Optional que contiene el cliente con sus vehículos cargados
     */
    // CU01: Consultar estado del vehículo
    public Optional<Cliente> consultarClienteConVehiculos(Long clienteId) {
        return clienteDAO.findByIdWithVehiculos(clienteId);
    }
}