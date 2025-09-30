package uniandes.edu.co.proyecto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uniandes.edu.co.proyecto.repositorio.ReportesRepository;
import uniandes.edu.co.proyecto.repositorio.ReportesRepository.*;

import java.util.List;

@RestController
@RequestMapping("/reportes")
public class ReportesController {

  private final ReportesRepository repo;

  public ReportesController(ReportesRepository repo) { this.repo = repo; }

  /* ========= RFC1 ========= */
  @GetMapping("/historico/usuarios-servicio/{idUsuarioServicio}")
  public ResponseEntity<List<Rfc1HistoricoRow>> rfc1(
      @PathVariable Long idUsuarioServicio,
      @RequestParam(required = false) String fini,   // "YYYY-MM-DD" (opcional)
      @RequestParam(required = false) String ffin) { // "YYYY-MM-DD" (opcional)
    return ResponseEntity.ok(repo.rfc1HistoricoUsuario(idUsuarioServicio, fini, ffin));
  }

  /* ========= RFC2 ========= */
  @GetMapping("/top-conductores")
  public ResponseEntity<List<Rfc2TopConductoresRow>> rfc2(
      @RequestParam(required = false) String fini,   // "YYYY-MM-DD" (opcional)
      @RequestParam(required = false) String ffin) { // "YYYY-MM-DD" (opcional)
    return ResponseEntity.ok(repo.rfc2TopConductores(fini, ffin));
  }

  /* ========= RFC3 ========= */
  @GetMapping("/ganancias-conductor/{idConductor}")
  public ResponseEntity<List<Rfc3GananciasRow>> rfc3(
      @PathVariable Long idConductor,
      @RequestParam(required = false) String fini,   // "YYYY-MM-DD" (opcional)
      @RequestParam(required = false) String ffin) { // "YYYY-MM-DD" (opcional)
    return ResponseEntity.ok(repo.rfc3GananciasPorVehiculoYTipo(idConductor, fini, ffin));
  }

  /* ========= RFC4 ========= */
  @GetMapping("/utilizacion")
  public ResponseEntity<List<Rfc4UtilizacionRow>> rfc4(
      @RequestParam Long idCiudad,
      @RequestParam String fini,   // "YYYY-MM-DD"
      @RequestParam String ffin) { // "YYYY-MM-DD"
    return ResponseEntity.ok(repo.rfc4Utilizacion(idCiudad, fini, ffin));
  }
}
