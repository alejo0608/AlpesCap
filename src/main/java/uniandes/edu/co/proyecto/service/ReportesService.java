package uniandes.edu.co.proyecto.service;

import java.util.List;
import java.util.Map;

public interface ReportesService {
  Map<String, Object> historicoUsuarioReadCommitted(Long idUsuarioServicio, int waitSeconds);
  Map<String, Object> historicoUsuarioSerializable(Long idUsuarioServicio, int waitSeconds);
  List<Map<String,Object>> historicoServiciosUsuario(Long idUsuarioServicio);

  // RFC2
  List<Map<String,Object>> top20Conductores();
  // RFC3
  List<Map<String,Object>> ingresosPorVehiculoYTipo(Long idConductor, Double pctComision);
  // RFC4
  List<Map<String,Object>> utilizacionServiciosCiudad(Long idCiudad, String desdeYmd, String hastaYmd);
}