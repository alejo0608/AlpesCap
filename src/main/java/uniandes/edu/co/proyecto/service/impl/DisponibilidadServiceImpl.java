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

  // TS = 'YYYY-MM-DD HH:MM:SS' (para TO_DATE en Oracle)
  private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  // H  = 'HH:MM:SS' solamente para entrada del usuario
  private static final DateTimeFormatter H  = DateTimeFormatter.ofPattern("HH:mm:ss");

  public DisponibilidadServiceImpl(DisponibilidadRepository repo) {
    this.repo = repo;
  }

  // ========= Helpers (DRY) =========

  private static String up(String s) {
    return s == null ? "" : s.trim().toUpperCase(Locale.ROOT);
  }

  private static void assertDiaValido(String diaUp) {
    if (!(diaUp.equals("LUNES") || diaUp.equals("MARTES") ||
          diaUp.equals("MIERCOLES") || diaUp.equals("MIÉRCOLES") ||
          diaUp.equals("JUEVES") || diaUp.equals("VIERNES") ||
          diaUp.equals("SABADO") || diaUp.equals("SÁBADO") ||
          diaUp.equals("DOMINGO"))) {
      throw new IllegalArgumentException("día inválido");
    }
  }

  private static void assertTipoValido(String tipoUp) {
    if (!(tipoUp.equals("PASAJEROS") || tipoUp.equals("COMIDA") ||
          tipoUp.equals("MERCANCIAS") || tipoUp.equals("MERCANCÍAS"))) {
      throw new IllegalArgumentException("tipo_servicio inválido (PASAJEROS|COMIDA|MERCANCIAS)");
    }
  }

  /** Parsea HH:mm:ss y devuelve {hi, hf} con la fecha de hoy */
  private static LocalDateTime[] parseHorasDeHoy(String hIni, String hFin) {
    try {
      LocalDate hoy = LocalDate.now();
      LocalTime li = LocalTime.parse(hIni, H);
      LocalTime lf = LocalTime.parse(hFin, H);
      LocalDateTime hi = LocalDateTime.of(hoy, li);
      LocalDateTime hf = LocalDateTime.of(hoy, lf);
      if (!hf.isAfter(hi)) throw new IllegalArgumentException("hora_fin debe ser mayor a hora_inicio");
      return new LocalDateTime[]{ hi, hf };
    } catch (Exception e) {
      throw new IllegalArgumentException("horaInicio/horaFin deben tener formato HH:mm:ss");
    }
  }

  private void assertFks(Long idConductor, Long idVehiculo) {
    if (repo.countConductor(idConductor) == 0) {
      throw new RuntimeException("Conductor no existe: " + idConductor);
    }
    if (repo.countVehiculo(idVehiculo) == 0) {
      throw new RuntimeException("Vehículo no existe: " + idVehiculo);
    }
  }

  private void assertVehiculoDeConductor(Long idVehiculo, Long idConductor) {
    if (repo.countVehiculoDeConductor(idVehiculo, idConductor) == 0) {
      throw new IllegalStateException("El vehículo no pertenece al conductor");
    }
  }

  private void assertNoSolapeCrear(Long idConductor, Long idVehiculo, String diaUp,
                                   String tsInicio, String tsFin) {
    if (repo.countSolapeConductor(idConductor, diaUp, tsInicio, tsFin) > 0) {
      throw new IllegalStateException("Solapa con otra disponibilidad del conductor");
    }
    if (repo.countSolapeVehiculo(idVehiculo, diaUp, tsInicio, tsFin) > 0) {
      throw new IllegalStateException("Solapa con otra disponibilidad del vehículo");
    }
  }

  private void assertNoSolapeModificar(Long idDisponibilidad, Long idConductor, Long idVehiculo,
                                       String diaUp, String tsInicio, String tsFin) {
    if (repo.countSolapeConductorExcluyendo(idConductor, diaUp, tsInicio, tsFin, idDisponibilidad) > 0) {
      throw new IllegalStateException("Solapa con otra disponibilidad del conductor");
    }
    if (repo.countSolapeVehiculoExcluyendo(idVehiculo, diaUp, tsInicio, tsFin, idDisponibilidad) > 0) {
      throw new IllegalStateException("Solapa con otra disponibilidad del vehículo");
    }
  }

  // ========= RF5: Registrar disponibilidad =========
  @Override
  @Transactional
  public Disponibilidad registrar(Long idDisponibilidad,
                                  String dia, String horaInicioHHmmss, String horaFinHHmmss,
                                  String tipoServicio, Long idVehiculo, Long idConductor) {

    if (idDisponibilidad == null || idDisponibilidad <= 0) {
      throw new IllegalArgumentException("idDisponibilidad requerido y positivo");
    }
    if (repo.existsById(idDisponibilidad)) {
      throw new IllegalStateException("id_disponibilidad ya existe");
    }

    String diaUp  = up(dia);
    String tipoUp = up(tipoServicio);
    assertDiaValido(diaUp);
    assertTipoValido(tipoUp);

    LocalDateTime[] par = parseHorasDeHoy(horaInicioHHmmss, horaFinHHmmss);
    String tsInicio = par[0].format(TS);
    String tsFin    = par[1].format(TS);

    assertFks(idConductor, idVehiculo);
    assertVehiculoDeConductor(idVehiculo, idConductor);
    assertNoSolapeCrear(idConductor, idVehiculo, diaUp, tsInicio, tsFin);

    repo.insertar(idDisponibilidad, diaUp, tsInicio, tsFin, tipoUp, idVehiculo, idConductor);

    return repo.findById(idDisponibilidad)
        .orElseThrow(() -> new RuntimeException("No fue posible recuperar la disponibilidad insertada"));
  }

  @Override
  public Optional<Disponibilidad> obtener(Long idDisponibilidad) {
    return repo.findById(idDisponibilidad);
  }

  // ========= RF6: Modificar disponibilidad =========
  @Override
  @Transactional
  public Disponibilidad modificar(Long idDisponibilidad,
                                  String diaOpt, String horaInicioHHmmss, String horaFinHHmmss,
                                  String tipoServicioOpt) {

    var actual = repo.findById(idDisponibilidad)
        .orElseThrow(() -> new RuntimeException("Disponibilidad no existe: " + idDisponibilidad));

    Long idConductor = repo.findConductorByDisponibilidad(idDisponibilidad);
    Long idVehiculo  = repo.findVehiculoByDisponibilidad(idDisponibilidad);
    if (idConductor == null || idVehiculo == null) {
      throw new RuntimeException("FK de disponibilidad inválidas");
    }

    String diaNuevo  = (diaOpt == null || diaOpt.isBlank()) ? actual.getDia() : up(diaOpt);
    String tipoNuevo = (tipoServicioOpt == null || tipoServicioOpt.isBlank()) ? actual.getTipoServicio() : up(tipoServicioOpt);

    assertDiaValido(diaNuevo);
    assertTipoValido(tipoNuevo);

    LocalDateTime[] par = parseHorasDeHoy(horaInicioHHmmss, horaFinHHmmss);
    String tsInicio = par[0].format(TS);
    String tsFin    = par[1].format(TS);

    assertNoSolapeModificar(idDisponibilidad, idConductor, idVehiculo, diaNuevo, tsInicio, tsFin);

    int rows = repo.actualizarDisponibilidad(idDisponibilidad, diaNuevo, tsInicio, tsFin, tipoNuevo);
    if (rows == 0) {
      throw new RuntimeException("No se pudo actualizar disponibilidad");
    }

    return repo.findById(idDisponibilidad)
        .orElseThrow(() -> new RuntimeException("No fue posible recuperar la disponibilidad actualizada"));
  }
}
