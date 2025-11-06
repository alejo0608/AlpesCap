// src/main/java/uniandes/edu/co/proyecto/service/DisponibilidadService.java
package uniandes.edu.co.proyecto.service;

import java.util.Optional;
import uniandes.edu.co.proyecto.modelo.Disponibilidad;

public interface DisponibilidadService {
  Disponibilidad registrar(Long idDisponibilidad,
                           String dia, String horaInicioHHmmss, String horaFinHHmmss,
                           String tipoServicio,
                           Long idVehiculo, Long idUsuarioConductor);

  Optional<Disponibilidad> obtener(Long idDisponibilidad);

  // 🔹 RF6
  Disponibilidad modificar(Long idDisponibilidad,
                           String diaOpt, String horaInicioHHmmss, String horaFinHHmmss,
                           String tipoServicioOpt);
}
