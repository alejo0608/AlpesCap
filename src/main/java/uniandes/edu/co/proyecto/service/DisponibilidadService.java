// src/main/java/uniandes/edu/co/proyecto/service/DisponibilidadService.java
package uniandes.edu.co.proyecto.service;

import java.util.Map;

public interface DisponibilidadService {
  Map<String, Object> modificar(Long idDisponibilidad,
                                String dia,
                                String horaInicio,
                                String horaFin,
                                String tipoServicio);
}
