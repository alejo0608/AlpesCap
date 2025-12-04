package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

import uniandes.edu.co.proyecto.modelo.Viaje;

/**
 * Repositorio de reportes (usamos Viaje como entidad base para habilitar JPA).
 */
public interface ReportesRepository extends JpaRepository<Viaje, Long> {

  /*
   * ======================== RFC1: Histórico por usuario servicio
   * ========================
   */
  @Query(value = """
      SELECT
        s.ID_SOLICITUD,
        s.TIPO,
        s.NIVEL,
        s.ESTADO                 AS ESTADO_SOLICITUD,
        s.FECHA                  AS FECHA_CREACION,
        s.FECHA_SOLICITUD,
        v.ID_VIAJE,
        v.FECHA_ASIGNACION,
        v.HORA_INICIO,
        v.HORA_FIN,
        v.DISTANCIA_KM,
        v.COSTO_TOTAL,
        v.ID_USUARIO_CONDUCTOR,
        v.ID_VEHICULO,
        p.ID_PAGO,
        p.METODO                 AS METODO_PAGO,
        p.ESTADO                 AS ESTADO_PAGO,
        p.MONTO                  AS MONTO_PAGO,
        pp.ID_PUNTO              AS ID_PUNTO_PARTIDA,
        NVL(pp.NOMBRE, pp.DIRECCION) AS NOMBRE_PARTIDA,
        pp.DIRECCION             AS DIRECCION_PARTIDA,
        cp.ID_CIUDAD             AS ID_CIUDAD_PARTIDA,
        cp.NOMBRE                AS CIUDAD_PARTIDA,
        pl.ID_PUNTO              AS ID_PUNTO_LLEGADA,
        NVL(pl.NOMBRE, pl.DIRECCION) AS NOMBRE_LLEGADA,
        pl.DIRECCION             AS DIRECCION_LLEGADA,
        cl.ID_CIUDAD             AS ID_CIUDAD_LLEGADA,
        cl.NOMBRE                AS CIUDAD_LLEGADA,
        CASE WHEN v.HORA_FIN IS NULL THEN 'EN CURSO' ELSE 'FINALIZADO' END AS ESTADO_VIAJE,
        CASE WHEN v.HORA_FIN IS NOT NULL
             THEN ROUND( (v.HORA_FIN - v.HORA_INICIO) * 24 * 60 )
        END                      AS DURACION_MIN
      FROM SOLICITUD_SERVICIO s
      LEFT JOIN VIAJE v  ON v.ID_SOLICITUD = s.ID_SOLICITUD
      LEFT JOIN PAGO p   ON p.ID_VIAJE = v.ID_VIAJE
      LEFT JOIN PUNTO_GEOGRAFICO pp ON pp.ID_PUNTO = s.ID_PUNTO_PARTIDA
      LEFT JOIN CIUDAD cp ON cp.ID_CIUDAD = pp.ID_CIUDAD
      LEFT JOIN PUNTO_GEOGRAFICO pl ON pl.ID_PUNTO = s.ID_PUNTO_LLEGADA
      LEFT JOIN CIUDAD cl ON cl.ID_CIUDAD = pl.ID_CIUDAD
      WHERE s.ID_USUARIO_SERVICIO = :idUsuarioServicio
      ORDER BY NVL(v.FECHA_ASIGNACION, s.FECHA) DESC, s.ID_SOLICITUD DESC
      """, nativeQuery = true)
  List<Map<String,Object>> historicoServiciosUsuario(@Param("idUsuarioServicio") Long idUsuarioServicio);

  // >>> NUEVO para la versión transaccional (proyección INTERFAZ tipada) <<<
  public interface HistoricoServicioRow {
    Long getIdViaje();
    java.sql.Timestamp getFechaAsignacion();
    java.sql.Timestamp getHoraInicio();
    java.sql.Timestamp getHoraFin();
    Double getDistanciaKm();
    Double getCostoTotal();
    String getTipo();
    String getNivel();
    Long getIdUsuarioServicio();
    Long getIdPuntoPartida();
    Long getIdPuntoLlegada();
    String getMetodoPago();
    Double getMonto();
    String getEstadoPago();
  }

