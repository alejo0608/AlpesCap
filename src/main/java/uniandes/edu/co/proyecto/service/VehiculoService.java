// src/main/java/uniandes/edu/co/proyecto/service/VehiculoService.java
package uniandes.edu.co.proyecto.service;

import java.util.Optional;
import uniandes.edu.co.proyecto.modelo.Vehiculo;

public interface VehiculoService {
  Vehiculo registrar(Long idVehiculo, String tipo, String marca, String modelo, String color,
                     String placa, Integer capacidad, Long idUsuarioConductor, Long idCiudadExpedicion);
  Optional<Vehiculo> obtener(Long idVehiculo);
}
