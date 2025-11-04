// src/main/java/uniandes/edu/co/proyecto/controller/DisponibilidadController.java
package uniandes.edu.co.proyecto.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uniandes.edu.co.proyecto.service.DisponibilidadService;

@RestController
@RequestMapping("/disponibilidades")
public class DisponibilidadController {

  private final DisponibilidadService service;

  public DisponibilidadController(DisponibilidadService service) {
    this.service = service;
  }

  // RF6: Modificar disponibilidad (día/horas/tipo)
  @PostMapping("/modificar")
  public ResponseEntity<?> modificar(
      @RequestParam Long idDisponibilidad,
      @RequestParam String dia,                  // LUNES..DOMINGO
      @RequestParam String horaInicio,           // "YYYY-MM-DD HH:MM:SS"
      @RequestParam String horaFin,              // "YYYY-MM-DD HH:MM:SS"
      @RequestParam String tipoServicio          // PASAJEROS|COMIDA|MERCANCIAS|MERCANCÍAS
  ) {
    try {
      var updated = service.modificar(idDisponibilidad, dia, horaInicio, horaFin, tipoServicio);
      return ResponseEntity.ok(Map.of(
        "idDisponibilidad", updated.get("idDisponibilidad"),
        "dia", updated.get("dia"),
        "horaInicio", updated.get("horaInicio"),
        "horaFin", updated.get("horaFin"),
        "tipoServicio", updated.get("tipoServicio")
      ));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());   // 400
    } catch (IllegalStateException e) {
      return ResponseEntity.status(409).body(e.getMessage());    // 409 (solape)
    } catch (RuntimeException e) {
      return ResponseEntity.status(404).body(e.getMessage());    // 404 (no existe)
    }
  }
}
