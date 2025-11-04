// src/main/java/uniandes/edu/co/proyecto/service/impl/ViajeServiceImpl.java
package uniandes.edu.co.proyecto.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uniandes.edu.co.proyecto.modelo.SolicitudServicio;
import uniandes.edu.co.proyecto.modelo.Viaje;
import uniandes.edu.co.proyecto.repositorio.SolicitudServicioRepository;
import uniandes.edu.co.proyecto.repositorio.ViajeRepository;
import uniandes.edu.co.proyecto.service.ViajeService;

@Service
public class ViajeServiceImpl implements ViajeService {

    private final ViajeRepository viajeRepo;
    private final SolicitudServicioRepository solicitudRepo;

    // Constructor explícito (Spring lo usa para inyectar los beans)
    public ViajeServiceImpl(ViajeRepository viajeRepo,
                            SolicitudServicioRepository solicitudRepo) {
        this.viajeRepo = viajeRepo;
        this.solicitudRepo = solicitudRepo;
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
