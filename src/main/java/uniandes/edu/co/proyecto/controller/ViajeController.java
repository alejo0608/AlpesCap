package uniandes.edu.co.proyecto.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uniandes.edu.co.proyecto.repositorio.ViajeRepository;
import uniandes.edu.co.proyecto.service.ViajeService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/viajes")
public class ViajeController {

  private final ViajeRepository repo;
  private final ViajeService viajeService;

  private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public ViajeController(ViajeRepository repo, ViajeService viajeService) {
    this.repo = repo;
    this.viajeService = viajeService;
  }

  @PostMapping("/registrar")
  public ResponseEntity<String> registrarViaje(
      @RequestParam Long   idViaje,
      @RequestParam String fechaAsignacion,               // YYYY-MM-DD
      @RequestParam(required = false) String horaInicio,  // YYYY-MM-DD HH:MM:SS (opcional)
      @RequestParam(required = false) String horaFin,     // IGNORADO en el insert (HORA_FIN queda NULL)
      @RequestParam(required = false) Double distanciaKm,
      @RequestParam(required = false) Double costoTotal,
      @RequestParam Long   idUsuarioConductor,
      @RequestParam Long   idVehiculo,
      @RequestParam Long   idPuntoPartida,
      @RequestParam Long   idSolicitud) {

    // 1) Unicidad de id
    if (repo.existsById(idViaje)) {
      return ResponseEntity.status(409).body("id_viaje ya existe");
    }

    // 2) Validación de fecha/hora
    final String tsInicio;
    try {
      LocalDate.parse(fechaAsignacion); // YYYY-MM-DD
      if (horaInicio == null || horaInicio.isBlank()) {
        tsInicio = LocalDateTime.now().format(TS_FMT);
      } else {
        // valida formato
        LocalDateTime.parse(horaInicio, TS_FMT);
        tsInicio = horaInicio.trim();
      }
      // Validación opcional de horaFin si la mandan (no se usa en insert)
      if (horaFin != null && !horaFin.isBlank()) {
        LocalDateTime fin = LocalDateTime.parse(horaFin, TS_FMT);
        LocalDateTime ini = LocalDateTime.parse(tsInicio, TS_FMT);
        if (fin.isBefore(ini)) {
          return ResponseEntity.badRequest().body("hora_fin no puede ser menor que hora_inicio");
        }
      }
    } catch (Exception ex) {
      return ResponseEntity.badRequest().body("Formato de fecha/hora inválido. Use YYYY-MM-DD y YYYY-MM-DD HH:MM:SS");
    }

    // 3) Validaciones FK “amigables” (si tienes estos métodos en el repo)
    if (repo.countConductor(idUsuarioConductor) == 0) {
      return ResponseEntity.status(404).body("UsuarioConductor no existe: " + idUsuarioConductor);
    }
    if (repo.countVehiculo(idVehiculo) == 0) {
      return ResponseEntity.status(404).body("Vehiculo no existe: " + idVehiculo);
    }
    if (repo.countPunto(idPuntoPartida) == 0) {
      return ResponseEntity.status(404).body("PuntoGeografico no existe: " + idPuntoPartida);
    }
    if (repo.countSolicitud(idSolicitud) == 0) {
      return ResponseEntity.status(404).body("SolicitudServicio no existe: " + idSolicitud);
    }

    // 4) Una solicitud -> un viaje
    if (repo.countViajePorSolicitud(idSolicitud) > 0) {
      return ResponseEntity.status(409).body("La solicitud ya tiene un viaje asignado");
    }

    // 5) Defaults de negocio
    Double dist = (distanciaKm == null) ? 0.0 : distanciaKm;
    Double costo = (costoTotal == null) ? 0.0 : costoTotal;

    // 6) INSERT: sin horaFin (queda NULL). Firma de repo:
    // insertarViajeInicio(Long idViaje, String fechaAsignacion, String horaInicio,
    //                     Double distanciaKm, Double costoTotal,
    //                     Long idConductor, Long idVehiculo, Long idPuntoPartida, Long idSolicitud)
    repo.insertarViajeInicio(
        idViaje,
        fechaAsignacion,
        tsInicio,
        dist,
        costo,
        idUsuarioConductor,
        idVehiculo,
        idPuntoPartida,
        idSolicitud
    );

    return ResponseEntity.ok("Viaje registrado: " + idViaje);
  }

  // RF9: Finalizar viaje “nuevo” (servicio)
  @PostMapping("/{idViaje}/finalizar")
  public ResponseEntity<?> finalizarRF9(@PathVariable Long idViaje,
                                        @RequestParam Double distanciaKm) {
    try {
      viajeService.finalizarViaje(idViaje, distanciaKm);
      return ResponseEntity.ok("Viaje finalizado: " + idViaje);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error interno al finalizar el viaje: " + e.getMessage());
    }
  }

  // Legacy (si aún lo usa tu compa)
  @Deprecated
  @PostMapping("/finalizar")
  public ResponseEntity<String> finalizarViajeLegacy(
      @RequestParam Long   idViaje,
      @RequestParam(required = false) String horaInicio,
      @RequestParam String horaFin,
      @RequestParam Double distanciaKm,
      @RequestParam Double costoTotal) {

    if (!repo.existsById(idViaje)) {
      return ResponseEntity.status(404).body("Viaje no existe: " + idViaje);
    }

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
    return ResponseEntity.ok("Viaje finalizado (legacy): " + idViaje);
  }

  @GetMapping("/{idViaje}")
  public ResponseEntity<?> obtenerViaje(@PathVariable Long idViaje) {
    return repo.findById(idViaje)
        .<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  private static String emptyToNull(String s) {
    return (s == null || s.isBlank()) ? null : s;
  }
}
