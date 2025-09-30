package uniandes.edu.co.proyecto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uniandes.edu.co.proyecto.repositorio.TarjetaCreditoRepository;

import java.time.Year;

@RestController
@RequestMapping("/tarjetas")
public class TarjetaCreditoController {

  private final TarjetaCreditoRepository repo;

  public TarjetaCreditoController(TarjetaCreditoRepository repo) { this.repo = repo; }

  @PostMapping("/registrar")
  public ResponseEntity<String> registrarTarjeta(
      @RequestParam Long idTarjeta,
      @RequestParam Long idUsuarioServicio,
      @RequestParam String numero,
      @RequestParam String nombre,
      @RequestParam Integer mesVencimiento,
      @RequestParam Integer anioVencimiento,
      @RequestParam Integer codigoSeguridad
  ) {
    if (repo.existsById(idTarjeta)) {
      return ResponseEntity.status(409).body("id_tarjeta ya existe");
    }
    if (repo.countUsuarioServicio(idUsuarioServicio) == 0) {
      return ResponseEntity.status(404).body("UsuarioServicio no existe: " + idUsuarioServicio);
    }
    if (repo.countByNumero(numero) > 0) {
      return ResponseEntity.status(409).body("numero de tarjeta ya registrado");
    }
    if (mesVencimiento == null || mesVencimiento < 1 || mesVencimiento > 12) {
      return ResponseEntity.badRequest().body("mes_vencimiento inválido (1..12)");
    }
    int anioActual = Year.now().getValue();
    if (anioVencimiento == null || anioVencimiento < anioActual) {
      return ResponseEntity.badRequest().body("anio_vencimiento inválido (>= " + anioActual + ")");
    }
    if (codigoSeguridad == null || codigoSeguridad < 0) {
      return ResponseEntity.badRequest().body("codigo_seguridad inválido");
    }

    repo.insertarTarjeta(idTarjeta, numero, nombre, mesVencimiento, anioVencimiento, codigoSeguridad, idUsuarioServicio);
    return ResponseEntity.ok("Tarjeta registrada: " + idTarjeta);
  }

  @GetMapping("/{idTarjeta}")
  public ResponseEntity<?> obtener(@PathVariable Long idTarjeta) {
    var t = repo.findById(idTarjeta);
    return t.<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }
}

