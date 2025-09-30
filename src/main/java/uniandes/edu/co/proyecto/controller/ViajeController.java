package uniandes.edu.co.proyecto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uniandes.edu.co.proyecto.repositorio.ViajeRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/viajes")
public class ViajeController {

  private final ViajeRepository repo;
  private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public ViajeController(ViajeRepository repo) {
    this.repo = repo;
  }

  /* ================== Crear viaje (asignación) ================== */
  @PostMapping("/registrar")
  public ResponseEntity<String> registrarViaje(
      @RequestParam Long   idViaje,
      @RequestParam String fechaAsignacion,     // "YYYY-MM-DD"
      @RequestParam(required = false) String horaInicio,   // "YYYY-MM-DD HH:MM:SS" o null
      @RequestParam(required = false) String horaFin,      // idem
      @RequestParam(required = false) Double distanciaKm,  // puede ser null
      @RequestParam(required = false) Double costoTotal,   // puede ser null
      @RequestParam Long   idUsuarioConductor,
      @RequestParam Long   idVehiculo,
      @RequestParam Long   idPuntoPartida,
      @RequestParam(required = false) Long idSolicitud) {

    // 1) PK
    if (repo.existsById(idViaje)) {
      return ResponseEntity.status(409).body("id_viaje ya existe");
    }

    // 2) Validar formato de fecha/horas (si vienen)
    try {
      LocalDate.parse(fechaAsignacion); // ISO "yyyy-MM-dd"
      if (horaInicio != null && !horaInicio.isBlank())  LocalDateTime.parse(horaInicio, TS_FMT);
      if (horaFin    != null && !horaFin.isBlank())     LocalDateTime.parse(horaFin, TS_FMT);
      if (horaInicio != null && !horaInicio.isBlank()
       && horaFin    != null && !horaFin.isBlank()) {
        if (LocalDateTime.parse(horaFin, TS_FMT).isBefore(LocalDateTime.parse(horaInicio, TS_FMT))) {
          return ResponseEntity.badRequest().body("hora_fin no puede ser menor que hora_inicio");
        }
      }
    } catch (Exception ex) {
      return ResponseEntity.badRequest().body("Formato de fecha/hora inválido. Use YYYY-MM-DD y YYYY-MM-DD HH:MM:SS");
    }

    // 3) Validar FKs
    if (repo.countConductor(idUsuarioConductor) == 0) {
      return ResponseEntity.status(404).body("UsuarioConductor no existe: " + idUsuarioConductor);
    }
    if (repo.countVehiculo(idVehiculo) == 0) {
      return ResponseEntity.status(404).body("Vehiculo no existe: " + idVehiculo);
    }
    if (repo.countPunto(idPuntoPartida) == 0) {
      return ResponseEntity.status(404).body("PuntoGeografico no existe: " + idPuntoPartida);
    }
    if (idSolicitud != null && repo.countSolicitud(idSolicitud) == 0) {
      return ResponseEntity.status(404).body("SolicitudServicio no existe: " + idSolicitud);
    }

    // 4) Insertar
    repo.insertarViaje(idViaje, fechaAsignacion,
                       emptyToNull(horaInicio), emptyToNull(horaFin),
                       distanciaKm, costoTotal,
                       idUsuarioConductor, idVehiculo, idPuntoPartida, idSolicitud);

    return ResponseEntity.ok("Viaje registrado: " + idViaje);
  }

  /* ================== Finalizar/actualizar viaje ================== */
  @PostMapping("/finalizar")
  public ResponseEntity<String> finalizarViaje(
      @RequestParam Long   idViaje,
      @RequestParam(required = false) String horaInicio,    // si se quiere setear
      @RequestParam String horaFin,                          // recomendado enviar
      @RequestParam Double distanciaKm,
      @RequestParam Double costoTotal) {

    // Validar existencia
    if (!repo.existsById(idViaje)) {
      return ResponseEntity.status(404).body("Viaje no existe: " + idViaje);
    }

    // Validar formato/orden temporal
    try {
      if (horaInicio != null && !horaInicio.isBlank())  LocalDateTime.parse(horaInicio, TS_FMT);
      LocalDateTime fin = LocalDateTime.parse(horaFin, TS_FMT);
      if (horaInicio != null && !horaInicio.isBlank()) {
        LocalDateTime ini = LocalDateTime.parse(horaInicio, TS_FMT);
        if (fin.isBefore(ini)) {
          return ResponseEntity.badRequest().body("hora_fin no puede ser menor que hora_inicio");
        }
      }
    } catch (Exception ex) {
      return ResponseEntity.badRequest().body("Formato de hora inválido. Use YYYY-MM-DD HH:MM:SS");
    }

    int rows = repo.actualizarFinal(idViaje, emptyToNull(horaInicio), horaFin, distanciaKm, costoTotal);
    if (rows == 0) {
      return ResponseEntity.status(500).body("No se pudo actualizar el viaje");
    }
    return ResponseEntity.ok("Viaje finalizado: " + idViaje);
  }

  /* ================== GET por id ================== */
  @GetMapping("/{idViaje}")
  public ResponseEntity<?> obtenerViaje(@PathVariable Long idViaje) {
    return repo.findById(idViaje)
        .<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /* Util */
  private static String emptyToNull(String s) {
    return (s == null || s.isBlank()) ? null : s;
  }
}
