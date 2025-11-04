// src/main/java/uniandes/edu/co/proyecto/controller/TarjetaCreditoController.java
package uniandes.edu.co.proyecto.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uniandes.edu.co.proyecto.repositorio.TarjetaCreditoRepository;
import uniandes.edu.co.proyecto.web.TarjetaCreditoRequest;

@RestController
@RequestMapping("/tarjetas")
public class TarjetaCreditoController {

  private final TarjetaCreditoRepository repo;

  public TarjetaCreditoController(TarjetaCreditoRepository repo) {
    this.repo = repo;
  }

  @PostMapping("/registrar")
  public ResponseEntity<?> registrar(@RequestBody TarjetaCreditoRequest body) {
    if (body == null) return ResponseEntity.badRequest().body("Body requerido");

    Long idTarjeta = body.idTarjeta();
    Long numero = body.numero();
    String nombre = body.nombre();
    Integer mes = body.mesVencimiento();
    Integer anio = body.anioVencimiento();
    Integer cvv = body.codigoSeguridad();
    Long idUsuarioServicio = body.idUsuarioServicio();

    if (idTarjeta == null || idTarjeta <= 0) return ResponseEntity.badRequest().body("idTarjeta requerido y positivo");
    if (idUsuarioServicio == null || idUsuarioServicio <= 0) return ResponseEntity.badRequest().body("idUsuarioServicio requerido y positivo");
    if (numero == null || numero <= 0) return ResponseEntity.badRequest().body("numero requerido y positivo");
    if (nombre == null || nombre.isBlank()) return ResponseEntity.badRequest().body("nombre requerido");
    if (mes == null || mes < 1 || mes > 12) return ResponseEntity.badRequest().body("mesVencimiento debe estar entre 1 y 12");
    if (anio == null || anio < 2024 || anio > 2040) return ResponseEntity.badRequest().body("anioVencimiento debe estar entre 2024 y 2040");
    if (cvv == null || cvv < 0 || cvv > 9999) return ResponseEntity.badRequest().body("codigoSeguridad debe estar entre 0 y 9999");

    if (repo.existsById(idTarjeta)) {
      return ResponseEntity.status(409).body("id_tarjeta ya existe");
    }
    // Usuario de servicios debe existir (FK)
    if (repo.countUsuarioServicio(idUsuarioServicio) == 0) {
      return ResponseEntity.status(404).body("Usuario de servicios no existe: " + idUsuarioServicio);
    }
    // Número único
    if (repo.countByNumero(numero) > 0) {
      return ResponseEntity.status(409).body("numero de tarjeta ya registrado");
    }

    repo.insertarTarjeta(idTarjeta, numero, nombre, mes, anio, cvv, idUsuarioServicio);

    return ResponseEntity.ok(Map.of(
        "idTarjeta", idTarjeta,
        "numero", numero,
        "nombre", nombre,
        "mesVencimiento", mes,
        "anioVencimiento", anio,
        "codigoSeguridad", cvv,
        "idUsuarioServicio", idUsuarioServicio
    ));
  }

  @PostMapping(value = "/registrar-form", consumes = "application/x-www-form-urlencoded")
  public ResponseEntity<?> registrarForm(
      @RequestParam Long idTarjeta,
      @RequestParam Long numero,
      @RequestParam String nombre,
      @RequestParam Integer mesVencimiento,
      @RequestParam Integer anioVencimiento,
      @RequestParam Integer codigoSeguridad,
      @RequestParam Long idUsuarioServicio
  ) {
    var req = new TarjetaCreditoRequest(
        idTarjeta, numero, nombre, mesVencimiento, anioVencimiento, codigoSeguridad, idUsuarioServicio
    );
    return registrar(req);
  }

  @GetMapping("/{idTarjeta}")
  public ResponseEntity<?> obtener(@PathVariable Long idTarjeta) {
    return repo.findById(idTarjeta)
        .<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
