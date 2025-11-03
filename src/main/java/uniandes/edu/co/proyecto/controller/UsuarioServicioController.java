// src/main/java/uniandes/edu/co/proyecto/controller/UsuarioServicioController.java
package uniandes.edu.co.proyecto.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uniandes.edu.co.proyecto.modelo.UsuarioServicio;
import uniandes.edu.co.proyecto.service.UsuarioServicioService;
import uniandes.edu.co.proyecto.web.UsuarioServicioRequest;

@RestController
@RequestMapping("/usuarios-servicio")
public class UsuarioServicioController {

  private final UsuarioServicioService service;

  public UsuarioServicioController(UsuarioServicioService service) {
    this.service = service;
  }

  @PostMapping({"/new/save", "/registrar"})
  public ResponseEntity<?> registrar(@RequestBody UsuarioServicioRequest body) {
    try {
      UsuarioServicio u = service.registrar(
          body.id(),
          body.nombre(),
          body.correo(),
          body.telefono(),
          body.cedula()
      );
      return ResponseEntity.ok(Map.of(
          "id", u.getIdUsuarioServicio(),
          "nombre", u.getNombre(),
          "correo", u.getCorreo(),
          "telefono", u.getTelefono(),
          "cedula", u.getCedula()
      ));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());   // 400
    } catch (IllegalStateException e) {
      return ResponseEntity.status(409).body(e.getMessage());    // 409
    }
  }
}
