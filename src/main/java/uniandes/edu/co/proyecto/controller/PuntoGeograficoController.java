// src/main/java/uniandes/edu/co/proyecto/controller/PuntoGeograficoController.java
package uniandes.edu.co.proyecto.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uniandes.edu.co.proyecto.modelo.PuntoGeografico;
import uniandes.edu.co.proyecto.service.PuntoGeograficoService;
import uniandes.edu.co.proyecto.web.PuntoGeograficoRequest;

@RestController
@RequestMapping("/puntos")
public class PuntoGeograficoController {

  private final PuntoGeograficoService service;

  public PuntoGeograficoController(PuntoGeograficoService service) {
    this.service = service;
  }

  @PostMapping({"/new/save", "/registrar"})
  public ResponseEntity<?> registrar(@RequestBody PuntoGeograficoRequest body) {
    try {
      PuntoGeografico p = service.registrar(
          body.idPunto(),
          body.nombre(),
          body.latitud(),
          body.longitud(),
          body.direccion(),
          body.idCiudad()
      );
      return ResponseEntity.ok(Map.of(
          "idPunto", p.getId(),
          "nombre", p.getNombre(),
          "latitud", p.getLatitud(),
          "longitud", p.getLongitud(),
          "direccion", p.getDireccion(),
          "idCiudad", p.getCiudad().getIdCiudad()
      ));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());     // 400
    } catch (IllegalStateException e) {
      return ResponseEntity.status(409).body(e.getMessage());      // 409
    } catch (RuntimeException e) {
      return ResponseEntity.status(404).body(e.getMessage());      // 404
    }
  }
}
