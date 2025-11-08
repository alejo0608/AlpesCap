package uniandes.edu.co.proyecto.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import uniandes.edu.co.proyecto.service.ReportesService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reportes")
public class ReportesController {

  private final ReportesService service;


  public ReportesController(ReportesService service) {
    this.service = service;
  }

  // ===== RFC1 "normal" (histórico simple) =====

  // GET directo (útil para dashboards o pruebas rápidas)
  @GetMapping("/usuario/{idUsuarioServicio}/servicios")
  public List<Map<String,Object>> historicoUsuarioGet(@PathVariable Long idUsuarioServicio) {
    return service.historicoServiciosUsuario(idUsuarioServicio);
  }

  // POST x-www-form-urlencoded (tu formato estándar de pruebas)
  @PostMapping(
      value = "/usuario/servicios",
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
  )
  public List<Map<String,Object>> historicoUsuarioPost(@RequestParam("idUsuarioServicio") Long idUsuarioServicio) {
    return service.historicoServiciosUsuario(idUsuarioServicio);
  }

  // ===== RFC1 transaccional con espera y aislamiento =====
  // Body: idUsuarioServicio=11012&isolation=RC|SER&waitSeconds=30

  @PostMapping(
      value = "/usuario/servicios-tx",
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
  )
  public ResponseEntity<Map<String,Object>> historicoUsuarioTx(
      @RequestParam("idUsuarioServicio") Long idUsuarioServicio,
      @RequestParam(name = "isolation", defaultValue = "RC") String isolation,
      @RequestParam(name = "waitSeconds", defaultValue = "30") Integer waitSeconds
  ) {
    String iso = isolation == null ? "RC" : isolation.trim().toUpperCase();
    int wait = (waitSeconds == null || waitSeconds <= 0) ? 30 : waitSeconds;

    Map<String,Object> out;
    switch (iso) {
      case "SER":
      case "SERIALIZABLE":
        out = service.historicoUsuarioSerializable(idUsuarioServicio, wait);
        break;
      case "RC":
      case "READ_COMMITTED":
      default:
        out = service.historicoUsuarioReadCommitted(idUsuarioServicio, wait);
        break;
    }
    return ResponseEntity.ok(out);
  }

  // GET helpers opcionales (si quieres probar vía URL):
  @GetMapping("/usuario/{id}/servicios-tx/rc")
  public ResponseEntity<Map<String,Object>> historicoUsuarioTxRc(
      @PathVariable("id") Long idUsuario,
      @RequestParam(name = "waitSeconds", defaultValue = "30") Integer waitSeconds
  ) {
    return ResponseEntity.ok(service.historicoUsuarioReadCommitted(idUsuario, waitSeconds));
  }

  @GetMapping("/usuario/{id}/servicios-tx/ser")
  public ResponseEntity<Map<String,Object>> historicoUsuarioTxSer(
      @PathVariable("id") Long idUsuario,
      @RequestParam(name = "waitSeconds", defaultValue = "30") Integer waitSeconds
  ) {
    return ResponseEntity.ok(service.historicoUsuarioSerializable(idUsuario, waitSeconds));
  }

  // ===== RFC2 =====
  @GetMapping("/conductores/top20")
  public List<Map<String,Object>> top20ConductoresGet() {
    return service.top20Conductores();
  }

  @PostMapping(value="/conductores/top20", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public List<Map<String,Object>> top20ConductoresPost() {
    return service.top20Conductores();
  }

  // ===== RFC3 =====
  @GetMapping("/conductor/{id}/ingresos-vehiculos")
  public List<Map<String,Object>> ingresosVehiculosGet(
      @PathVariable("id") Long idConductor,
      @RequestParam(name="pctComision", defaultValue="0.15") Double pctComision) {
    return service.ingresosPorVehiculoYTipo(idConductor, pctComision);
  }

  @PostMapping(value="/conductor/ingresos-vehiculos", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public List<Map<String,Object>> ingresosVehiculosPost(
      @RequestParam("idConductor") Long idConductor,
      @RequestParam(name="pctComision", defaultValue="0.15") Double pctComision) {
    return service.ingresosPorVehiculoYTipo(idConductor, pctComision);
  }

  // ===== RFC4 =====
  @GetMapping("/ciudad/{id}/utilizacion")
  public List<Map<String,Object>> utilizacionCiudadGet(
      @PathVariable("id") Long idCiudad,
      @RequestParam("desde") String desdeYmd,
      @RequestParam("hasta") String hastaYmd) {
    return service.utilizacionServiciosCiudad(idCiudad, desdeYmd, hastaYmd);
  }

  @PostMapping(value="/ciudad/utilizacion", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public List<Map<String,Object>> utilizacionCiudadPost(
      @RequestParam("idCiudad") Long idCiudad,
      @RequestParam("desde")   String desdeYmd,
      @RequestParam("hasta")   String hastaYmd) {
    return service.utilizacionServiciosCiudad(idCiudad, desdeYmd, hastaYmd);
  }
}