  @Query(value = """
      SELECT
        v.ID_VIAJE              AS idViaje,
        v.FECHA_ASIGNACION      AS fechaAsignacion,
        v.HORA_INICIO           AS horaInicio,
        v.HORA_FIN              AS horaFin,
        v.DISTANCIA_KM          AS distanciaKm,
        v.COSTO_TOTAL           AS costoTotal,
        s.TIPO                  AS tipo,
        s.NIVEL                 AS nivel,
        s.ID_USUARIO_SERVICIO   AS idUsuarioServicio,
        s.ID_PUNTO_PARTIDA      AS idPuntoPartida,
        s.ID_PUNTO_LLEGADA      AS idPuntoLlegada,
        p.METODO                AS metodoPago,
        p.MONTO                 AS monto,
        p.ESTADO                AS estadoPago
      FROM VIAJE v
      JOIN SOLICITUD_SERVICIO s ON s.ID_SOLICITUD = v.ID_SOLICITUD
      LEFT JOIN PAGO p          ON p.ID_VIAJE     = v.ID_VIAJE
      WHERE s.ID_USUARIO_SERVICIO = :idUsuario
      ORDER BY v.FECHA_ASIGNACION DESC, v.ID_VIAJE DESC
      """, nativeQuery = true)
  List<HistoricoServicioRow> historicoServiciosUsuarioRows(@Param("idUsuario") Long idUsuario);


  
  // ===== RFC2: TOP 20 conductores por cantidad de servicios =====
  @Query(value = """
      SELECT
        uc.ID_USUARIO_CONDUCTOR AS idConductor,
        uc.NOMBRE               AS nombre,
        uc.CORREO               AS correo,
        COUNT(1)                AS totalServicios
      FROM VIAJE v
      JOIN USUARIO_CONDUCTOR uc ON uc.ID_USUARIO_CONDUCTOR = v.ID_USUARIO_CONDUCTOR
      WHERE v.HORA_INICIO IS NOT NULL
      GROUP BY uc.ID_USUARIO_CONDUCTOR, uc.NOMBRE, uc.CORREO
      ORDER BY totalServicios DESC
      FETCH FIRST 20 ROWS ONLY
      """, nativeQuery = true)
  List<Map<String,Object>> top20Conductores();

  // ===== RFC3: Ingresos por vehículo (por tipo) para 1 conductor, con comisión =====
  @Query(value = """
      SELECT
        v.ID_USUARIO_CONDUCTOR               AS idConductor,
        v.ID_VEHICULO                        AS idVehiculo,
        ve.PLACA                             AS placa,
        s.TIPO                               AS tipo,
        SUM(p.MONTO)                         AS bruto,
        ROUND(SUM(p.MONTO) * (1 - :pct), 2)  AS neto,
        SUM(SUM(p.MONTO)) OVER (PARTITION BY v.ID_VEHICULO)                       AS brutoVehiculo,
        ROUND(SUM(SUM(p.MONTO)) OVER (PARTITION BY v.ID_VEHICULO) * (1 - :pct),2) AS netoVehiculo
      FROM VIAJE v
      JOIN SOLICITUD_SERVICIO s ON s.ID_SOLICITUD = v.ID_SOLICITUD
      JOIN PAGO p               ON p.ID_VIAJE     = v.ID_VIAJE
      JOIN VEHICULO ve          ON ve.ID_VEHICULO = v.ID_VEHICULO
      WHERE v.ID_USUARIO_CONDUCTOR = :idConductor
        AND UPPER(p.ESTADO) = 'COMPLETADO'
      GROUP BY v.ID_USUARIO_CONDUCTOR, v.ID_VEHICULO, ve.PLACA, s.TIPO
      ORDER BY v.ID_VEHICULO, s.TIPO
      """, nativeQuery = true)
  List<Map<String,Object>> ingresosPorVehiculoYTipo(@Param("idConductor") Long idConductor,
                                                    @Param("pct") double pctComision);

  // ===== RFC4: Utilización por ciudad en rango de fechas (por tipo y nivel) =====
  @Query(value = """
      SELECT
        s.TIPO       AS tipo,
        s.NIVEL      AS nivel,
        COUNT(*)     AS total,
        ROUND(RATIO_TO_REPORT(COUNT(*)) OVER() * 100, 2) AS porcentaje
      FROM VIAJE v
      JOIN SOLICITUD_SERVICIO s ON s.ID_SOLICITUD = v.ID_SOLICITUD
      JOIN PUNTO_GEOGRAFICO pp  ON pp.ID_PUNTO    = v.ID_PUNTO_PARTIDA
      WHERE pp.ID_CIUDAD = :idCiudad
        AND NVL(v.HORA_INICIO, v.FECHA_ASIGNACION) >= TO_DATE(:desde,'YYYY-MM-DD')
        AND NVL(v.HORA_INICIO, v.FECHA_ASIGNACION) <  TO_DATE(:hasta,'YYYY-MM-DD') + 1
      GROUP BY s.TIPO, s.NIVEL
      ORDER BY total DESC
      """, nativeQuery = true)
  List<Map<String,Object>> utilizacionServiciosCiudad(@Param("idCiudad") Long idCiudad,
                                                      @Param("desde") String desdeYmd,
                                                      @Param("hasta") String hastaYmd);
}