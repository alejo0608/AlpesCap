// src/main/java/uniandes/edu/co/proyecto/service/ServicioSolicitudService.java
package uniandes.edu.co.proyecto.service;

import java.util.Map;
import uniandes.edu.co.proyecto.web.SolicitarServicioRequest;

public interface ServicioSolicitudService {
  Map<String, Object> solicitarServicio(SolicitarServicioRequest in);
}
