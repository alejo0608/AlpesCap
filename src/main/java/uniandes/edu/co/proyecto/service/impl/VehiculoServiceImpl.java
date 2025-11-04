package uniandes.edu.co.proyecto.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uniandes.edu.co.proyecto.modelo.*;
import uniandes.edu.co.proyecto.repositorio.*;
import uniandes.edu.co.proyecto.service.VehiculoService;

import java.util.List;
import java.util.Optional;

@Service
public class VehiculoServiceImpl implements VehiculoService {

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private UsuarioConductorRepository usuarioConductorRepository;

    @Autowired
    private CiudadRepository ciudadRepository;

    @Override
    @Transactional
    public Vehiculo registrarVehiculo(Vehiculo vehiculo) {
        // Validaciones básicas
        if (vehiculo.getId() == null || vehiculo.getPlaca() == null || vehiculo.getConductor() == null ||
            vehiculo.getTipo() == null || vehiculo.getMarca() == null ||
            vehiculo.getCiudadExpedicion() == null) {
            throw new IllegalArgumentException("Todos los campos obligatorios deben estar completos");
        }

        // Verificar que la placa no esté repetida
        boolean existePlaca = vehiculoRepository.findAll().stream()
                .anyMatch(v -> v.getPlaca().equalsIgnoreCase(vehiculo.getPlaca()));
        if (existePlaca) {
            throw new IllegalArgumentException("Ya existe un vehículo con la placa " + vehiculo.getPlaca());
        }

        // Verificar que el conductor existe
        UsuarioConductor conductor = usuarioConductorRepository.findById(vehiculo.getConductor().getIdUsuarioConductor())
                .orElseThrow(() -> new IllegalArgumentException("El conductor especificado no existe"));

        // Verificar que la ciudad existe
        Ciudad ciudad = ciudadRepository.findById(vehiculo.getCiudadExpedicion().getIdCiudad())
                .orElseThrow(() -> new IllegalArgumentException("La ciudad de expedición no existe"));

        // Asociar entidades gestionadas
        vehiculo.setConductor(conductor);
        vehiculo.setCiudadExpedicion(ciudad);

        // Guardar el vehículo
        return vehiculoRepository.save(vehiculo);
    }

    @Override
    public List<Vehiculo> obtenerTodos() {
        return vehiculoRepository.findAll();
    }

    @Override
    public Optional<Vehiculo> buscarPorId(Long id) {
        return vehiculoRepository.findById(id);
    }

    @Override
    public void eliminarVehiculo(Long id) {
        vehiculoRepository.deleteById(id);
    }
}