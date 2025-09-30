package uniandes.edu.co.proyecto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uniandes.edu.co.proyecto.modelo.Ciudad;
import uniandes.edu.co.proyecto.repositorio.CiudadRepository;

import java.util.Map;

@RestController
@RequestMapping("/ciudades")
public class CiudadController {

  private final CiudadRepository repo;

  public CiudadController(CiudadRepository repo) { this.repo = repo; }

  @PostMapping("/new/save")
  public ResponseEntity<?> crear(@RequestBody Ciudad body) {
    if (body.getNombre() == null || body.getNombre().isBlank()) {
      return ResponseEntity.badRequest().body("nombre requerido");
    }
    var nombre = body.getNombre().trim();
    if (repo.existsByNombreIgnoreCase(nombre)) {
      return ResponseEntity.status(409).body("nombre ya existe");
    }
    var c = new Ciudad();
    c.setNombre(nombre);
    var saved = repo.save(c); // usa la SEQUENCE -> ID autogenerado
    return ResponseEntity.ok(Map.of("idCiudad", saved.getIdCiudad(), "nombre", saved.getNombre()));
  }

  @GetMapping("/por-nombre")
public org.springframework.http.ResponseEntity<?> porNombre(@RequestParam String nombre) {
  return repo.findByNombreIgnoreCase(nombre.trim())
      .<org.springframework.http.ResponseEntity<?>>map(org.springframework.http.ResponseEntity::ok)
      .orElseGet(() -> org.springframework.http.ResponseEntity.notFound().build());
}

}
