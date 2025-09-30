// src/main/java/uniandes/edu/co/proyecto/controller/ReseniaController.java
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
  public ResponseEntity<String> registrar(
      @RequestParam Long idResenia,
      @RequestParam Long idViaje,
      @RequestParam Integer calificacion,
      @RequestParam(required = false) String comentario,
      @RequestParam String fecha,      // YYYY-MM-DD
      @RequestParam String autorRol    // USR|COND (admitimos CLIENTE|CONDUCTOR y los mapeamos)
  ) {
    if (repo.existsById(idResenia)) {
      return ResponseEntity.status(409).body("id_resenia ya existe");
    }
    if (repo.countViaje(idViaje) == 0) {
      return ResponseEntity.status(404).body("Viaje no existe: " + idViaje);
    }
    if (calificacion == null || calificacion < 0 || calificacion > 5) {
      return ResponseEntity.badRequest().body("calificacion debe estar en [0..5]");
    }

    String rol = (autorRol == null ? "" : autorRol.trim().toUpperCase());
    // Mapear sinónimos amigables
    if (rol.equals("CLIENTE")) rol = "USR";
    if (rol.equals("CONDUCTOR")) rol = "COND";
    if (!(rol.equals("USR") || rol.equals("COND"))) {
      return ResponseEntity.badRequest().body("autorRol debe ser USR o COND");
    }

    if (repo.countPorViajeYRol(idViaje, rol) > 0) {
      return ResponseEntity.status(409).body("Ya existe reseña para ese viaje con el mismo autor_rol");
    }

    repo.insertarResenia(
        idResenia,
        calificacion,
        comentario == null ? "" : comentario,
        fecha,
        idViaje,
        rol
    );
    return ResponseEntity.ok("Resenia registrada: " + idResenia + " (rol=" + rol + ")");
  }
}
