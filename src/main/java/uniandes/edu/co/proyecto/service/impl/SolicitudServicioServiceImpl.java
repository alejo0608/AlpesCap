package uniandes.edu.co.proyecto.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uniandes.edu.co.proyecto.modelo.PuntoGeografico;
import uniandes.edu.co.proyecto.repositorio.DisponibilidadRepository;
import uniandes.edu.co.proyecto.repositorio.PagoRepository;
import uniandes.edu.co.proyecto.repositorio.PuntoGeograficoRepository;
import uniandes.edu.co.proyecto.repositorio.SolicitudServicioRepository;
import uniandes.edu.co.proyecto.repositorio.TarjetaCreditoRepository;
import uniandes.edu.co.proyecto.repositorio.ViajeRepository;
import uniandes.edu.co.proyecto.service.SolicitudServicioService;
import uniandes.edu.co.proyecto.web.SolicitudServicioRequest;

/**
 * Implementación transaccional de RF8: Solicitar un servicio.
 *
 * Flujo (ACID, una sola transacción):
 *  1) Validar usuario y medio de pago (si TARJETA, verificar vigencia y pertenencia).
 *  2) Validar puntos geográficos y tomar ciudad de origen.
 *  3) Tomar una disponibilidad candidata con bloqueo (FOR UPDATE SKIP LOCKED).
 *  4) Calcular distancia y costo (tarifa simple; puede externalizarse a tabla TARIFA).
 *  5) Insertar VIAJE.
 *  6) Insertar PAGO (multimétodo: TARJETA/EFECTIVO/WALLET/PSE). Si falla, rollback total.
 *  7) Responder con los ids y datos calculados.
 */
@Service
public class SolicitudServicioServiceImpl implements SolicitudServicioService {

  private final TarjetaCreditoRepository tarjetaRepo;
  private final PuntoGeograficoRepository puntoRepo;
  private final DisponibilidadRepository dispRepo;
  private final ViajeRepository viajeRepo;
  private final PagoRepository pagoRepo;
  private final SolicitudServicioRepository solRepo;

  // Zona horaria operativa
  private static final ZoneId BOG = ZoneId.of("America/Bogota");

  // Nombres de secuencias (ajusta si difieren en tu BD)
  private static final String SEQ_VIAJE = "VIAJE_SEQ";
  private static final String SEQ_PAGO  = "PAGO_SEQ";

  @PersistenceContext
  private EntityManager em;

  public SolicitudServicioServiceImpl(
      TarjetaCreditoRepository tarjetaRepo,
      PuntoGeograficoRepository puntoRepo,
      DisponibilidadRepository dispRepo,
      ViajeRepository viajeRepo,
      PagoRepository pagoRepo,
      SolicitudServicioRepository solRepo
  ) {
    this.tarjetaRepo = tarjetaRepo;
    this.puntoRepo   = puntoRepo;
    this.dispRepo    = dispRepo;
    this.viajeRepo   = viajeRepo;
    this.pagoRepo    = pagoRepo;
    this.solRepo     = solRepo;
  }

  // ================= Helpers reutilizables =================
  private static String up(String s) { return s == null ? "" : s.trim().toUpperCase(Locale.ROOT); }

  private static String diaHoyUpperEs() {
    DayOfWeek d = LocalDate.now(BOG).getDayOfWeek();
    return switch (d) {
      case MONDAY    -> "LUNES";
      case TUESDAY   -> "MARTES";
      case WEDNESDAY -> "MIERCOLES";
      case THURSDAY  -> "JUEVES";
      case FRIDAY    -> "VIERNES";
      case SATURDAY  -> "SABADO";
      case SUNDAY    -> "DOMINGO";
    };
  }

  private static void assertTipo(String t) {
    if (!("PASAJEROS".equals(t) || "COMIDA".equals(t) || "MERCANCIAS".equals(t) || "MERCANCÍAS".equals(t))) {
      throw new IllegalArgumentException("tipo inválido (PASAJEROS|COMIDA|MERCANCIAS)");
    }
  }

