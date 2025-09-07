package com.example.autofixpro.service;

import com.example.autofixpro.dao.VehiculoDAO;
import com.example.autofixpro.entity.Vehiculo;
import com.example.autofixpro.entity.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    // CU07: Registrar vehículo
    public Vehiculo registrarVehiculo(Vehiculo vehiculo, Cliente cliente) {
        vehiculo.setCliente(cliente);
        return vehiculoDAO.save(vehiculo);
    }

    // CU08: Consultar estado del vehículo
    public Optional<Vehiculo> consultarEstadoPorPlaca(String placa) {
        return vehiculoDAO.findByPlacaWithOrdenesAndEstados(placa);
    }

    // CU09: Ver historial de servicio
    public Optional<Vehiculo> obtenerHistorialServicios(Long vehiculoId) {
        return vehiculoDAO.findByIdWithHistorialServicios(vehiculoId);
    }

    public Optional<Vehiculo> findByPlaca(String placa) {
        return vehiculoDAO.findByPlaca(placa);
    }

    public List<Vehiculo> findByCliente(Long clienteId) {
        return vehiculoDAO.findByClienteClienteId(clienteId);
    }

    public List<Vehiculo> findByMarcaAndModelo(String marca, String modelo) {
        return vehiculoDAO.findByMarcaAndModelo(marca, modelo);
    }
}