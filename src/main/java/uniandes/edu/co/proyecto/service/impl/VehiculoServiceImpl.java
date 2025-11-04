// src/main/java/uniandes/edu/co/proyecto/service/impl/VehiculoServiceImpl.java
package uniandes.edu.co.proyecto.service.impl;

import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uniandes.edu.co.proyecto.modelo.Vehiculo;
import uniandes.edu.co.proyecto.repositorio.VehiculoRepository;
import uniandes.edu.co.proyecto.service.VehiculoService;

@Service
public class VehiculoServiceImpl implements VehiculoService {

  private final VehiculoRepository repo;

  public VehiculoServiceImpl(VehiculoRepository repo) { this.repo = repo; }

  @Override
  @Transactional
  public Vehiculo registrar(Long idVehiculo, String tipo, String marca, String modelo, String color,
                            String placa, Integer capacidad, Long idUsuarioConductor, Long idCiudadExpedicion) {

    if (idVehiculo == null || idVehiculo <= 0) throw new IllegalArgumentException("idVehiculo requerido y positivo");
    if (capacidad == null || capacidad <= 0)   throw new IllegalArgumentException("capacidad debe ser > 0");

    String t = (tipo == null ? "" : tipo.trim().toUpperCase(Locale.ROOT));
    if (!(t.equals("CARRO") || t.equals("CAMIONETA") || t.equals("MOTOCICLETA"))) {
      throw new IllegalArgumentException("tipo inválido (CARRO|CAMIONETA|MOTOCICLETA)");
    }

    String placaUp = (placa == null ? "" : placa.trim().toUpperCase(Locale.ROOT));

    if (repo.existsById(idVehiculo))                 throw new IllegalStateException("id_vehiculo ya existe");
    if (repo.countByPlaca(placaUp) > 0)              throw new IllegalStateException("placa ya registrada");
    if (repo.countConductor(idUsuarioConductor) == 0)throw new RuntimeException("Conductor no existe: " + idUsuarioConductor);
    if (repo.countCiudad(idCiudadExpedicion) == 0)   throw new RuntimeException("Ciudad no existe: " + idCiudadExpedicion);

    repo.insertarVehiculo(idVehiculo, t,
        marca == null ? "" : marca.trim(),
        modelo == null ? "" : modelo.trim(),
        color == null ? "" : color.trim(),
        placaUp, capacidad, idUsuarioConductor, idCiudadExpedicion);

    return repo.findById(idVehiculo)
        .orElseThrow(() -> new RuntimeException("No fue posible recuperar el vehículo insertado"));
  }

  @Override
  public Optional<Vehiculo> obtener(Long idVehiculo) { return repo.findById(idVehiculo); }
}
