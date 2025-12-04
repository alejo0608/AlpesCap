// src/main/java/uniandes/edu/co/proyecto/controller/SolicitudServicioController.java
package uniandes.edu.co.proyecto.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import uniandes.edu.co.proyecto.service.SolicitudServicioService;
import uniandes.edu.co.proyecto.web.SolicitudServicioRequest;

@RestController
@RequestMapping("/solicitudes")
public class SolicitudServicioController {

  private final SolicitudServicioService service;

  public SolicitudServicioController(SolicitudServicioService service) {
    this.service = service;
  }

  @PostMapping("/solicitar")
  public ResponseEntity<?> solicitar(@Valid @RequestBody SolicitudServicioRequest body) {
    try {
      Map<String,Object> r = service.solicitar(body);
      return ResponseEntity.ok(r);                                 // 200
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());     // 400 (validación)
    } catch (IllegalStateException e) {
      return ResponseEntity.status(409).body(e.getMessage());      // 409 (sin disponibilidad, duplicados)
    } catch (RuntimeException e) {
      return ResponseEntity.status(404).body(e.getMessage());      // 404 (FK inexistentes)
    }
  }
}
