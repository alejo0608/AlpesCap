package uniandes.edu.co.proyecto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uniandes.edu.co.proyecto.repositorio.PagoRepository;

@RestController
@RequestMapping("/pagos")
public class PagoController {

  private final PagoRepository repo;

  public PagoController(PagoRepository repo) { this.repo = repo; }

  @PostMapping("/registrar")
  public ResponseEntity<String> registrarPago(
      @RequestParam Long idPago,
      @RequestParam Long idViaje,
      @RequestParam Double monto,
      @RequestParam String fecha,      // "YYYY-MM-DD"
      @RequestParam String estado      // APROBADO | RECHAZADO | EN ESPERA | PENDIENTE (según tu DDL)
  ) {
    // PK duplicada
    if (repo.existsById(idPago)) {
      return ResponseEntity.status(409).body("id_pago ya existe");
    }
    // FK viaje
    if (repo.countViaje(idViaje) == 0) {
      return ResponseEntity.status(404).body("Viaje no existe: " + idViaje);
    }
    // 1:1 pago↔viaje
    if (repo.countPagoPorViaje(idViaje) > 0) {
      return ResponseEntity.status(409).body("El viaje ya tiene pago registrado");
    }
    // Validación simple de monto
    if (monto == null || monto < 0) {
      return ResponseEntity.badRequest().body("monto inválido (>= 0)");
    }

    // Normalizar estado (deja que el CHECK de la BD valide exactamente)
    String estadoUp = estado == null ? null : estado.trim().toUpperCase();
    repo.insertarPago(idPago, monto, fecha, estadoUp, idViaje);
    return ResponseEntity.ok("Pago registrado: " + idPago);
  }

  @GetMapping("/{idPago}")
  public ResponseEntity<?> obtener(@PathVariable Long idPago) {
    return repo.findById(idPago)
        .<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
