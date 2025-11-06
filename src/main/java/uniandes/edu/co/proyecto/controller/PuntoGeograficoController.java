// src/main/java/uniandes/edu/co/proyecto/controller/PuntoGeograficoController.java
package uniandes.edu.co.proyecto.controller;

import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import uniandes.edu.co.proyecto.modelo.PuntoGeografico;
import uniandes.edu.co.proyecto.repositorio.PuntoGeograficoRepository;
import uniandes.edu.co.proyecto.service.PuntoGeograficoService;
import uniandes.edu.co.proyecto.web.PuntoGeograficoCreateRequest;
import uniandes.edu.co.proyecto.web.PuntoGeograficoRequest;

@RestController
@RequestMapping("/puntos")
public class PuntoGeograficoController {

  private final PuntoGeograficoService service;
  private final PuntoGeograficoRepository repo;

  public PuntoGeograficoController(PuntoGeograficoService service, PuntoGeograficoRepository repo) {
    this.service = service;
    this.repo = repo;
  }

  @PostMapping({ "/new/save", "/registrar" })
  public ResponseEntity<?> registrar(@RequestBody PuntoGeograficoRequest body) {
    try {
      PuntoGeografico p = service.registrar(
          body.idPunto(),
          body.nombre(),
          body.latitud(),
          body.longitud(),
          body.direccion(),
          body.idCiudad());
      return ResponseEntity.ok(Map.of(
          "idPunto", p.getId(),
          "nombre", p.getNombre(),
          "latitud", p.getLatitud(),
          "longitud", p.getLongitud(),
          "direccion", p.getDireccion(),
          "idCiudad", p.getCiudad().getIdCiudad()));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage()); // 400
    } catch (IllegalStateException e) {
      return ResponseEntity.status(409).body(e.getMessage()); // 409
    } catch (RuntimeException e) {
      return ResponseEntity.status(404).body(e.getMessage()); // 404
    }
  }
  
@PostMapping("/new/auto")
public ResponseEntity<?> registrarAuto(@Valid @RequestBody PuntoGeograficoCreateRequest body) {
  try {
    var p = service.registrarAuto(body.nombre(), body.latitud(), body.longitud(), body.direccion(), body.idCiudad());
    return ResponseEntity.ok(Map.of(
      "idPunto", p.getId(),
      "nombre", p.getNombre(),
      "latitud", p.getLatitud(),
      "longitud", p.getLongitud(),
      "direccion", p.getDireccion(),
      "idCiudad", p.getCiudad().getIdCiudad()
    ));
  } catch (IllegalArgumentException e) {
    return ResponseEntity.badRequest().body(e.getMessage());       // 400
  } catch (IllegalStateException e) {
    return ResponseEntity.status(409).body(e.getMessage());        // 409 duplicados
  } catch (DataIntegrityViolationException e) {
    return ResponseEntity.status(409).body("Duplicado (DB): " + e.getMostSpecificCause().getMessage());
  } catch (RuntimeException e) {
    return ResponseEntity.status(404).body(e.getMessage());       // 404 FK ciudad
  }
}


  @GetMapping("/{idPunto}")
  public ResponseEntity<?> obtener(@PathVariable Long idPunto) {
    return repo.findById(idPunto)
        .<ResponseEntity<?>>map(p -> ResponseEntity.ok(Map.of(
            "idPunto", p.getId(),
            "nombre", p.getNombre(),
            "latitud", p.getLatitud(),
            "longitud", p.getLongitud(),
            "direccion", p.getDireccion(),
            "idCiudad", p.getCiudad().getIdCiudad())))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/ping")
  public String ping() {
    return "puntos-ok";
  }

}
