package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import uniandes.edu.co.proyecto.modelo.Disponibilidad;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {

  // ===== Utilidades RF6 (modificar sin solapes) =====

  @Query(value = "SELECT id_usuario_conductor FROM disponibilidad WHERE id_disponibilidad = :id", nativeQuery = true)
  Long findConductorByDisponibilidad(@Param("id") Long idDisponibilidad);

  @Query(value = "SELECT id_vehiculo FROM disponibilidad WHERE id_disponibilidad = :id", nativeQuery = true)
  Long findVehiculoByDisponibilidad(@Param("id") Long idDisponibilidad);

  // Solape por CONDUCTOR (mismo día), excluyendo la disponibilidad actual
  @Query(value = """
      SELECT COUNT(1)
        FROM disponibilidad d
       WHERE d.id_usuario_conductor = :idConductor
         AND UPPER(d.dia) = :dia
         AND d.id_disponibilidad <> :idActual
         AND ( d.hora_inicio < TO_DATE(:horaFin,'YYYY-MM-DD HH24:MI:SS')
           AND d.hora_fin    > TO_DATE(:horaInicio,'YYYY-MM-DD HH24:MI:SS') )
      """, nativeQuery = true)
  int countSolapeConductorExcluyendo(@Param("idConductor") Long idConductor,
                                     @Param("dia") String diaNormalizado,
                                     @Param("horaInicio") String horaInicio,
                                     @Param("horaFin") String horaFin,
                                     @Param("idActual") Long idActual);

  // Solape por VEHÍCULO (mismo día), excluyendo la disponibilidad actual
  @Query(value = """
      SELECT COUNT(1)
        FROM disponibilidad d
       WHERE d.id_vehiculo = :idVehiculo
         AND UPPER(d.dia) = :dia
         AND d.id_disponibilidad <> :idActual
         AND ( d.hora_inicio < TO_DATE(:horaFin,'YYYY-MM-DD HH24:MI:SS')
           AND d.hora_fin    > TO_DATE(:horaInicio,'YYYY-MM-DD HH24:MI:SS') )
      """, nativeQuery = true)
  int countSolapeVehiculoExcluyendo(@Param("idVehiculo") Long idVehiculo,
                                    @Param("dia") String diaNormalizado,
                                    @Param("horaInicio") String horaInicio,
                                    @Param("horaFin") String horaFin,
                                    @Param("idActual") Long idActual);

  @Modifying
  @Transactional
  @Query(value = """
      UPDATE disponibilidad
         SET dia = :dia,
             hora_inicio = TO_DATE(:horaInicio,'YYYY-MM-DD HH24:MI:SS'),
             hora_fin    = TO_DATE(:horaFin,'YYYY-MM-DD HH24:MI:SS'),
             tipo_servicio = :tipoServicio
       WHERE id_disponibilidad = :id
      """, nativeQuery = true)
  int actualizarDisponibilidad(@Param("id") Long idDisponibilidad,
                               @Param("dia") String dia,
                               @Param("horaInicio") String horaInicio,
                               @Param("horaFin") String horaFin,
                               @Param("tipoServicio") String tipoServicio);

  // ===== Selección de candidato con bloqueo (RF8) =====

  // Proyección liviana para el "pick"
  interface Candidato {
    Long getIdDisponibilidad();
    Long getIdUsuarioConductor();
    Long getIdVehiculo();
  }

  /**
   * Elige 1 disponibilidad candidata en la ciudad del punto de partida, para el día/hora/tipo,
   * excluyendo conductores con viaje abierto. BLOQUEA la fila elegida para evitar doble asignación.
   *
   * @param idCiudad  ciudad del punto de partida
   * @param dia       'LUNES'..'DOMINGO' (UPPER)
   * @param tipo      'PASAJEROS'|'COMIDA'|'MERCANCIAS' (UPPER)
   * @param tsActual  timestamp actual como 'YYYY-MM-DD HH24:MI:SS'
   */
  @Query(value = """
      SELECT x.id_disponibilidad AS idDisponibilidad,
             x.id_usuario_conductor AS idUsuarioConductor,
             x.id_vehiculo AS idVehiculo
        FROM (
              SELECT d.id_disponibilidad,
                     d.id_usuario_conductor,
                     d.id_vehiculo
                FROM disponibilidad d
                JOIN vehiculo v ON v.id_vehiculo = d.id_vehiculo
               WHERE v.id_ciudad_expedicion = :idCiudad
                 AND UPPER(d.dia) = :dia
                 AND TO_DATE(:ts,'YYYY-MM-DD HH24:MI:SS') BETWEEN d.hora_inicio AND d.hora_fin
                 AND UPPER(d.tipo_servicio) = :tipo
                 AND NOT EXISTS (
                       SELECT 1
                         FROM viaje vv
                        WHERE vv.id_usuario_conductor = d.id_usuario_conductor
                          AND vv.hora_fin IS NULL
                 )
               ORDER BY d.id_disponibilidad
             ) x
       WHERE ROWNUM = 1
       FOR UPDATE SKIP LOCKED
      """, nativeQuery = true)
  Candidato pickDisponibilidadParaAsignar(@Param("idCiudad") Long idCiudad,
                                          @Param("dia") String dia,
                                          @Param("tipo") String tipo,
                                          @Param("ts") String tsActual);
}
