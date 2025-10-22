package com.example.autofixpro.service;

import com.example.autofixpro.dao.VehiculoDAO;
import com.example.autofixpro.entity.Vehiculo;
import com.example.autofixpro.entity.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar la lógica de negocio de los vehículos.
 * Proporciona operaciones CRUD y métodos específicos para registrar, consultar y gestionar vehículos.
 */
@Service
@Transactional
public class VehiculoService implements GenericService<Vehiculo, Long> {

    @Autowired
    private VehiculoDAO vehiculoDAO;

    @Override
    public Vehiculo save(Vehiculo vehiculo) {
        return vehiculoDAO.save(vehiculo);
    }

    @Override
    public Optional<Vehiculo> findById(Long id) {
        return vehiculoDAO.findById(id);
    }

    @Override
    public List<Vehiculo> findAll() {
        return vehiculoDAO.findAll();
    }

    @Override
    public Vehiculo update(Vehiculo vehiculo) {
        return vehiculoDAO.save(vehiculo);
    }

    @Override
    public void deleteById(Long id) {
        vehiculoDAO.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return vehiculoDAO.existsById(id);
    }

    @Override
    public long count() {
        return vehiculoDAO.count();
    }

    // Métodos específicos del negocio

    /**
     * Registra un nuevo vehículo y lo asocia a un cliente.
     * Corresponde al CU07: Registrar vehículo.
     * @param vehiculo El vehículo a registrar.
     * @param cliente El cliente propietario del vehículo.
     * @return El vehículo guardado.
     */
    public Vehiculo registrarVehiculo(Vehiculo vehiculo, Cliente cliente) {
        vehiculo.setCliente(cliente);
        return vehiculoDAO.save(vehiculo);
    }

    /**
     * Consulta el estado de un vehículo por su número de placa.
     * Carga la información de las órdenes y sus estados asociados.
     * Corresponde al CU08: Consultar estado del vehículo.
     * @param placa La placa del vehículo a consultar.
     * @return Un Optional que contiene el vehículo con su información de estado.
     */
    public Optional<Vehiculo> consultarEstadoPorPlaca(String placa) {
        return vehiculoDAO.findByPlacaWithOrdenesAndEstados(placa);
    }

    /**
     * Obtiene el historial completo de servicios para un vehículo específico.
     * Corresponde al CU09: Ver historial de servicio.
     * @param vehiculoId El ID del vehículo.
     * @return Un Optional que contiene el vehículo con su historial de servicios.
     */
    public Optional<Vehiculo> obtenerHistorialServicios(Long vehiculoId) {
        return vehiculoDAO.findByIdWithHistorialServicios(vehiculoId);
    }

    /**
     * Busca un vehículo por su número de placa.
     * @param placa La placa del vehículo a buscar.
     * @return Un Optional que contiene el vehículo si se encuentra.
     */
    public Optional<Vehiculo> findByPlaca(String placa) {
        return vehiculoDAO.findByPlaca(placa);
    }

    /**
     * Busca todos los vehículos pertenecientes a un cliente específico.
     * @param clienteId El ID del cliente.
     * @return Una lista de vehículos del cliente.
     */
    public List<Vehiculo> findByCliente(Long clienteId) {
        return vehiculoDAO.findByClienteClienteId(clienteId);
    }

    /**
     * Busca vehículos por marca y modelo.
     * @param marca La marca del vehículo.
     * @param modelo El modelo del vehículo.
     * @return Una lista de vehículos que coinciden con la marca y el modelo.
     */
    public List<Vehiculo> findByMarcaAndModelo(String marca, String modelo) {
        return vehiculoDAO.findByMarcaAndModelo(marca, modelo);
    }
}