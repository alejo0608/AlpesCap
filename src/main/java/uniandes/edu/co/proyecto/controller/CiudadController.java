// src/main/java/uniandes/edu/co/proyecto/controller/CiudadController.java
package uniandes.edu.co.proyecto.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uniandes.edu.co.proyecto.modelo.Ciudad;
import uniandes.edu.co.proyecto.service.CiudadService;

@RestController
@RequestMapping("/ciudades")
public class CiudadController {

  private final CiudadService service;

  public CiudadController(CiudadService service) {
    this.service = service;
  }

  @PostMapping({"/new/save", "/registrar"})
  public ResponseEntity<?> registrar(@RequestBody Map<String, Object> body) {
    try {
      String nombre = body == null ? null : (String) body.get("nombre");
      Ciudad c = service.registrar(nombre);
      return ResponseEntity.ok(Map.of("idCiudad", c.getIdCiudad(), "nombre", c.getNombre()));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());   // 400
    } catch (IllegalStateException e) {
      return ResponseEntity.status(409).body(e.getMessage());    // 409
    }
  }

  @GetMapping("/por-nombre")
  public ResponseEntity<?> porNombre(@RequestParam String nombre) {
    return service.buscarPorNombre(nombre)
        .<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
