package uniandes.edu.co.proyecto.controller;

import java.time.Year;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import uniandes.edu.co.proyecto.repositorio.TarjetaCreditoRepository;

@RestController
@RequestMapping("/tarjetas")
public class TarjetaCreditoController {

  private final TarjetaCreditoRepository repo;

  @PersistenceContext
  private EntityManager em;

  public TarjetaCreditoController(TarjetaCreditoRepository repo) {
    this.repo = repo;
  }

  // ---- utilidades ----
  private static String cleanDigits(String s) {
    return s == null ? "" : s.replaceAll("[^0-9]", "");
  }
  private static boolean luhnOk(String digits) {
    if (digits == null || digits.isBlank()) return false;
    int sum = 0; boolean alt = false;
    for (int i = digits.length() - 1; i >= 0; i--) {
      int n = digits.charAt(i) - '0';
      if (n < 0 || n > 9) return false;
      if (alt) { n *= 2; if (n > 9) n -= 9; }
      sum += n; alt = !alt;
    }
    return sum % 10 == 0;
  }
  private static String mask(String digits) {
    if (digits == null || digits.length() < 4) return "****";
    String last4 = digits.substring(digits.length()-4);
    return "**** **** **** " + last4;
  }
  private static int nowYear() { return Year.now().getValue(); }

  private Long nextIdTarjeta(Long fallback) {
    try {
      Object v = em.createNativeQuery("SELECT TARJETA_CREDITO_SEQ.NEXTVAL FROM DUAL").getSingleResult();
      return ((Number) v).longValue();
    } catch (Exception ignore) {
      return fallback; // fallback si no existe la secuencia
    }
  }

  // ------------------------------------------------------------
  // POST /tarjetas/registrar (x-www-form-urlencoded)  -> id manual
  // ------------------------------------------------------------
  @PostMapping("/registrar")
  @Transactional
  public ResponseEntity<?> registrarManual(
      @RequestParam Long idTarjeta,
      @RequestParam String numero,
      @RequestParam String titular,
      @RequestParam Integer mesVencimiento,
      @RequestParam Integer anioVencimiento,
      @RequestParam Integer cvv,
      @RequestParam Long idUsuarioServicio
  ) {
    try {
      // Validaciones
      if (idTarjeta == null || idTarjeta <= 0) return ResponseEntity.badRequest().body("idTarjeta requerido y positivo");
      if (idUsuarioServicio == null || idUsuarioServicio <= 0) return ResponseEntity.badRequest().body("idUsuarioServicio requerido y positivo");

      if (repo.countUsuarioServicio(idUsuarioServicio) == 0)
        return ResponseEntity.status(404).body("Usuario de servicios no existe: " + idUsuarioServicio);

      String digits = cleanDigits(numero);
      if (digits.length() < 12 || digits.length() > 19) return ResponseEntity.badRequest().body("Número de tarjeta inválido (12–19 dígitos)");
      if (!luhnOk(digits)) return ResponseEntity.badRequest().body("Número de tarjeta inválido (LUHN)");

      if (titular == null || titular.trim().isEmpty()) return ResponseEntity.badRequest().body("titular requerido");
      String tit = titular.trim();
      if (tit.length() > 120) return ResponseEntity.badRequest().body("titular demasiado largo (<=120)");

      if (mesVencimiento == null || mesVencimiento < 1 || mesVencimiento > 12) return ResponseEntity.badRequest().body("mesVencimiento inválido (1–12)");

      int y = nowYear();
      if (anioVencimiento == null || anioVencimiento < y || anioVencimiento > (y + 20))
        return ResponseEntity.badRequest().body("anioVencimiento inválido (>= año actual y <= +20)");

      if (cvv == null || cvv < 0) return ResponseEntity.badRequest().body("cvv requerido");
      int cvvLen = String.valueOf(cvv).length();
      if (cvvLen < 3 || cvvLen > 4) return ResponseEntity.badRequest().body("cvv inválido (3–4 dígitos)");

      // Duplicado por número (normalizado)
      if (repo.countByNumero(digits) > 0)
        return ResponseEntity.status(409).body("Ya existe una tarjeta con ese número");

      // Insert
      int rows = repo.insertarTarjeta(idTarjeta, digits, tit, mesVencimiento, anioVencimiento, cvv, idUsuarioServicio);
      if (rows == 0) return ResponseEntity.internalServerError().body("No fue posible insertar la tarjeta");

      // Respuesta
      return ResponseEntity.ok(Map.of(
        "idTarjeta", idTarjeta,
        "numeroMasked", mask(digits),
        "titular", tit,
        "mesVencimiento", mesVencimiento,
        "anioVencimiento", anioVencimiento,
        "idUsuarioServicio", idUsuarioServicio
      ));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (IllegalStateException e) {
      return ResponseEntity.status(409).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
    }
  }

  // ------------------------------------------------------------
  // POST /tarjetas/new/auto (application/json) -> ID autogenerado
  // body: { numero, titular, mesVencimiento, anioVencimiento, cvv, idUsuarioServicio }
  // ------------------------------------------------------------
  @PostMapping("/new/auto")
  @Transactional
  public ResponseEntity<?> registrarAuto(@RequestBody Map<String,Object> body) {
    try {
      String numero = body.getOrDefault("numero","").toString();
      String titular = body.getOrDefault("titular","").toString();
      Integer mesV = body.get("mesVencimiento") == null ? null : Integer.valueOf(body.get("mesVencimiento").toString());
      Integer anioV = body.get("anioVencimiento") == null ? null : Integer.valueOf(body.get("anioVencimiento").toString());
      Integer cvv   = body.get("cvv") == null ? null : Integer.valueOf(body.get("cvv").toString());
      Long idUsr    = body.get("idUsuarioServicio") == null ? null : Long.valueOf(body.get("idUsuarioServicio").toString());

      // Reutilizamos la lógica del endpoint manual, generando el ID
      Long idTarjeta = nextIdTarjeta(System.currentTimeMillis());

      // Reusamos la lógica llamando al método manual
      return registrarManual(idTarjeta, numero, titular, mesV, anioV, cvv, idUsr);

    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
    }
  }

  // ------------------------------------------------------------
  // GET /tarjetas/{idTarjeta}
  // ------------------------------------------------------------
  @GetMapping("/{idTarjeta}")
  public ResponseEntity<?> obtener(@PathVariable Long idTarjeta) {
    return repo.findById(idTarjeta)
      .<ResponseEntity<?>>map(t -> ResponseEntity.ok(Map.of(
        "idTarjeta", t.getIdTarjeta(),
        "numeroMasked", mask(cleanDigits(t.getNumero())),
        "titular", t.getNombre(),
        "mesVencimiento", t.getMesVencimiento(),
        "anioVencimiento", t.getAnioVencimiento(),
        "idUsuarioServicio", t.getUsuarioServicio().getIdUsuarioServicio()
      )))
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  // ------------------------------------------------------------
  // DELETE /tarjetas/{idTarjeta}
  // ------------------------------------------------------------
  @DeleteMapping("/{idTarjeta}")
  @Transactional
  public ResponseEntity<?> eliminar(@PathVariable Long idTarjeta) {
    if (idTarjeta == null || idTarjeta <= 0) return ResponseEntity.badRequest().body("idTarjeta requerido");
    if (!repo.existsById(idTarjeta)) return ResponseEntity.notFound().build();
    repo.deleteById(idTarjeta);
    return ResponseEntity.ok(Map.of("eliminado", idTarjeta));
  }

  @GetMapping("/ping")
  public String ping() { return "tarjetas-ok"; }
}
