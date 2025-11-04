// src/main/java/uniandes/edu/co/proyecto/controller/PagoController.java
package uniandes.edu.co.proyecto.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uniandes.edu.co.proyecto.service.PagoService;
import uniandes.edu.co.proyecto.web.PagoRequest;

@RestController
@RequestMapping("/pagos")
public class PagoController {

  private final PagoService service;

  public PagoController(PagoService service) {
    this.service = service;
  }

  @PostMapping("/registrar")
  public ResponseEntity<?> registrar(@RequestBody PagoRequest body) {
    try {
      var p = service.registrar(body.idPago(), body.idViaje(), body.monto(), body.fecha(), body.estado());
      return ResponseEntity.ok(Map.of(
          "idPago", p.getIdPago(),
          "idViaje", p.getViaje().getIdViaje(),
          "monto", p.getMonto(),
          "fecha", p.getFecha(),
          "estado", p.getEstado()
      ));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (IllegalStateException e) {
      return ResponseEntity.status(409).body(e.getMessage());
    } catch (RuntimeException e) {
      return ResponseEntity.status(404).body(e.getMessage());
    }
  }

  @PostMapping(value = "/registrar-form", consumes = "application/x-www-form-urlencoded")
  public ResponseEntity<?> registrarForm(
      @RequestParam Long idPago,
      @RequestParam Long idViaje,
      @RequestParam Double monto,
      @RequestParam String fecha,     // YYYY-MM-DD
      @RequestParam String estado     // EN ESPERA | COMPLETADO | RECHAZADO
  ) {
    try {
      var p = service.registrar(idPago, idViaje, monto, fecha, estado);
      return ResponseEntity.ok(Map.of(
          "idPago", p.getIdPago(),
          "idViaje", p.getViaje().getIdViaje(),
          "monto", p.getMonto(),
          "fecha", p.getFecha(),
          "estado", p.getEstado()
      ));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (IllegalStateException e) {
      return ResponseEntity.status(409).body(e.getMessage());
    } catch (RuntimeException e) {
      return ResponseEntity.status(404).body(e.getMessage());
    }
  }

  @GetMapping("/{idPago}")
  public ResponseEntity<?> obtener(@PathVariable Long idPago) {
    return service.obtener(idPago)
        .<ResponseEntity<?>>map(p -> ResponseEntity.ok(Map.of(
            "idPago", p.getIdPago(),
            "idViaje", p.getViaje().getIdViaje(),
            "monto", p.getMonto(),
            "fecha", p.getFecha(),
            "estado", p.getEstado()
        )))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PatchMapping("/{idPago}/estado")
  public ResponseEntity<?> actualizarEstado(
      @PathVariable Long idPago,
      @RequestParam String estado   // EN ESPERA | COMPLETADO | RECHAZADO
  ) {
    try {
      var p = service.actualizarEstado(idPago, estado);
      return ResponseEntity.ok(Map.of(
          "idPago", p.getIdPago(),
          "estado", p.getEstado()
      ));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (RuntimeException e) {
      return ResponseEntity.status(404).body(e.getMessage());
    }
  }
}
