package uniandes.edu.co.proyecto.repositorio;

import java.util.Map;

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
    SELECT d.id_disponibilidad AS ID_DISPONIBILIDAD,
           d.id_usuario_conductor AS ID_USUARIO_CONDUCTOR,
           d.id_vehiculo AS ID_VEHICULO
    FROM disponibilidad d
    WHERE UPPER(d.dia) = :dia
      AND UPPER(d.tipo_servicio) = :tipo
      AND TO_CHAR(SYSDATE,'HH24:MI:SS')
          BETWEEN TO_CHAR(d.hora_inicio,'HH24:MI:SS') AND TO_CHAR(d.hora_fin,'HH24:MI:SS')
      AND NOT EXISTS (
        SELECT 1 FROM viaje v
        WHERE v.id_usuario_conductor = d.id_usuario_conductor
          AND v.hora_fin IS NULL
      )
    ORDER BY d.hora_fin
    FETCH FIRST 1 ROWS ONLY
    FOR UPDATE SKIP LOCKED
    """, nativeQuery = true)
    Map<String,Object> pickDisponibilidadParaAsignar(@Param("dia") String dia,
                                                 @Param("tipo") String tipo);


}
