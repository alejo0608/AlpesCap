package uniandes.edu.co.proyecto.controller;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uniandes.edu.co.proyecto.repositorio.ViajeRepository;
import uniandes.edu.co.proyecto.service.ViajeService;
import uniandes.edu.co.proyecto.web.FinalizarViajeRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

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
  @PostMapping(
      value = "/{idViaje}/finalizar",
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> finalizar(
      @PathVariable Long idViaje,
      FinalizarViajeRequest body // @ModelAttribute implícito para x-www-form-urlencoded
  ) {
    try {
      Map<String,Object> out = viajeService.finalizar(
          idViaje,
          body.distanciaKm(),   // o body.getDistanciaKm() si no es record
          body.costoTotal()     // o body.getCostoTotal()
      );
      return ResponseEntity.ok(out);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (IllegalStateException e) {
      return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(Map.of("error", "Error interno: " + e.getMessage()));
    }
  }

  @GetMapping("/{idViaje}")
  public ResponseEntity<?> obtenerViaje(@PathVariable Long idViaje) {
    return repo.findById(idViaje)
        .<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
