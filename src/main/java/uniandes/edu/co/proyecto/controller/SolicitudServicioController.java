// src/main/java/uniandes/edu/co/proyecto/controller/SolicitudServicioController.java
package uniandes.edu.co.proyecto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uniandes.edu.co.proyecto.service.SolicitudServicioService;
import uniandes.edu.co.proyecto.web.SolicitarServicioRequest;
import uniandes.edu.co.proyecto.web.SolicitarServicioResponse;

@RestController
@RequestMapping("/solicitudes")
public class SolicitudServicioController {

  private final SolicitudServicioService service;

  public SolicitudServicioController(SolicitudServicioService service) {
    this.service = service;
  }

  @PostMapping("/solicitar")
  public ResponseEntity<?> solicitar(@RequestBody SolicitarServicioRequest req) {
    try {
      SolicitarServicioResponse r = service.solicitar(req);
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
