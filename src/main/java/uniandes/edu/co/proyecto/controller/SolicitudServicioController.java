package uniandes.edu.co.proyecto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uniandes.edu.co.proyecto.repositorio.SolicitudServicioRepository;

@RestController
@RequestMapping("/solicitudes") 
public class SolicitudServicioController {

  private final SolicitudServicioRepository repo;

  public SolicitudServicioController(SolicitudServicioRepository repo) {
    this.repo = repo;
  }

  @PostMapping("/registrar")
  public ResponseEntity<String> registrarSolicitud(
      @RequestParam Long idSolicitud,
      @RequestParam String tipo,                  // PASAJEROS | COMIDA | MERCANCIAS
      @RequestParam(required = false) String nivel, // ESTANDAR | CONFORT | LARGE (solo si tipo=PASAJEROS)
      @RequestParam String fecha,                 // "YYYY-MM-DD"
      @RequestParam String estado,                // CREADA | ASIGNADA | EN_PROGRESO | FINALIZADA | CANCELADA
      @RequestParam Long idUsuarioServicio,
      @RequestParam Long idPuntoPartida) {

    // PK duplicada
    if (repo.existsById(idSolicitud)) {
      return ResponseEntity.status(409).body("id_solicitud ya existe");
    }

    // Normalizar a mayúsculas para evitar fallos de CHECK
    String tipoUp = tipo == null ? null : tipo.trim().toUpperCase();
    String estadoUp = estado == null ? null : estado.trim().toUpperCase();
    String nivelUp = (nivel == null || nivel.isBlank()) ? null : nivel.trim().toUpperCase();

    // Dominios (coherentes con tu DDL)
    if (!( "PASAJEROS".equals(tipoUp) || "COMIDA".equals(tipoUp) || "MERCANCIAS".equals(tipoUp) )) {
      return ResponseEntity.badRequest().body("tipo inválido (use: PASAJEROS | COMIDA | MERCANCIAS)");
    }
    if (!( "CREADA".equals(estadoUp) || "ASIGNADA".equals(estadoUp) || "EN_PROGRESO".equals(estadoUp)
         || "FINALIZADA".equals(estadoUp) || "CANCELADA".equals(estadoUp) )) {
      return ResponseEntity.badRequest().body("estado inválido");
    }
    if ("PASAJEROS".equals(tipoUp)) {
      if (nivelUp == null || !( "ESTANDAR".equals(nivelUp) || "CONFORT".equals(nivelUp) || "LARGE".equals(nivelUp) )) {
        return ResponseEntity.badRequest().body("nivel requerido y válido para tipo PASAJEROS (ESTANDAR | CONFORT | LARGE)");
      }
    } else {
      // Para COMIDA/MERCANCIAS, permitimos null o ignoramos nivel
      nivelUp = null;
    }

    // Validar FKs antes de insertar (evita error 500 por FK)
    if (repo.countUsuarioServicio(idUsuarioServicio) == 0) {
      return ResponseEntity.status(404).body("UsuarioServicio no existe: " + idUsuarioServicio);
    }
    if (repo.countPuntoGeografico(idPuntoPartida) == 0) {
      return ResponseEntity.status(404).body("PuntoGeografico no existe: " + idPuntoPartida);
    }

    repo.insertarSolicitud(idSolicitud, tipoUp, nivelUp, fecha, estadoUp, idUsuarioServicio, idPuntoPartida);
    return ResponseEntity.ok("Solicitud registrada: " + idSolicitud);
  }

  // GET para verificación rápida
  @GetMapping("/{idSolicitud}")
  public ResponseEntity<?> obtener(@PathVariable Long idSolicitud) {
    return repo.findById(idSolicitud)
        .<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}

