// src/main/java/uniandes/edu/co/proyecto/controller/DisponibilidadController.java
package uniandes.edu.co.proyecto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uniandes.edu.co.proyecto.repositorio.DisponibilidadRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/disponibilidades")
public class DisponibilidadController {

  private final DisponibilidadRepository repo;
  private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public DisponibilidadController(DisponibilidadRepository repo) {
    this.repo = repo;
  }

  @PostMapping("/registrar")
  public ResponseEntity<String> registrar(
      @RequestParam Long idDisponibilidad,
      @RequestParam String dia,
      @RequestParam String horaInicio,
      @RequestParam String horaFin,
      @RequestParam String tipoServicio,          // PASAJEROS | COMIDA | MERCANCIAS/MERCANÍAS
      @RequestParam Long idVehiculo,
      @RequestParam Long idUsuarioConductor) {

    if (repo.existsById(idDisponibilidad)) {
      return ResponseEntity.status(409).body("id_disponibilidad ya existe");
    }
    if (repo.countVehiculo(idVehiculo) == 0) {
      return ResponseEntity.status(404).body("Vehiculo no existe: " + idVehiculo);
    }
    if (repo.countConductor(idUsuarioConductor) == 0) {
      return ResponseEntity.status(404).body("UsuarioConductor no existe: " + idUsuarioConductor);
    }

    try {
      LocalDateTime ini = LocalDateTime.parse(horaInicio, TS);
      LocalDateTime fin = LocalDateTime.parse(horaFin, TS);
      if (!fin.isAfter(ini)) {
        return ResponseEntity.badRequest().body("hora_fin debe ser > hora_inicio");
      }
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Formato de hora inválido (use YYYY-MM-DD HH:MM:SS)");
    }

    String diaUp = dia.trim().toUpperCase();
    String tipoUp = tipoServicio.trim().toUpperCase();

    if (!(diaUp.equals("LUNES") || diaUp.equals("MARTES") || diaUp.equals("MIERCOLES") || diaUp.equals("MIÉRCOLES")
        || diaUp.equals("JUEVES") || diaUp.equals("VIERNES") || diaUp.equals("SABADO") || diaUp.equals("SÁBADO")
        || diaUp.equals("DOMINGO"))) {
      return ResponseEntity.badRequest().body("dia inválido");
    }

    if (!(tipoUp.equals("PASAJEROS") || tipoUp.equals("COMIDA") || tipoUp.equals("MERCANCIAS") || tipoUp.equals("MERCANÍAS"))) {
      return ResponseEntity.badRequest().body("tipo_servicio inválido");
    }

    int solapes = repo.contarSolape(diaUp, idVehiculo, idUsuarioConductor, horaInicio, horaFin);
    if (solapes > 0) {
      return ResponseEntity.status(409).body("Traslape para el mismo vehiculo o conductor en ese día");
    }

    repo.insertarDisponibilidad(idDisponibilidad, diaUp, horaInicio, horaFin, tipoUp, idVehiculo, idUsuarioConductor);
    return ResponseEntity.ok("Disponibilidad registrada: " + idDisponibilidad);
  }
}
