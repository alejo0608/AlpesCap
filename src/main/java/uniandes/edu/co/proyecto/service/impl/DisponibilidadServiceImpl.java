// src/main/java/uniandes/edu/co/proyecto/service/impl/DisponibilidadServiceImpl.java
package uniandes.edu.co.proyecto.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uniandes.edu.co.proyecto.repositorio.DisponibilidadRepository;
import uniandes.edu.co.proyecto.service.DisponibilidadService;

@Service
public class DisponibilidadServiceImpl implements DisponibilidadService {

  private final DisponibilidadRepository repo;

  public DisponibilidadServiceImpl(DisponibilidadRepository repo) {
    this.repo = repo;
  }

  private static final Set<String> DIAS = Set.of(
      "LUNES","MARTES","MIERCOLES","MIÉRCOLES","JUEVES","VIERNES","SABADO","SÁBADO","DOMINGO");
  private static final Set<String> TIPOS = Set.of(
      "PASAJEROS","COMIDA","MERCANCIAS","MERCANCÍAS");
  private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private static String normalizaDia(String d) {
    if (d == null) return null;
    String u = d.trim().toUpperCase();
    if (u.equals("MIÉRCOLES")) u = "MIERCOLES";
    if (u.equals("SÁBADO")) u = "SABADO";
    return u;
  }

  private static String normalizaTipo(String t) {
    if (t == null) return null;
    String u = t.trim().toUpperCase();
    if (u.equals("MERCANCÍAS")) u = "MERCANCIAS";
    return u;
  }

  @Override
  @Transactional
  public Map<String, Object> modificar(Long idDisponibilidad,
                                       String dia,
                                       String horaInicio,
                                       String horaFin,
                                       String tipoServicio) {

    if (idDisponibilidad == null || idDisponibilidad <= 0)
      throw new IllegalArgumentException("idDisponibilidad requerido y positivo");

    String diaNorm = normalizaDia(dia);
    if (diaNorm == null || !DIAS.contains(diaNorm))
      throw new IllegalArgumentException("dia inválido (use LUNES..DOMINGO)");

    String tipoNorm = normalizaTipo(tipoServicio);
    if (tipoNorm == null || !TIPOS.contains(tipoNorm))
      throw new IllegalArgumentException("tipoServicio inválido (PASAJEROS|COMIDA|MERCANCIAS)");

    LocalDateTime hi, hf;
    try {
      hi = LocalDateTime.parse(horaInicio.trim(), TS);
      hf = LocalDateTime.parse(horaFin.trim(), TS);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Formato de fecha/hora inválido. Use YYYY-MM-DD HH:MM:SS");
    }
    if (!hf.isAfter(hi))
      throw new IllegalArgumentException("horaFin debe ser mayor a horaInicio");

    // 1) Obtener FK actuales (aseguramos que exista)
    Long idConductor = repo.findConductorByDisponibilidad(idDisponibilidad);
    Long idVehiculo  = repo.findVehiculoByDisponibilidad(idDisponibilidad);
    if (idConductor == null || idVehiculo == null)
      throw new RuntimeException("Disponibilidad no existe: " + idDisponibilidad);

    // 2) Validar que no haya solapes (excluyendo el propio registro)
    int solapeCond = repo.countSolapeConductorExcluyendo(
        idConductor, diaNorm, horaInicio.trim(), horaFin.trim(), idDisponibilidad);
    if (solapeCond > 0)
      throw new IllegalStateException("Solape con otra disponibilidad del mismo conductor");

    int solapeVeh = repo.countSolapeVehiculoExcluyendo(
        idVehiculo, diaNorm, horaInicio.trim(), horaFin.trim(), idDisponibilidad);
    if (solapeVeh > 0)
      throw new IllegalStateException("Solape con otra disponibilidad del mismo vehículo");

    // 3) Actualizar
    int updated = repo.actualizarDisponibilidad(
        idDisponibilidad, diaNorm, horaInicio.trim(), horaFin.trim(), tipoNorm);
    if (updated == 0)
      throw new RuntimeException("No fue posible actualizar la disponibilidad");

    return Map.of(
        "idDisponibilidad", idDisponibilidad,
        "dia", diaNorm,
        "horaInicio", horaInicio.trim(),
        "horaFin", horaFin.trim(),
        "tipoServicio", tipoNorm
    );
  }
}
