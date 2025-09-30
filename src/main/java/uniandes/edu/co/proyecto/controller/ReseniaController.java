package uniandes.edu.co.proyecto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uniandes.edu.co.proyecto.repositorio.ReseniaRepository;

@RestController
@RequestMapping("/resenias")
public class ReseniaController {

  private final ReseniaRepository repo;

  public ReseniaController(ReseniaRepository repo) { this.repo = repo; }

  @PostMapping("/registrar")
  public ResponseEntity<String> registrarResenia(
      @RequestParam Long idResenia,
      @RequestParam Long idViaje,
      @RequestParam Integer calificacion,     // 0..5
      @RequestParam(required = false) String comentario,
      @RequestParam String fecha              // "YYYY-MM-DD"
  ) {
    // PK duplicada
    if (repo.existsById(idResenia)) {
      return ResponseEntity.status(409).body("id_resenia ya existe");
    }
    // FK viaje
    if (repo.countViaje(idViaje) == 0) {
      return ResponseEntity.status(404).body("Viaje no existe: " + idViaje);
    }
    // Check de negocio/BD: calificación en rango
    if (calificacion == null || calificacion < 0 || calificacion > 5) {
      return ResponseEntity.badRequest().body("calificacion inválida (0..5)");
    }

    repo.insertarResenia(idResenia, calificacion, comentario, fecha, idViaje);
    return ResponseEntity.ok("Reseña registrada: " + idResenia);
  }

  @GetMapping("/{idResenia}")
  public ResponseEntity<?> obtener(@PathVariable Long idResenia) {
    return repo.findById(idResenia)
        .<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
