package uniandes.edu.co.proyecto.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import uniandes.edu.co.proyecto.repositorio.ReportesRepository;
import uniandes.edu.co.proyecto.service.ReportesService;

@Service
public class ReportesServiceImpl implements ReportesService {

  private final ReportesRepository reportesRepo;
  private final ReportesRepository repo;


  public ReportesServiceImpl(ReportesRepository reportesRepo) {
    this.reportesRepo = reportesRepo;
    this.repo = reportesRepo;
  }

  private static void sleepSeconds(int s) {
    try { Thread.sleep(Math.max(1, s) * 1000L); }
    catch (InterruptedException ie) { Thread.currentThread().interrupt(); throw new RuntimeException(ie); }
  }

  private static void requirePos(Long v, String msg) {
    if (v == null || v <= 0) throw new IllegalArgumentException(msg);
  }
  private static double normPct(Double p) {
    double x = (p == null ? 0.15 : p.doubleValue());
    if (x < 0 || x >= 1) throw new IllegalArgumentException("pctComision debe estar entre 0 y 1");
    return x;
  }
  private static void validaFechaYmd(String s, String param) {
    if (s == null || !s.matches("\\d{4}-\\d{2}-\\d{2}"))
      throw new IllegalArgumentException(param + " debe ser YYYY-MM-DD");
  }

  // ===== RFC1 normal (sin aislamiento/espera) =====
  @Override
  @Transactional(readOnly = true)
  public List<Map<String, Object>> historicoServiciosUsuario(Long idUsuarioServicio) {
    if (idUsuarioServicio == null || idUsuarioServicio <= 0)
      throw new IllegalArgumentException("idUsuarioServicio requerido y positivo");
    return reportesRepo.historicoServiciosUsuario(idUsuarioServicio);
  }

  // ===== RFC1 transaccional: READ COMMITTED =====
  @Override
  @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, timeout = 60)
  public Map<String, Object> historicoUsuarioReadCommitted(Long idUsuarioServicio, int waitSeconds) {
    if (idUsuarioServicio == null || idUsuarioServicio <= 0)
      throw new IllegalArgumentException("idUsuarioServicio requerido y positivo");

    List<ReportesRepository.HistoricoServicioRow> snap1 =
        reportesRepo.historicoServiciosUsuarioRows(idUsuarioServicio);

    sleepSeconds(waitSeconds <= 0 ? 30 : waitSeconds);

    List<ReportesRepository.HistoricoServicioRow> snap2 =
        reportesRepo.historicoServiciosUsuarioRows(idUsuarioServicio);

    Map<String,Object> out = new HashMap<>();
    out.put("isolation", "READ_COMMITTED");
    out.put("waitSeconds", waitSeconds <= 0 ? 30 : waitSeconds);
    out.put("snapshot1Count", snap1.size());
    out.put("snapshot2Count", snap2.size());
    out.put("snapshot1", snap1);
    out.put("snapshot2", snap2);
    return out;
  }

  // ===== RFC1 transaccional: SERIALIZABLE =====
  @Override
  @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE, timeout = 60)
  public Map<String, Object> historicoUsuarioSerializable(Long idUsuarioServicio, int waitSeconds) {
    if (idUsuarioServicio == null || idUsuarioServicio <= 0)
      throw new IllegalArgumentException("idUsuarioServicio requerido y positivo");

    List<ReportesRepository.HistoricoServicioRow> snap1 =
        reportesRepo.historicoServiciosUsuarioRows(idUsuarioServicio);

    sleepSeconds(waitSeconds <= 0 ? 30 : waitSeconds);

    List<ReportesRepository.HistoricoServicioRow> snap2 =
        reportesRepo.historicoServiciosUsuarioRows(idUsuarioServicio);

    Map<String,Object> out = new HashMap<>();
    out.put("isolation", "SERIALIZABLE");
    out.put("waitSeconds", waitSeconds <= 0 ? 30 : waitSeconds);
    out.put("snapshot1Count", snap1.size());
    out.put("snapshot2Count", snap2.size());
    out.put("snapshot1", snap1);
    out.put("snapshot2", snap2);
    return out;
  }

  @Override
  @Transactional(readOnly = true)
  public List<Map<String, Object>> top20Conductores() {
    return repo.top20Conductores();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Map<String, Object>> ingresosPorVehiculoYTipo(Long idConductor, Double pctComision) {
    requirePos(idConductor, "idConductor requerido y positivo");
    double pct = normPct(pctComision);
    return repo.ingresosPorVehiculoYTipo(idConductor, pct);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Map<String, Object>> utilizacionServiciosCiudad(Long idCiudad, String desdeYmd, String hastaYmd) {
    requirePos(idCiudad, "idCiudad requerida y positiva");
    validaFechaYmd(desdeYmd, "desde");
    validaFechaYmd(hastaYmd, "hasta");
    return repo.utilizacionServiciosCiudad(idCiudad, desdeYmd, hastaYmd);
  }
}