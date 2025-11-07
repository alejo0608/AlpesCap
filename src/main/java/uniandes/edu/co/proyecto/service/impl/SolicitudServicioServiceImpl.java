// src/main/java/uniandes/edu/co/proyecto/service/impl/SolicitudServicioServiceImpl.java
package uniandes.edu.co.proyecto.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uniandes.edu.co.proyecto.modelo.PuntoGeografico;
import uniandes.edu.co.proyecto.repositorio.*;
import uniandes.edu.co.proyecto.service.SolicitudServicioService;
import uniandes.edu.co.proyecto.web.SolicitarServicioRequest;
import uniandes.edu.co.proyecto.web.SolicitarServicioResponse;

@Service
public class SolicitudServicioServiceImpl implements SolicitudServicioService {

  private final TarjetaCreditoRepository tarjetaRepo;
  private final PuntoGeograficoRepository puntoRepo;
  private final SolicitudServicioRepository solicitudRepo;
  private final DisponibilidadRepository dispRepo;
  private final ViajeRepository viajeRepo;
  private final UsuarioServicioRepository usuarioServicioRepo;
  private final PagoRepository pagoRepo; // opcional

  public SolicitudServicioServiceImpl(
      TarjetaCreditoRepository tarjetaRepo,
      PuntoGeograficoRepository puntoRepo,
      SolicitudServicioRepository solicitudRepo,
      DisponibilidadRepository dispRepo,
      ViajeRepository viajeRepo,
      UsuarioServicioRepository usuarioServicioRepo,
      PagoRepository pagoRepo
  ) {
    this.tarjetaRepo = tarjetaRepo;
    this.puntoRepo = puntoRepo;
    this.solicitudRepo = solicitudRepo;
    this.dispRepo = dispRepo;
    this.viajeRepo = viajeRepo;
    this.usuarioServicioRepo = usuarioServicioRepo;
    this.pagoRepo = pagoRepo;
  }

  private static String normalizaTipo(String t) {
    if (t == null) return null;
    String u = t.trim().toUpperCase(Locale.ROOT);
    if (u.equals("MERCANCÍAS")) u = "MERCANCIAS";
    return u;
  }

  private static String normalizaNivel(String n) {
    String u = (n == null ? "ESTANDAR" : n.trim().toUpperCase(Locale.ROOT));
    if (!(u.equals("ESTANDAR") || u.equals("CONFORT") || u.equals("LARGE")))
      throw new IllegalArgumentException("nivel inválido (ESTANDAR|CONFORT|LARGE)");
    return u;
  }

  private static String hoyDiaEs() {
    String d = LocalDate.now(ZoneId.of("America/Bogota")).getDayOfWeek().name();
    return switch (d) {
      case "MONDAY" -> "LUNES";
      case "TUESDAY" -> "MARTES";
      case "WEDNESDAY" -> "MIERCOLES";
      case "THURSDAY" -> "JUEVES";
      case "FRIDAY" -> "VIERNES";
      case "SATURDAY" -> "SABADO";
      default -> "DOMINGO";
    };
  }

