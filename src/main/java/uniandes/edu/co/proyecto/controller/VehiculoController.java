// src/main/java/uniandes/edu/co/proyecto/controller/VehiculoController.java
package uniandes.edu.co.proyecto.controller;

import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uniandes.edu.co.proyecto.service.VehiculoService;

@RestController
@RequestMapping("/vehiculos")
public class VehiculoController {

  private final VehiculoService service;

  public VehiculoController(VehiculoService service) { this.service = service; }

  @PostMapping(value = {"/registrar", "/new/save"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ResponseEntity<?> registrar(
      @RequestParam Long idVehiculo,
      @RequestParam String tipo,
      @RequestParam String marca,
      @RequestParam String modelo,
      @RequestParam String color,
      @RequestParam String placa,
      @RequestParam Integer capacidad,
      @RequestParam Long idUsuarioConductor,
      @RequestParam Long idCiudadExpedicion
  ) {
    try {
      var v = service.registrar(idVehiculo, tipo, marca, modelo, color, placa, capacidad,
                                idUsuarioConductor, idCiudadExpedicion);
      return ResponseEntity.ok(Map.of(
          "idVehiculo", v.getId(),
          "placa", v.getPlaca(),
          "tipo", v.getTipo()
      ));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (IllegalStateException e) {
      return ResponseEntity.status(409).body(e.getMessage());
    } catch (RuntimeException e) {
      return ResponseEntity.status(404).body(e.getMessage());
    }
  }

  @GetMapping("/{idVehiculo}")
  public ResponseEntity<?> obtener(@PathVariable Long idVehiculo) {
    return service.obtener(idVehiculo)
      .<ResponseEntity<?>>map(ResponseEntity::ok)
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/ping")
  public String ping() { return "vehiculos-ok"; }
}
