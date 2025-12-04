package uniandes.edu.co.proyecto.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import uniandes.edu.co.proyecto.modelo.Pago;
import uniandes.edu.co.proyecto.service.PagoService;
import uniandes.edu.co.proyecto.web.PagoRequest;

@RestController
@RequestMapping("/pagos")
public class PagoController {

  private final PagoService service;

  public PagoController(PagoService service) { this.service = service; }

  // x-www-form-urlencoded
  @PostMapping("/registrar")
  @Transactional
  public ResponseEntity<?> registrarForm(
      @RequestParam Long idPago,
      @RequestParam Long idUsuarioServicio,
      @RequestParam Long idViaje,
      @RequestParam Double monto,
      @RequestParam String metodoPago,
      @RequestParam(required = false) Long idTarjeta,
      @RequestParam String estado
  ) {
    try {
      Pago p = service.registrar(idPago, idUsuarioServicio, idViaje, monto, metodoPago, idTarjeta, estado);
      return ResponseEntity.ok(Map.of(
        "idPago", p.getIdPago(),
        "idUsuarioServicio", p.getUsuarioServicio().getIdUsuarioServicio(),
        "idViaje", p.getViaje().getIdViaje(),
        "metodo", p.getMetodo(),
        "idTarjeta", p.getTarjeta() == null ? null : p.getTarjeta().getIdTarjeta(),
        "valor", p.getValor(),
        "estado", p.getEstado()
      ));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (IllegalStateException e) {
      return ResponseEntity.status(409).body(e.getMessage());
    } catch (RuntimeException e) {
      return ResponseEntity.status(404).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
    }
  }

  // JSON opcional
  @PostMapping("/registrar/json")
  @Transactional
  public ResponseEntity<?> registrarJson(@RequestBody PagoRequest body) {
    try {
      Pago p = service.registrar(
          body.idPago(), body.idUsuarioServicio(), body.idViaje(),
          body.monto(), body.metodoPago(), body.idTarjeta(), body.estado()
      );
      return ResponseEntity.ok(Map.of(
        "idPago", p.getIdPago(),
        "idUsuarioServicio", p.getUsuarioServicio().getIdUsuarioServicio(),
        "idViaje", p.getViaje().getIdViaje(),
        "metodo", p.getMetodo(),
        "idTarjeta", p.getTarjeta() == null ? null : p.getTarjeta().getIdTarjeta(),
        "valor", p.getValor(),
        "estado", p.getEstado()
      ));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (IllegalStateException e) {
      return ResponseEntity.status(409).body(e.getMessage());
    } catch (RuntimeException e) {
      return ResponseEntity.status(404).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
    }
  }

  @GetMapping("/{idPago}")
  public ResponseEntity<?> obtener(@PathVariable Long idPago) {
    return service.obtener(idPago)
        .<ResponseEntity<?>>map(p -> ResponseEntity.ok(Map.of(
          "idPago", p.getIdPago(),
          "idUsuarioServicio", p.getUsuarioServicio().getIdUsuarioServicio(),
          "idViaje", p.getViaje().getIdViaje(),
          "metodo", p.getMetodo(),
          "idTarjeta", p.getTarjeta() == null ? null : p.getTarjeta().getIdTarjeta(),
          "valor", p.getValor(),
          "estado", p.getEstado(),
          "fecha", p.getFecha()
        )))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PatchMapping("/{idPago}/estado")
  @Transactional
  public ResponseEntity<?> actualizarEstado(@PathVariable Long idPago, @RequestParam String estado) {
    try {
      Pago p = service.actualizarEstado(idPago, estado);
      return ResponseEntity.ok(Map.of("idPago", p.getIdPago(), "estado", p.getEstado()));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (RuntimeException e) {
      return ResponseEntity.status(404).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
    }
  }
}
