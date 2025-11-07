package uniandes.edu.co.proyecto.service;

import java.util.Map;

import uniandes.edu.co.proyecto.web.SolicitudServicioRequest;

public interface SolicitudServicioService {
  Map<String,Object> solicitar(SolicitudServicioRequest req);
}
