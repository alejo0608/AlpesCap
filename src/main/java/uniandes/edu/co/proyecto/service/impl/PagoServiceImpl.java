// src/main/java/uniandes/edu/co/proyecto/service/impl/PagoServiceImpl.java
package uniandes.edu.co.proyecto.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uniandes.edu.co.proyecto.modelo.Pago;
import uniandes.edu.co.proyecto.repositorio.PagoRepository;
import uniandes.edu.co.proyecto.repositorio.ViajeRepository;
import uniandes.edu.co.proyecto.service.PagoService;

@Service
public class PagoServiceImpl implements PagoService {

  private final PagoRepository pagoRepo;
  private final ViajeRepository viajeRepo;

  public PagoServiceImpl(PagoRepository pagoRepo, ViajeRepository viajeRepo) {
    this.pagoRepo = pagoRepo;
    this.viajeRepo = viajeRepo;
  }

  private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private static String normalizaEstado(String e) {
    if (e == null) throw new IllegalArgumentException("estado requerido");
    String u = e.trim().toUpperCase(Locale.ROOT);
    if (!(u.equals("EN ESPERA") || u.equals("COMPLETADO") || u.equals("RECHAZADO"))) {
      throw new IllegalArgumentException("estado inválido (EN ESPERA|COMPLETADO|RECHAZADO)");
    }
    return u;
  }

  private static String validaFecha(String fecha) {
    if (fecha == null || fecha.isBlank()) throw new IllegalArgumentException("fecha requerida (YYYY-MM-DD)");
    try {
      LocalDate.parse(fecha.trim(), FMT);
      return fecha.trim();
    } catch (DateTimeParseException ex) {
      throw new IllegalArgumentException("formato de fecha inválido, use YYYY-MM-DD");
    }
  }

  @Override
  @Transactional
  public Pago registrar(Long idPago, Long idViaje, Double monto, String fecha, String estado) {
    if (idPago == null || idPago <= 0) throw new IllegalArgumentException("idPago requerido y positivo");
    if (idViaje == null || idViaje <= 0) throw new IllegalArgumentException("idViaje requerido y positivo");
    if (monto == null || monto <= 0) throw new IllegalArgumentException("monto debe ser > 0");

    String f = validaFecha(fecha);
    String st = normalizaEstado(estado);

    if (!viajeRepo.existsById(idViaje)) {
      throw new RuntimeException("Viaje no existe: " + idViaje); // 404
    }
    if (pagoRepo.existsById(idPago)) {
      throw new IllegalStateException("idPago ya existe"); // 409
    }
    if (pagoRepo.countByViaje(idViaje) > 0) {
      throw new IllegalStateException("El viaje ya tiene un pago asociado"); // 409 (UQ_PAGO_VIAJE)
    }
    String metodo = "TARJETA";
    pagoRepo.insertarPago(idPago, idViaje, monto, f, st, metodo);

    return pagoRepo.findById(idPago)
        .orElseThrow(() -> new RuntimeException("No fue posible recuperar el pago insertado"));
  }

  @Override
  public Optional<Pago> obtener(Long idPago) {
    return pagoRepo.findById(idPago);
  }

  @Override
  @Transactional
  public Pago actualizarEstado(Long idPago, String estado) {
    if (idPago == null || idPago <= 0) throw new IllegalArgumentException("idPago requerido y positivo");
    String st = normalizaEstado(estado);

    if (!pagoRepo.existsById(idPago)) {
      throw new RuntimeException("Pago no existe: " + idPago); // 404
    }
    int upd = pagoRepo.actualizarEstado(idPago, st);
    if (upd == 0) throw new RuntimeException("No fue posible actualizar el estado del pago");

    return pagoRepo.findById(idPago)
        .orElseThrow(() -> new RuntimeException("No fue posible recuperar el pago actualizado"));
  }
}
