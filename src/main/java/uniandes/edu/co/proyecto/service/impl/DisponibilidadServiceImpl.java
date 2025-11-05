package uniandes.edu.co.proyecto.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uniandes.edu.co.proyecto.modelo.Disponibilidad;
import uniandes.edu.co.proyecto.repositorio.DisponibilidadRepository;
import uniandes.edu.co.proyecto.service.DisponibilidadService;

@Service
public class DisponibilidadServiceImpl implements DisponibilidadService {

  private final DisponibilidadRepository repo;
  private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private static final DateTimeFormatter H  = DateTimeFormatter.ofPattern("HH:mm:ss");

  public DisponibilidadServiceImpl(DisponibilidadRepository repo) { this.repo = repo; }

  @Override
  @Transactional
  public Disponibilidad registrar(Long idDisponibilidad,
                                  String dia, String horaInicioHHmmss, String horaFinHHmmss,
                                  String tipoServicio,
                                  Long idVehiculo, Long idConductor) {

    if (idDisponibilidad == null || idDisponibilidad <= 0)
      throw new IllegalArgumentException("idDisponibilidad requerido y positivo");

    // Normalizaciones
    String diaUp  = (dia == null ? "" : dia.trim().toUpperCase(Locale.ROOT));
    String tipoUp = (tipoServicio == null ? "" : tipoServicio.trim().toUpperCase(Locale.ROOT));

    if (!(diaUp.equals("LUNES") || diaUp.equals("MARTES") || diaUp.equals("MIERCOLES") || diaUp.equals("MIÉRCOLES") ||
          diaUp.equals("JUEVES") || diaUp.equals("VIERNES") || diaUp.equals("SABADO") || diaUp.equals("SÁBADO") ||
          diaUp.equals("DOMINGO"))) {
      throw new IllegalArgumentException("día inválido");
    }

    if (!(tipoUp.equals("PASAJEROS") || tipoUp.equals("COMIDA") ||
          tipoUp.equals("MERCANCIAS") || tipoUp.equals("MERCANCÍAS"))) {
      throw new IllegalArgumentException("tipo_servicio inválido (PASAJEROS|COMIDA|MERCANCIAS)");
    }
    // Valida horas HH:mm:ss y arma timestamp con la fecha de hoy
    LocalDate hoy = LocalDate.now();
    LocalDateTime hi, hf;
    try {
      LocalTime li = LocalTime.parse(horaInicioHHmmss, H);
      LocalTime lf = LocalTime.parse(horaFinHHmmss,    H);
      hi = LocalDateTime.of(hoy, li);
      hf = LocalDateTime.of(hoy, lf);
    } catch (Exception e) {
      throw new IllegalArgumentException("horaInicio/horaFin deben tener formato HH:mm:ss");
    }
    if (!hf.isAfter(hi)) throw new IllegalArgumentException("hora_fin debe ser mayor a hora_inicio");
    if (!hf.isAfter(hi)) throw new IllegalArgumentException("hora_fin debe ser mayor a hora_inicio");

    String tsInicio = hi.format(TS);
    String tsFin    = hf.format(TS);

    // Reglas de existencia/pertenencia
    if (repo.existsById(idDisponibilidad))
      throw new IllegalStateException("id_disponibilidad ya existe");
    if (repo.countConductor(idConductor) == 0)
      throw new RuntimeException("Conductor no existe: " + idConductor);
    if (repo.countVehiculo(idVehiculo) == 0)
      throw new RuntimeException("Vehículo no existe: " + idVehiculo);
    if (repo.countVehiculoDeConductor(idVehiculo, idConductor) == 0)
      throw new IllegalStateException("El vehículo no pertenece al conductor");

    // Solapes
    if (repo.countSolapeConductor(idConductor, diaUp, tsInicio, tsFin) > 0)
      throw new IllegalStateException("Solapa con otra disponibilidad del conductor");
    if (repo.countSolapeVehiculo(idVehiculo, diaUp, tsInicio, tsFin) > 0)
      throw new IllegalStateException("Solapa con otra disponibilidad del vehículo");

    // Insert
    repo.insertar(idDisponibilidad, diaUp, tsInicio, tsFin, tipoUp, idVehiculo, idConductor);

    return repo.findById(idDisponibilidad)
      .orElseThrow(() -> new RuntimeException("No fue posible recuperar la disponibilidad insertada"));
  }

  @Override
  public Optional<Disponibilidad> obtener(Long idDisponibilidad) {
    return repo.findById(idDisponibilidad);
  }
}
