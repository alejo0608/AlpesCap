// src/main/java/uniandes/edu/co/proyecto/controller/VehiculoController.java
package uniandes.edu.co.proyecto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uniandes.edu.co.proyecto.repositorio.VehiculoRepository;

@RestController
@RequestMapping("/vehiculos")
public class VehiculoController {

  private final VehiculoRepository repo;

  public VehiculoController(VehiculoRepository repo) {
    this.repo = repo;
  }

  @PostMapping("/registrar")
  public ResponseEntity<String> registrarVehiculo(
      @RequestParam Long idVehiculo,
      @RequestParam String tipo,
      @RequestParam String marca,
      @RequestParam String modelo,
      @RequestParam String color,
      @RequestParam String placa,
      @RequestParam Integer capacidad,
      @RequestParam Long idUsuarioConductor,
      @RequestParam Long idCiudadExpedicion) {

    if (repo.existsById(idVehiculo)) {
      return ResponseEntity.status(409).body("id_vehiculo ya existe");
    }
    if (repo.countByPlaca(placa) > 0) {
      return ResponseEntity.status(409).body("placa ya registrada");
    }

    repo.insertarVehiculo(
        idVehiculo, tipo, marca, modelo, color, placa, capacidad,
        idUsuarioConductor, idCiudadExpedicion
    );

    return ResponseEntity.ok("Vehiculo registrado: " + idVehiculo);
  }

  @GetMapping("/{idVehiculo}")
  public ResponseEntity<?> obtenerVehiculo(@PathVariable Long idVehiculo) {
    return repo.findById(idVehiculo)
        .<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
