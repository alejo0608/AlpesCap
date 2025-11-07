// src/main/java/uniandes/edu/co/proyecto/service/impl/PagoServiceImpl.java
package uniandes.edu.co.proyecto.service.impl;

import java.util.Locale;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uniandes.edu.co.proyecto.modelo.Pago;
import uniandes.edu.co.proyecto.repositorio.PagoRepository;
import uniandes.edu.co.proyecto.repositorio.TarjetaCreditoRepository;
import uniandes.edu.co.proyecto.repositorio.ViajeRepository;
import uniandes.edu.co.proyecto.service.PagoService;

@Service
public class PagoServiceImpl implements PagoService {

  private final PagoRepository pagoRepo;
  private final ViajeRepository viajeRepo;
  private final TarjetaCreditoRepository tarjetaRepo;

  public PagoServiceImpl(PagoRepository pagoRepo,
                         ViajeRepository viajeRepo,
                         TarjetaCreditoRepository tarjetaRepo) {
    this.pagoRepo   = pagoRepo;
    this.viajeRepo  = viajeRepo;
    this.tarjetaRepo = tarjetaRepo;
  }

  // ========= Helpers =========

  private static String normEstado(String e) {
    if (e == null) throw new IllegalArgumentException("estado requerido");
    String u = e.trim().toUpperCase(Locale.ROOT);
    // Aceptamos los cuatro estados típicos del flujo
    if (!(u.equals("APROBADO") || u.equals("EN ESPERA") || u.equals("COMPLETADO") || u.equals("RECHAZADO"))) {
      throw new IllegalArgumentException("estado inválido (APROBADO|EN ESPERA|COMPLETADO|RECHAZADO)");
    }
    return u;
  }

  private static String normMetodo(String m) {
    if (m == null) throw new IllegalArgumentException("metodoPago requerido");
    String u = m.trim().toUpperCase(Locale.ROOT);
    if (!(u.equals("TARJETA") || u.equals("EFECTIVO") || u.equals("WALLET") || u.equals("PSE"))) {
      throw new IllegalArgumentException("metodoPago inválido (TARJETA|EFECTIVO|WALLET|PSE)");
    }
    return u;
  }

  // ========= API =========
  /**
   * Registra un pago para un viaje (un pago por viaje).
   * Usa SYSDATE en BD; si metodo = TARJETA, idTarjeta es obligatorio y se valida que sea del usuario y esté vigente.
   */
  @Override
  @Transactional
  public Pago registrar(Long idPago,
                        Long idUsuarioServicio,
                        Long idViaje,
                        Double monto,
                        String metodoPago,
                        Long idTarjeta,
                        String estado) {

    // Validaciones básicas
    if (idPago == null || idPago <= 0) throw new IllegalArgumentException("idPago requerido y positivo");
    if (idUsuarioServicio == null || idUsuarioServicio <= 0) throw new IllegalArgumentException("idUsuarioServicio requerido y positivo");
    if (idViaje == null || idViaje <= 0) throw new IllegalArgumentException("idViaje requerido y positivo");
    if (monto == null || monto <= 0) throw new IllegalArgumentException("monto debe ser > 0");

    String metodo = normMetodo(metodoPago);
    String st = normEstado(estado);

    // FK viaje
    if (!viajeRepo.existsById(idViaje)) {
      throw new RuntimeException("Viaje no existe: " + idViaje); // 404
    }
    // Unicidad por viaje
    if (pagoRepo.countByViaje(idViaje) > 0) {
      throw new IllegalStateException("El viaje ya tiene un pago asociado"); // 409
    }
    // Unicidad por idPago
    if (pagoRepo.existsById(idPago)) {
      throw new IllegalStateException("idPago ya existe"); // 409
    }

    // Si TARJETA, validar tarjeta del usuario y vigencia
    Long idTarjetaFinal = null;
    if ("TARJETA".equals(metodo)) {
      if (idTarjeta == null || idTarjeta <= 0) {
        throw new IllegalArgumentException("idTarjeta requerido cuando metodoPago=TARJETA");
      }
      if (tarjetaRepo.countTarjetaVigenteDeUsuario(idTarjeta, idUsuarioServicio) == 0) {
        throw new IllegalStateException("Tarjeta no vigente o no pertenece al usuario"); // 409
      }
      idTarjetaFinal = idTarjeta;
    }

    // Insert
    try {
      pagoRepo.insertarPagoConViaje(
          idPago, idUsuarioServicio, metodo, idTarjetaFinal, idViaje, monto, st
      );
    } catch (DataIntegrityViolationException ex) {
      // Índices únicos / FKs
      throw new IllegalStateException("No fue posible registrar el pago (restricción BD)", ex);
    }

    // Retornar fresco desde BD
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
    String st = normEstado(estado);

    if (!pagoRepo.existsById(idPago)) {
      throw new RuntimeException("Pago no existe: " + idPago); // 404
    }
    int upd = pagoRepo.actualizarEstado(idPago, st);
    if (upd == 0) throw new RuntimeException("No fue posible actualizar el estado del pago");

    return pagoRepo.findById(idPago)
        .orElseThrow(() -> new RuntimeException("No fue posible recuperar el pago actualizado"));
  }
}
