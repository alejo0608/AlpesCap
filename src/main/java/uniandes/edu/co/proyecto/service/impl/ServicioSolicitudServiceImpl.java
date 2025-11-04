// src/main/java/uniandes/edu/co/proyecto/service/impl/ServicioSolicitudServiceImpl.java
package uniandes.edu.co.proyecto.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import uniandes.edu.co.proyecto.modelo.*;
import uniandes.edu.co.proyecto.repositorio.*;
import uniandes.edu.co.proyecto.service.ServicioSolicitudService;
import uniandes.edu.co.proyecto.web.SolicitarServicioRequest;

@Service
public class ServicioSolicitudServiceImpl implements ServicioSolicitudService {

  private final SolicitudServicioRepository solRepo;
  private final UsuarioServicioRepository usrRepo;
  private final PuntoGeograficoRepository puntoRepo;
  private final DisponibilidadRepository dispRepo;
  private final VehiculoRepository vehRepo;
  private final UsuarioConductorRepository condRepo;
  private final ViajeRepository viajeRepo;
  private final PagoRepository pagoRepo;
  private final TarjetaCreditoRepository tarjetaRepo;

  public ServicioSolicitudServiceImpl(
      SolicitudServicioRepository solRepo,
      UsuarioServicioRepository usrRepo,
      PuntoGeograficoRepository puntoRepo,
      DisponibilidadRepository dispRepo,
      VehiculoRepository vehRepo,
      UsuarioConductorRepository condRepo,
      ViajeRepository viajeRepo,
      PagoRepository pagoRepo,
      TarjetaCreditoRepository tarjetaRepo) {
    this.solRepo = solRepo;
    this.usrRepo = usrRepo;
    this.puntoRepo = puntoRepo;
    this.dispRepo = dispRepo;
    this.vehRepo = vehRepo;
    this.condRepo = condRepo;
    this.viajeRepo = viajeRepo;
    this.pagoRepo = pagoRepo;
    this.tarjetaRepo = tarjetaRepo;
  }

