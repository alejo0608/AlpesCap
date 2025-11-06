// src/main/java/uniandes/edu/co/proyecto/controller/DisponibilidadController.java
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

  @GetMapping("/{idDisponibilidad}")
  public ResponseEntity<?> obtener(@PathVariable Long idDisponibilidad) {
    return service.obtener(idDisponibilidad)
      .<ResponseEntity<?>>map(d -> {
        // formatear horas legibles HH:mm:ss
        java.time.ZoneId Z = java.time.ZoneId.systemDefault();
        java.time.format.DateTimeFormatter H = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");
        String hi = java.time.LocalDateTime.ofInstant(d.getHoraInicio().toInstant(), Z).toLocalTime().format(H);
        String hf = java.time.LocalDateTime.ofInstant(d.getHoraFin().toInstant(), Z).toLocalTime().format(H);
        return ResponseEntity.ok(java.util.Map.of(
          "idDisponibilidad", d.getIdDisponibilidad(),
          "dia", d.getDia(),
          "tipoServicio", d.getTipoServicio(),
          "horaInicio", hi,
          "horaFin", hf,
          "idVehiculo", d.getVehiculo().getIdVehiculo(),
          "idUsuarioConductor", d.getUsuarioConductor().getIdUsuarioConductor()
        ));
      })
      .orElseGet(() -> ResponseEntity.notFound().build());
  }
  

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

  // 🔹 RF6: modificar disponibilidad (transaccional en el Service)
  @PatchMapping(value = "/{idDisponibilidad}/modificar", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ResponseEntity<?> modificar(
      @PathVariable Long idDisponibilidad,
      @RequestParam String horaInicio,   // HH:mm:ss
      @RequestParam String horaFin,      // HH:mm:ss
      @RequestParam(required = false) String dia,           // opcional
      @RequestParam(required = false) String tipoServicio   // opcional
  ) {
    try {
      var d = service.modificar(idDisponibilidad, dia, horaInicio, horaFin, tipoServicio);
      java.time.ZoneId Z = java.time.ZoneId.systemDefault();
      java.time.format.DateTimeFormatter HH = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");
      String hi = java.time.LocalDateTime.ofInstant(d.getHoraInicio().toInstant(), Z).toLocalTime().format(HH);
      String hf = java.time.LocalDateTime.ofInstant(d.getHoraFin().toInstant(), Z).toLocalTime().format(HH);
      
      return ResponseEntity.ok(java.util.Map.of(
        "idDisponibilidad", d.getIdDisponibilidad(),
        "dia", d.getDia(),
        "tipoServicio", d.getTipoServicio(),
        "horaInicio", hi,
        "horaFin", hf
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
