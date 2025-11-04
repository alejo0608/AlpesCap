// src/main/java/uniandes/edu/co/proyecto/service/SolicitudDeServicioService.java
package uniandes.edu.co.proyecto.service;

import uniandes.edu.co.proyecto.web.SolicitarServicioRequest;
import uniandes.edu.co.proyecto.web.SolicitarServicioResponse;

public interface SolicitudServicioService {
  SolicitarServicioResponse solicitar(SolicitarServicioRequest req);
}
