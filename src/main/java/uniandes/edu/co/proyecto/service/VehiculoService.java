package uniandes.edu.co.proyecto.service;

import uniandes.edu.co.proyecto.modelo.Vehiculo;
import java.util.List;
import java.util.Optional;

public interface VehiculoService {
    Vehiculo registrarVehiculo(Vehiculo vehiculo);
    List<Vehiculo> obtenerTodos();
    Optional<Vehiculo> buscarPorId(Long id);
    void eliminarVehiculo(Long id);
}