  private static void assertNivel(String n) {
    if (!("ESTANDAR".equals(n) || "CONFORT".equals(n) || "LARGE".equals(n))) {
      throw new IllegalArgumentException("nivel inválido (ESTANDAR|CONFORT|LARGE)");
    }
  }

  private static boolean isMetodoValido(String m) {
    if (m == null) return false;
    String M = m.trim().toUpperCase(Locale.ROOT);
    return M.equals("TARJETA") || M.equals("EFECTIVO") || M.equals("WALLET") || M.equals("PSE");
  }

  private static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
    double R = 6371.0;
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    double a = Math.sin(dLat/2)*Math.sin(dLat/2) +
               Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*
               Math.sin(dLon/2)*Math.sin(dLon/2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    return R * c;
  }

  /** Tarifa simple parametrizada (puedes reemplazar por tabla TARIFA más adelante). */
  private static double calcularTarifa(String tipo, String nivel, double km) {
    double base = 3500.0, porKm = 1200.0, mult = 1.0;
    if ("PASAJEROS".equals(tipo)) {
      if ("CONFORT".equals(nivel)) mult = 1.2;
      else if ("LARGE".equals(nivel)) mult = 1.5;
    }
    return base + porKm * Math.max(1.0, km) * mult;
  }

  private static double round2(double v) { return Math.round(v * 100.0) / 100.0; }
  private static long nowId() { return System.currentTimeMillis(); }

  /** Intenta usar una secuencia Oracle; si no existe, vuelve al fallback. */
  private Long nextId(String sequenceName, Long fallback) {
    try {
      Object o = em.createNativeQuery("SELECT " + sequenceName + ".NEXTVAL FROM DUAL").getSingleResult();
      return ((Number) o).longValue();
    } catch (Exception ignore) {
      return fallback;
    }
  }

