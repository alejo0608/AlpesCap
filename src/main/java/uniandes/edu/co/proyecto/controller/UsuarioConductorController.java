// src/main/java/uniandes/edu/co/proyecto/controller/UsuarioConductorController.java
package uniandes.edu.co.proyecto.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uniandes.edu.co.proyecto.service.UsuarioConductorService;
import uniandes.edu.co.proyecto.web.UsuarioConductorRequest;

import java.util.Map;

@RestController
@RequestMapping("/conductores")
public class UsuarioConductorController {

  private final UsuarioConductorService service;

  public UsuarioConductorController(UsuarioConductorService service) {
    this.service = service;
  }

  @PostMapping("/registrar")
  public ResponseEntity<?> registrarConductor(@Valid @RequestBody UsuarioConductorRequest request) {
    try {
      var c = service.registrarConductor(request);
      return ResponseEntity.ok(Map.of(
          "idConductor", c.getIdUsuarioConductor(),
          "nombre", c.getNombre(),
          "correo", c.getCorreo()
      ));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (IllegalStateException e) {
      return ResponseEntity.status(409).body(e.getMessage());
    }
  }

  @GetMapping("/{idConductor}")
  public ResponseEntity<?> obtenerConductor(@PathVariable Long idConductor) {
    return service.obtenerConductor(idConductor)
        .<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/{idConductor}")
  public ResponseEntity<?> actualizarConductor(@PathVariable Long idConductor,
                                               @Valid @RequestBody UsuarioConductorRequest request) {
    try {
      var c = service.actualizarConductor(idConductor, request);
      return ResponseEntity.ok(Map.of(
          "idConductor", c.getIdUsuarioConductor(),
          "nombre", c.getNombre(),
          "correo", c.getCorreo(),
          "comision", c.getComision()
      ));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (IllegalStateException e) {
      return ResponseEntity.status(409).body(e.getMessage());
    } catch (RuntimeException e) {
      return ResponseEntity.status(404).body(e.getMessage());
    }
  }

  @DeleteMapping("/{idConductor}")
  public ResponseEntity<?> eliminarConductor(@PathVariable Long idConductor) {
    try {
      service.eliminarConductor(idConductor);
      return ResponseEntity.ok("Conductor eliminado: " + idConductor);
    } catch (IllegalStateException e) {
      return ResponseEntity.status(409).body(e.getMessage());
    } catch (RuntimeException e) {
      return ResponseEntity.status(404).body(e.getMessage());
    }
  }
}
