package uniandes.edu.co.proyecto.controller;

import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uniandes.edu.co.proyecto.service.DisponibilidadService;

@RestController
@RequestMapping("/disponibilidades")
public class DisponibilidadController {

  private final DisponibilidadService service;

  public DisponibilidadController(DisponibilidadService service) { this.service = service; }

  @PostMapping(value = {"/registrar", "/new/save"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ResponseEntity<?> registrar(
      @RequestParam Long   idDisponibilidad,
      @RequestParam String dia,
      @RequestParam String horaInicio,   // HH:mm:ss
      @RequestParam String horaFin,      // HH:mm:ss
      @RequestParam String tipoServicio, // PASAJEROS|COMIDA|MERCANCIAS
      @RequestParam Long   idVehiculo,
      @RequestParam Long   idUsuarioConductor
  ) {
    try {
      var d = service.registrar(idDisponibilidad, dia, horaInicio, horaFin, tipoServicio, idVehiculo, idUsuarioConductor);
      return ResponseEntity.ok(Map.of(
          "idDisponibilidad", d.getIdDisponibilidad(),
          "dia", d.getDia(),
          "tipoServicio", d.getTipoServicio()
      ));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (IllegalStateException e) {
      return ResponseEntity.status(409).body(e.getMessage());
    } catch (RuntimeException e) {
      return ResponseEntity.status(404).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
    }
  }

  @GetMapping("/ping")
  public String ping() { return "disponibilidades-ok"; }
}
