// src/main/java/uniandes/edu/co/proyecto/controller/ReseniaController.java
package uniandes.edu.co.proyecto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import uniandes.edu.co.proyecto.service.ReseniaService;

@RestController
@RequestMapping("/resenias")
public class ReseniaController {

  private final ReseniaService service;

  public ReseniaController(ReseniaService service) {
    this.service = service;
  }

  // RF10: pasajero califica a conductor
  @PostMapping(
      value = "/pasajero",
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> pasajeroCalifica(
      @RequestParam Long idResenia,
      @RequestParam Long idViaje,
      @RequestParam Long idUsuarioServicio,     // autor pasajero
      @RequestParam Integer calificacion,       // 1..5
      @RequestParam(required = false) String comentario
  ) {
    service.pasajeroCalificaConductor(idResenia, idViaje, idUsuarioServicio, calificacion, comentario);
    return ResponseEntity.ok(Map.of(
        "idResenia", idResenia,
        "idViaje", idViaje,
        "rol", "PASAJERO",
        "calificacion", calificacion,
        "comentario", comentario == null ? "" : comentario
    ));
  }

  // RF11: conductor califica a pasajero
  @PostMapping(
      value = "/conductor",
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> conductorCalifica(
      @RequestParam Long idResenia,
      @RequestParam Long idViaje,
      @RequestParam Long idUsuarioConductor,    // autor conductor
      @RequestParam Integer calificacion,       // 1..5
      @RequestParam(required = false) String comentario
  ) {
    service.conductorCalificaPasajero(idResenia, idViaje, idUsuarioConductor, calificacion, comentario);
    return ResponseEntity.ok(Map.of(
        "idResenia", idResenia,
        "idViaje", idViaje,
        "rol", "CONDUCTOR",
        "calificacion", calificacion,
        "comentario", comentario == null ? "" : comentario
    ));
  }
}
