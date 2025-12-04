// src/main/java/uniandes/edu/co/proyecto/service/impl/ViajeServiceImpl.java
package uniandes.edu.co.proyecto.service.impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uniandes.edu.co.proyecto.modelo.SolicitudServicio;
import uniandes.edu.co.proyecto.modelo.Viaje;
import uniandes.edu.co.proyecto.repositorio.PagoRepository;
import uniandes.edu.co.proyecto.repositorio.SolicitudServicioRepository;
import uniandes.edu.co.proyecto.repositorio.ViajeRepository;
import uniandes.edu.co.proyecto.service.ViajeService;

@Service
public class ViajeServiceImpl implements ViajeService {

    private final ViajeRepository viajeRepo;
    private final SolicitudServicioRepository solicitudRepo;
    private final PagoRepository pagoRepo;

    // Constructor explícito (Spring lo usa para inyectar los beans)
    public ViajeServiceImpl(ViajeRepository viajeRepo,
                            SolicitudServicioRepository solicitudRepo,
                            PagoRepository pagoRepo) {
        this.viajeRepo = viajeRepo;
        this.solicitudRepo = solicitudRepo;
        this.pagoRepo = pagoRepo;
    }

    private static double round2(Double v) {
        if (v == null) return 0d;
        return Math.round(v * 100.0) / 100.0;
      }

      @Override
      @Transactional
      public Map<String, Object> finalizar(Long idViaje, Double distanciaKm, Double costoTotal) {
        if (idViaje == null || idViaje <= 0) {
          throw new IllegalArgumentException("idViaje requerido y positivo");
        }
        // Validaciones de negocio
        if (!viajeRepo.existsById(idViaje)) {
          throw new RuntimeException("Viaje no existe: " + idViaje); // 404
        }
        if (viajeRepo.countAbierto(idViaje) == 0) {
          throw new IllegalStateException("El viaje ya está cerrado"); // 409
        }
        if (distanciaKm != null && distanciaKm < 0) {
          throw new IllegalArgumentException("distanciaKm no puede ser negativa");
        }
        if (costoTotal != null && costoTotal < 0) {
          throw new IllegalArgumentException("costoTotal no puede ser negativo");
        }
    
        // Cerrar viaje (marca HORA_FIN y actualiza (opc) distancia y costo)
        int upd = viajeRepo.cerrarViaje(idViaje, distanciaKm, costoTotal);
        if (upd == 0) {
          throw new RuntimeException("No fue posible cerrar el viaje (posible carrera ya cerrada)");
        }
    
        // (Opcional) Completar pago si estaba EN ESPERA/APROBADO
        if (pagoRepo != null) {
          try { pagoRepo.completarPagoPorViaje(idViaje); } catch (Exception ignore) {}
        }
    
        return Map.of(
          "idViaje", idViaje,
          "cerrado", true,
          "distanciaKmFinal", distanciaKm == null ? "(sin cambios)" : round2(distanciaKm),
          "costoTotalFinal",  costoTotal  == null ? "(sin cambios)" : round2(costoTotal)
        );
      }

    @Override
    @Transactional
    public void finalizarViaje(Long idViaje, Double distanciaKm) {
        if (idViaje == null || distanciaKm == null || distanciaKm <= 0) {
            throw new IllegalArgumentException("idViaje y distanciaKm (>0) son obligatorios");
        }

        // 1) Cargar viaje + solicitud
        Viaje viaje = viajeRepo.findById(idViaje)
                .orElseThrow(() -> new IllegalArgumentException("Viaje inexistente: " + idViaje));

        SolicitudServicio solicitud = viaje.getSolicitud();
        if (solicitud == null) {
            throw new IllegalStateException("El viaje no tiene solicitud asociada");
        }

        if ("FINALIZADA".equalsIgnoreCase(solicitud.getEstado())) {
            throw new IllegalStateException("La solicitud ya se encuentra finalizada");
        }

        // 2) Calcular costo total (misma política que RF8, pero en Java)
        double tarifaKm = tarifaPorKm(solicitud.getTipo(), solicitud.getNivel());
        double total = Math.round(distanciaKm * tarifaKm * 100.0) / 100.0;

        // 3) Actualizar viaje
        viaje.setDistanciaKm(distanciaKm);
        viaje.setCostoTotal(total);
        viaje.setHoraFin(LocalDateTime.now());
        viajeRepo.save(viaje);

        // 4) Actualizar estado de la solicitud
        solicitud.setEstado("FINALIZADA");
        solicitudRepo.save(solicitud);
    }

    // Mantén esta función consistente con lo que usaste en RF8 (sin tocar BD):
    private double tarifaPorKm(String tipo, String nivel) {
        String t = tipo == null ? "" : tipo.toUpperCase();
        String n = nivel == null ? "" : nivel.toUpperCase();
        if ("PASAJEROS".equals(t)) {
            return switch (n) {
                case "CONFORT" -> 2200d;
                case "LARGE"   -> 2800d;
                default        -> 1500d; // ESTANDAR
            };
        } else if ("COMIDA".equals(t)) {
            return 1200d;
        } else if ("MERCANCIAS".equals(t)) {
            return 1800d;
        }
        throw new IllegalArgumentException("Tipo/nivel no soportado: " + tipo + "/" + nivel);
    }
}
