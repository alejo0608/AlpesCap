package uniandes.edu.co.proyecto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uniandes.edu.co.proyecto.repositorio.DisponibilidadRepository;

@RestController
@RequestMapping("/disponibilidades")
public class DisponibilidadController {

  private final DisponibilidadRepository repo;

  public DisponibilidadController(DisponibilidadRepository repo) {
    this.repo = repo;
  }

  @PostMapping("/registrar")
  public ResponseEntity<String> registrarDisponibilidad(
      @RequestParam Long idDisponibilidad,
      @RequestParam String dia,                    // p.ej. "LUNES" (según tu dominio)
      @RequestParam String horaInicio,             // "2025-09-01 08:00:00"
      @RequestParam String horaFin,                // "2025-09-01 12:00:00"
      @RequestParam Long idVehiculo,
      @RequestParam Long idUsuarioConductor,
      @RequestParam String tipoServicio            // "PASAJEROS", "COMIDA", "MERCANCIAS"
  ) {

    // Validación de solape (mismo día, vehículo o conductor)
    int solapes = repo.contarSolape(dia, idVehiculo, idUsuarioConductor, horaInicio, horaFin);
    if (solapes > 0) {
      return ResponseEntity.status(409).body("Traslape de disponibilidad para el mismo recurso/día");
    }

    repo.insertarDisponibilidad(idDisponibilidad, dia, horaInicio, horaFin,
                                idVehiculo, idUsuarioConductor, tipoServicio);

    return ResponseEntity.ok("Disponibilidad registrada: " + idDisponibilidad);
  }
}
