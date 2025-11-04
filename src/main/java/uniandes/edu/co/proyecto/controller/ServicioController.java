// src/main/java/uniandes/edu/co/proyecto/controller/ServicioController.java
package uniandes.edu.co.proyecto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uniandes.edu.co.proyecto.service.ServicioSolicitudService;
import uniandes.edu.co.proyecto.web.SolicitarServicioRequest;

@RestController
@RequestMapping("/servicios")
public class ServicioController {

  private final ServicioSolicitudService service;

  public ServicioController(ServicioSolicitudService service) {
    this.service = service;
  }

  // RF8: Solicitar un servicio (transacción completa)
  @PostMapping("/solicitar")
  public ResponseEntity<?> solicitar(@RequestBody SolicitarServicioRequest body) {
    try {
      return ResponseEntity.ok(service.solicitarServicio(body)); // 200
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());   // 400 (validaciones)
    } catch (IllegalStateException e) {
      return ResponseEntity.status(409).body(e.getMessage());    // 409 (sin medio de pago, sin disponibilidad, ids duplicados)
    } catch (RuntimeException e) {
      return ResponseEntity.status(404).body(e.getMessage());    // 404 (FKs inexistentes)
    }
  }
}
