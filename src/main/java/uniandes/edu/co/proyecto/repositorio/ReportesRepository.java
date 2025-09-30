package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import uniandes.edu.co.proyecto.modelo.Viaje;

/**
 * Repositorio de reportes (usamos Viaje como entidad base para habilitar JPA).
 */
public interface ReportesRepository extends JpaRepository<Viaje, Long> {

  /* ======================== RFC1: Histórico por usuario servicio ======================== */
  public static interface Rfc1HistoricoRow {
    Long   getIdSolicitud();
    String getTipo();
    String getNivel();
    java.sql.Date getFecha();
    String getEstado();
    java.sql.Date getFechaAsignacion();
    java.sql.Timestamp getHoraInicio();
    java.sql.Timestamp getHoraFin();
    Double getDistanciaKm();
    Double getCostoTotal();
  }

  @Query(value = """
      SELECT
        s.id_solicitud      AS idSolicitud,
        s.tipo              AS tipo,
        s.nivel             AS nivel,
        s.fecha             AS fecha,
        s.estado            AS estado,
        v.fecha_asignacion  AS fechaAsignacion,
        v.hora_inicio       AS horaInicio,
        v.hora_fin          AS horaFin,
        v.distancia_km      AS distanciaKm,
        v.costo_total       AS costoTotal
      FROM solicitud_servicio s
      JOIN usuario_servicio u
        ON u.id_usuario_servicio = s.id_usuario_servicio
      LEFT JOIN viaje v
        ON v.id_solicitud = s.id_solicitud
      WHERE u.id_usuario_servicio = :idUsuario
        AND (:fini IS NULL OR s.fecha >= TO_DATE(:fini,'YYYY-MM-DD'))
        AND (:ffin IS NULL OR s.fecha <= TO_DATE(:ffin,'YYYY-MM-DD'))
      ORDER BY s.fecha DESC
      """, nativeQuery = true)
  List<Rfc1HistoricoRow> rfc1HistoricoUsuario(@Param("idUsuario") Long idUsuario,
                                              @Param("fini") String fini,   // "YYYY-MM-DD" o null
                                              @Param("ffin") String ffin);  // "YYYY-MM-DD" o null

  /* ======================== RFC2: Top 20 conductores por # servicios ======================== */
  public static interface Rfc2TopConductoresRow {
    Long   getIdConductor();
    String getNombre();
    Long   getTotalServicios();
  }

  @Query(value = """
      SELECT
        c.id_usuario_conductor AS idConductor,
        c.nombre               AS nombre,
        COUNT(*)               AS totalServicios
      FROM viaje v
      JOIN usuario_conductor c
        ON c.id_usuario_conductor = v.id_usuario_conductor
      WHERE (:fini IS NULL OR v.hora_inicio >= TO_TIMESTAMP(:fini || ' 00:00:00','YYYY-MM-DD HH24:MI:SS'))
        AND (:ffin IS NULL OR v.hora_inicio <  TO_TIMESTAMP(:ffin || ' 23:59:59','YYYY-MM-DD HH24:MI:SS'))
      GROUP BY c.id_usuario_conductor, c.nombre
      ORDER BY totalServicios DESC, c.nombre ASC
      FETCH FIRST 20 ROWS ONLY
      """, nativeQuery = true)
  List<Rfc2TopConductoresRow> rfc2TopConductores(@Param("fini") String fini,   // "YYYY-MM-DD" o null
                                                 @Param("ffin") String ffin);  // "YYYY-MM-DD" o null

  /* ======================== RFC3: Dinero ganado por vehículo y tipo ======================== */
  public static interface Rfc3GananciasRow {
    Long   getIdVehiculo();
    String getPlaca();
    String getTipoServicio();
    Double getGanadoConductor();
  }

  @Query(value = """
      SELECT
        vh.id_vehiculo                         AS idVehiculo,
        vh.placa                               AS placa,
        s.tipo                                 AS tipoServicio,
        SUM(v.costo_total * c.comision)        AS ganadoConductor
      FROM viaje v
      JOIN usuario_conductor c
        ON c.id_usuario_conductor = v.id_usuario_conductor
      JOIN vehiculo vh
        ON vh.id_vehiculo = v.id_vehiculo
      JOIN solicitud_servicio s
        ON s.id_solicitud = v.id_solicitud
      WHERE c.id_usuario_conductor = :idConductor
        AND (:fini IS NULL OR v.hora_inicio >= TO_TIMESTAMP(:fini || ' 00:00:00','YYYY-MM-DD HH24:MI:SS'))
        AND (:ffin IS NULL OR v.hora_inicio <  TO_TIMESTAMP(:ffin || ' 23:59:59','YYYY-MM-DD HH24:MI:SS'))
      GROUP BY vh.id_vehiculo, vh.placa, s.tipo
      ORDER BY vh.placa, s.tipo
      """, nativeQuery = true)
  List<Rfc3GananciasRow> rfc3GananciasPorVehiculoYTipo(@Param("idConductor") Long idConductor,
                                                       @Param("fini") String fini,   // "YYYY-MM-DD" o null
                                                       @Param("ffin") String ffin);  // "YYYY-MM-DD" o null

  /* ======================== RFC4: Utilización por ciudad y rango ======================== */
  public static interface Rfc4UtilizacionRow {
    String getTipo();
    String getNivel();
    Long   getTotalServicios();
    Double getPorcentaje();
  }

  @Query(value = """
      SELECT
        s.tipo                                  AS tipo,
        s.nivel                                 AS nivel,
        COUNT(*)                                AS totalServicios,
        ROUND(100*COUNT(*)/NULLIF(SUM(COUNT(*)) OVER (),0), 2) AS porcentaje
      FROM solicitud_servicio s
      JOIN punto_geografico pg
        ON pg.id_punto = s.id_punto_partida
      JOIN ciudad ci
        ON ci.id_ciudad = pg.id_ciudad
      WHERE ci.id_ciudad = :idCiudad
        AND s.fecha BETWEEN TO_DATE(:fini,'YYYY-MM-DD') AND TO_DATE(:ffin,'YYYY-MM-DD')
      GROUP BY s.tipo, s.nivel
      ORDER BY totalServicios DESC
      """, nativeQuery = true)
  List<Rfc4UtilizacionRow> rfc4Utilizacion(@Param("idCiudad") Long idCiudad,
                                           @Param("fini") String fini,   // "YYYY-MM-DD"
                                           @Param("ffin") String ffin);  // "YYYY-MM-DD"
}