  // ============================ RF8 ============================
  /**
   * Orquesta RF8 en una sola transacción ACID.
   *
   * @param req parámetros de la solicitud (usuario, puntos, tipo/nivel, método y opcionalmente ids sugeridos)
   * @return mapa con idViaje, idPago, método, idTarjeta, ids de conductor/vehículo, distancia y costo.
   */
  @Override
  @Transactional
  public Map<String, Object> solicitar(SolicitudServicioRequest req) {
    if (req == null) throw new IllegalArgumentException("request requerido");

    // 0) Normalizaciones
    String tipo  = up(req.tipo());
    assertTipo(tipo);
    String nivel = up(req.nivel());
    if ("PASAJEROS".equals(tipo)) assertNivel(nivel); else nivel = "N/A";

    Long idUsrServ = req.idUsuarioServicio();
    Long idPartida = req.idPuntoPartida();
    Long idLlegada = req.idPuntoLlegada();
    if (idUsrServ == null || idUsrServ <= 0) throw new IllegalArgumentException("idUsuarioServicio requerido");
    if (idPartida == null || idPartida <= 0) throw new IllegalArgumentException("idPuntoPartida requerido");
    if (idLlegada == null || idLlegada <= 0) throw new IllegalArgumentException("idPuntoLlegada requerido");

    // Método de pago (default TARJETA)
    String metodo = up(req.metodoPago());
    if (metodo.isBlank()) metodo = "TARJETA";
    if (!isMetodoValido(metodo)) throw new IllegalArgumentException("metodoPago inválido (TARJETA|EFECTIVO|WALLET|PSE)");

    // 1) Usuario y medio disponible
    if (tarjetaRepo.countUsuarioServicio(idUsrServ) == 0)
      throw new RuntimeException("Usuario de servicios no existe: " + idUsrServ);

    Long idTarjeta = null;
    if ("TARJETA".equals(metodo)) {
      // idTarjeta específico o cualquiera vigente
      if (req.idTarjeta() != null && req.idTarjeta() > 0) {
        if (tarjetaRepo.countTarjetaVigenteDeUsuario(req.idTarjeta(), idUsrServ) == 0)
          throw new IllegalStateException("Tarjeta no vigente o no pertenece al usuario");
        idTarjeta = req.idTarjeta();
      } else {
        if (tarjetaRepo.countTarjetasVigentes(idUsrServ) == 0)
          throw new IllegalStateException("Usuario sin medio de pago vigente (tarjeta)");
        idTarjeta = tarjetaRepo.findAnyTarjetaVigente(idUsrServ);
        if (idTarjeta == null) throw new IllegalStateException("No se encontró tarjeta vigente");
      }
    }

    // 2) Puntos y ciudad
    PuntoGeografico p1 = puntoRepo.findById(idPartida)
        .orElseThrow(() -> new RuntimeException("Punto partida no existe: " + idPartida));
    PuntoGeografico p2 = puntoRepo.findById(idLlegada)
        .orElseThrow(() -> new RuntimeException("Punto llegada no existe: " + idLlegada));
    Long idCiudadOrigen = p1.getCiudad().getIdCiudad();

    // 3) Pick disponibilidad con bloqueo (por ciudad, día y tipo)
    String dia = diaHoyUpperEs();
    var pick = dispRepo.pickEnCiudad(idCiudadOrigen, dia, tipo);
    if (pick == null || pick.isEmpty())
      throw new IllegalStateException("No hay conductor disponible en este momento");

    Long idConductor = ((Number) pick.get("ID_USUARIO_CONDUCTOR")).longValue();
    Long idVehiculo  = ((Number) pick.get("ID_VEHICULO")).longValue();

    // 4) Distancia y costo
    double distKm = haversineKm(p1.getLatitud(), p1.getLongitud(), p2.getLatitud(), p2.getLongitud());
    double costo  = calcularTarifa(tipo, nivel, distKm);
    // 3) SOLICITUD
    Long idSolicitud = nextId("SOLICITUD_SEQ", nowId()+2);
    solRepo.insertarSolicitud(idSolicitud, idUsrServ, idPartida, idLlegada, tipo, nivel);
    
    // 4) VIAJE (FK a SOLICITUD)
    Long idViaje = (req.idViaje()!=null && req.idViaje()>0) ? req.idViaje() : nextId(SEQ_VIAJE, nowId()+1);
    try {
      viajeRepo.insertarViaje(idViaje, idConductor, idVehiculo, idPartida, idSolicitud, distKm, costo);
    } catch (DataIntegrityViolationException e) {
      throw new IllegalStateException("No fue posible iniciar el viaje (duplicado o restricción BD)");
    }
    
    // 6) PAGO idempotente (si ya existe para este viaje, lo reusamos)
    Long idPagoExistente = null;
    if (pagoRepo.countByViaje(idViaje) > 0) {
      idPagoExistente = pagoRepo.findIdByViaje(idViaje);
    }
    
    // Si ya hay pago, devolvemos ese id; si no, insertamos uno nuevo
    Long idPago;
    if (idPagoExistente != null) {
      idPago = idPagoExistente;
    } else {
      idPago = /* usa tu generador actual, idealmente secuencia PAGO_SEQ */ 
               ((req.idPago() != null && req.idPago() > 0) ? req.idPago() : /* next from PAGO_SEQ */ nextId(SEQ_PAGO, nowId()+3));
      try {
        pagoRepo.insertarPagoConViaje(idPago, metodo, idTarjeta, idViaje, costo, "COMPLETADO");
      } catch (org.springframework.dao.DataIntegrityViolationException e) {
        // Si fue carrera de inserción (doble clic), verificamos si ya quedó creado
        Long ya = pagoRepo.findIdByViaje(idViaje);
        if (ya != null) {
          idPago = ya; // idempotente: devolvemos el existente
        } else {
          throw new IllegalStateException("No fue posible registrar el pago (duplicado o restricción BD)");
        }
      }
    }

    // 7) Respuesta
    return Map.of(
      "idViaje", idViaje,
      "idPago", idPago,
      "metodoPago", metodo,
      "idTarjeta", idTarjeta,
      "idConductor", idConductor,
      "idVehiculo", idVehiculo,
      "distanciaKm", round2(distKm),
      "costoTotal", round2(costo),
      "tipo", tipo,
      "nivel", nivel
    );
  }
}