  private static final Set<String> TIPOS =
      Set.of("PASAJEROS", "COMIDA", "MERCANCIAS", "MERCANCÍAS");
  private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private static String normalizaTipo(String t) {
    if (t == null) return null;
    String u = t.trim().toUpperCase();
    if (u.equals("MERCANCÍAS")) u = "MERCANCIAS";
    return u;
  }
  private static String normalizaNivel(String n) {
    if (n == null || n.isBlank()) return "ESTANDAR";
    return n.trim().toUpperCase();
  }
  private static String diaOracle(LocalDate d) {
    return switch (d.getDayOfWeek()) {
      case MONDAY    -> "LUNES";
      case TUESDAY   -> "MARTES";
      case WEDNESDAY -> "MIERCOLES";
      case THURSDAY  -> "JUEVES";
      case FRIDAY    -> "VIERNES";
      case SATURDAY  -> "SABADO";
      case SUNDAY    -> "DOMINGO";
    };
  }
  private static double tarifaKm(String tipo, String nivel) {
    if (tipo.equals("COMIDA")) return nivel.equals("PREMIUM") ? 1600 : 1200;
    if (tipo.startsWith("MERCANC")) return nivel.equals("PREMIUM") ? 2000 : 1500;
    return nivel.equals("PREMIUM") ? 1800 : 1000; // PASAJEROS
  }
  private static double tarifaBase(String nivel) {
    return nivel.equals("PREMIUM") ? 6000 : 3500;
  }
  private static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
    final double R = 6371.0;
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    double a = Math.sin(dLat/2)*Math.sin(dLat/2)
        + Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))
        * Math.sin(dLon/2)*Math.sin(dLon/2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    return R * c;
  }

  @Override
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public Map<String, Object> solicitarServicio(SolicitarServicioRequest in) {
    // 0) Validaciones de entrada
    if (in == null) throw new IllegalArgumentException("body requerido");
    if (in.idSolicitud() == null || in.idSolicitud() <= 0) throw new IllegalArgumentException("idSolicitud requerido");
    if (in.idViaje() == null || in.idViaje() <= 0) throw new IllegalArgumentException("idViaje requerido");
    if (in.idPago() == null || in.idPago() <= 0) throw new IllegalArgumentException("idPago requerido");
    if (in.idUsuarioServicio() == null || in.idUsuarioServicio() <= 0) throw new IllegalArgumentException("idUsuarioServicio requerido");
    if (in.idPuntoPartida() == null || in.idPuntoPartida() <= 0) throw new IllegalArgumentException("idPuntoPartida requerido");
    if (in.idPuntoLlegada() == null || in.idPuntoLlegada() <= 0) throw new IllegalArgumentException("idPuntoLlegada requerido");

    String tipo = normalizaTipo(in.tipoServicio());
    if (tipo == null || !TIPOS.contains(tipo)) throw new IllegalArgumentException("tipoServicio inválido");
    String nivel = normalizaNivel(in.nivel());

    if (solRepo.existsById(in.idSolicitud())) throw new IllegalStateException("idSolicitud ya existe");
    if (viajeRepo.existsById(in.idViaje()))   throw new IllegalStateException("idViaje ya existe");
    if (pagoRepo.existsById(in.idPago()))     throw new IllegalStateException("idPago ya existe");

    UsuarioServicio usuario = usrRepo.findById(in.idUsuarioServicio())
        .orElseThrow(() -> new RuntimeException("UsuarioServicio no existe: " + in.idUsuarioServicio()));
    PuntoGeografico pPart = puntoRepo.findById(in.idPuntoPartida())
        .orElseThrow(() -> new RuntimeException("Punto partida no existe: " + in.idPuntoPartida()));
    PuntoGeografico pLleg = puntoRepo.findById(in.idPuntoLlegada())
        .orElseThrow(() -> new RuntimeException("Punto llegada no existe: " + in.idPuntoLlegada()));

    // 1) Verificar medio de pago
    if (tarjetaRepo.countByUsuarioServicio(in.idUsuarioServicio()) == 0)
      throw new IllegalStateException("Usuario no tiene medio de pago");

    // 2) Elegir disponibilidad con LOCK (evita doble asignación)
    LocalDateTime now = LocalDateTime.now();
    String tsNow = now.format(TS); // "yyyy-MM-dd HH:mm:ss"
    String dia = diaOracle(now.toLocalDate());

    var cand = dispRepo.pickDisponibilidadParaAsignar(
        pPart.getCiudad().getIdCiudad(), dia, tipo, tsNow);
    if (cand == null) throw new IllegalStateException("No hay conductor disponible en este momento");

    Long idConductor = cand.getIdUsuarioConductor();
    Long idVehiculo  = cand.getIdVehiculo();

    UsuarioConductor conductor = condRepo.findById(idConductor)
        .orElseThrow(() -> new RuntimeException("Conductor no existe: " + idConductor));
    Vehiculo vehiculo = vehRepo.findById(idVehiculo)
        .orElseThrow(() -> new RuntimeException("Vehiculo no existe: " + idVehiculo));

    // 3) Calcular costo (Haversine + tarifa)
    double km = haversineKm(pPart.getLatitud(), pPart.getLongitud(), pLleg.getLatitud(), pLleg.getLongitud());
    double costo = Math.round(tarifaBase(nivel) + km * tarifaKm(tipo, nivel));

    // 4) Persistir: Solicitud -> Viaje -> Pago (misma transacción)
    SolicitudServicio sol = new SolicitudServicio();
    sol.setIdSolicitud(in.idSolicitud());
    sol.setTipo(tipo);
    sol.setNivel(nivel);
    sol.setFecha(now.toLocalDate());
    sol.setEstado("ASIGNADA");
    sol.setUsuarioServicio(usuario);
    sol.setPuntoPartida(pPart);
    sol.setPuntoLlegada(pLleg);
    solRepo.save(sol);

    Viaje v = new Viaje();
    v.setIdViaje(in.idViaje());
    v.setFechaAsignacion(now.toLocalDate());
    v.setHoraInicio(now);
    v.setHoraFin(null); 
    v.setDistanciaKm(km);
    v.setCostoTotal(costo);
    v.setUsuarioConductor(conductor);
    v.setVehiculo(vehiculo);
    v.setPuntoPartida(pPart);
    v.setSolicitud(sol);
    viajeRepo.save(v);

    Pago pg = new Pago();
    pg.setIdPago(in.idPago());
    pg.setMonto(costo);
    pg.setFecha(now.toLocalDate());
    pg.setEstado("APROBADO");
    pg.setViaje(v);
    pagoRepo.save(pg);

    // 5) Respuesta
    return Map.of(
        "idSolicitud", sol.getIdSolicitud(),
        "idViaje", v.getIdViaje(),
        "idPago", pg.getIdPago(),
        "conductor", Map.of("id", conductor.getIdUsuarioConductor(), "nombre", conductor.getNombre()),
        "vehiculo", Map.of("id", vehiculo.getIdVehiculo(), "placa", vehiculo.getPlaca(), "tipo", vehiculo.getTipo()),
        "origen", Map.of("idPunto", pPart.getIdPunto(), "lat", pPart.getLatitud(), "lon", pPart.getLongitud()),
        "destino", Map.of("idPunto", pLleg.getIdPunto(), "lat", pLleg.getLatitud(), "lon", pLleg.getLongitud()),
        "kmEstimados", km,
        "costo", costo,
        "estado", "ASIGNADA",
        "timestamp", now.toString()
    );
  }
}