  private static double distanciaKm(double lat1, double lon1, double lat2, double lon2) {
    double R = 6371.0;
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    double a = Math.sin(dLat/2)*Math.sin(dLat/2) +
               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
               Math.sin(dLon/2)*Math.sin(dLon/2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    return R * c;
  }

  private static double tarifa(String nivel, double km) {
    String n = normalizaNivel(nivel);
    double base, porKm;
    switch (n) {
      case "CONFORT": base = 3000; porKm = 1600; break;
      case "LARGE":   base = 4000; porKm = 2200; break;
      default:        base = 2500; porKm = 1200; // ESTANDAR
    }
    double k = Math.max(1.0, km);
    return base + porKm * k;
  }

  @Override
  @Transactional
  public SolicitarServicioResponse solicitar(SolicitarServicioRequest req) {
    if (req == null) throw new IllegalArgumentException("request requerido");
    if (req.idSolicitud() == null || req.idSolicitud() <= 0) throw new IllegalArgumentException("idSolicitud requerido");
    if (req.idViaje() == null || req.idViaje() <= 0) throw new IllegalArgumentException("idViaje requerido");
    if (req.idUsuarioServicio() == null || req.idUsuarioServicio() <= 0) throw new IllegalArgumentException("idUsuarioServicio requerido");
    if (req.idPuntoPartida() == null || req.idPuntoPartida() <= 0) throw new IllegalArgumentException("idPuntoPartida requerido");
    if (req.tipo() == null || req.tipo().isBlank()) throw new IllegalArgumentException("tipo requerido");

    String tipo = normalizaTipo(req.tipo());
    String nivel = normalizaNivel(req.nivel());
    String dia = hoyDiaEs();
    LocalDate hoy = LocalDate.now(ZoneId.of("America/Bogota"));
    LocalDateTime ahora = LocalDateTime.now(ZoneId.of("America/Bogota"));

    // 1) Usuario y medio de pago
    if (!usuarioServicioRepo.existsById(req.idUsuarioServicio()))
      throw new RuntimeException("Usuario de servicio no existe: " + req.idUsuarioServicio());
    if (tarjetaRepo.countByUsuario(req.idUsuarioServicio()) == 0)
      throw new IllegalArgumentException("El usuario no tiene medio de pago registrado");

    // 2) Puntos y distancia (usar entidad, no Map)
    PuntoGeografico partida = puntoRepo.findById(req.idPuntoPartida())
        .orElseThrow(() -> new RuntimeException("Punto de partida no existe: " + req.idPuntoPartida()));
    double lat1 = partida.getLatitud();
    double lon1 = partida.getLongitud();

    double lat2 = lat1, lon2 = lon1;
    if (req.idPuntoLlegada() != null) {
      PuntoGeografico llegada = puntoRepo.findById(req.idPuntoLlegada())
          .orElseThrow(() -> new RuntimeException("Punto de llegada no existe: " + req.idPuntoLlegada()));
      lat2 = llegada.getLatitud();
      lon2 = llegada.getLongitud();
    }
    double km = distanciaKm(lat1, lon1, lat2, lon2);
    double costo = tarifa(nivel, km);

    // 3) Elegir disponibilidad (concurrencia segura vía FOR UPDATE SKIP LOCKED en el repo)
    var cand = dispRepo.pickDisponibilidadParaAsignar(dia, tipo);
    if (cand == null) throw new IllegalStateException("No hay conductores disponibles para el tipo solicitado");

    Long idConductor = ((Number)cand.get("ID_USUARIO_CONDUCTOR")).longValue();
    Long idVehiculo  = ((Number)cand.get("ID_VEHICULO")).longValue();

    if (viajeRepo.countViajesAbiertosDeConductor(idConductor) > 0)
      throw new IllegalStateException("El conductor seleccionado se ocupó en paralelo. Intenta de nuevo.");

    // 4) Insertar SOLICITUD + VIAJE (hora_fin NULL) + PAGO EN ESPERA (opcional)
    if (solicitudRepo.existsById(req.idSolicitud()))
      throw new IllegalStateException("idSolicitud ya existe");
    solicitudRepo.insertarSolicitud(
        req.idSolicitud(), tipo, nivel, hoy.toString(), "ASIGNADA",
        req.idUsuarioServicio(), req.idPuntoPartida()
    );

    if (viajeRepo.existsById(req.idViaje()))
      throw new IllegalStateException("idViaje ya existe");
    String ts = ahora.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    viajeRepo.insertarViajeInicio(
        req.idViaje(), hoy.toString(), ts, km, costo,
        idConductor, idVehiculo, req.idPuntoPartida(), req.idSolicitud()
    );

    if (req.idPago() != null) {
      if (pagoRepo.existsById(req.idPago()))
        throw new IllegalStateException("idPago ya existe");
      // Asumiendo que el método de pago es "TARJETA" por defecto, cámbialo si tienes otra lógica
      String metodo = "TARJETA";
      pagoRepo.insertarPago(req.idPago(), req.idViaje(), costo, hoy.toString(), "EN ESPERA", metodo);
    }

    double kmOut = Math.round(km * 100.0) / 100.0;
    double costoOut = Math.round(costo * 100.0) / 100.0;

    return new SolicitarServicioResponse(
        req.idSolicitud(),
        req.idViaje(),
        idConductor,
        idVehiculo,
        kmOut,
        costoOut
    );
  }
}
