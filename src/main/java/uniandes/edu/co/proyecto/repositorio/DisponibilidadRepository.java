package uniandes.edu.co.proyecto.repositorio;

import java.util.Map;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import uniandes.edu.co.proyecto.modelo.Disponibilidad;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {

  // ===== Existencia / pertenencia =====
  @Query(value = "SELECT COUNT(1) FROM USUARIO_CONDUCTOR WHERE ID_USUARIO_CONDUCTOR = :id", nativeQuery = true)
  int countConductor(@Param("id") Long idConductor);

  @Query(value = "SELECT COUNT(1) FROM VEHICULO WHERE ID_VEHICULO = :id", nativeQuery = true)
  int countVehiculo(@Param("id") Long idVehiculo);

  @Query(value = """
      SELECT COUNT(1) 
      FROM VEHICULO 
      WHERE ID_VEHICULO = :idVehiculo 
        AND ID_USUARIO_CONDUCTOR = :idConductor
      """, nativeQuery = true)
  int countVehiculoDeConductor(@Param("idVehiculo") Long idVehiculo,
                               @Param("idConductor") Long idConductor);

  // ===== Solapes (crear) =====
  @Query(value = """
      SELECT COUNT(1)
        FROM disponibilidad d
       WHERE d.id_usuario_conductor = :idConductor
         AND UPPER(d.dia) = :dia
         AND ( d.hora_inicio < TO_DATE(:horaFin,'YYYY-MM-DD HH24:MI:SS')
           AND d.hora_fin    > TO_DATE(:horaInicio,'YYYY-MM-DD HH24:MI:SS') )
      """, nativeQuery = true)
  int countSolapeConductor(@Param("idConductor") Long idConductor,
                           @Param("dia") String diaNormalizado,
                           @Param("horaInicio") String horaInicio,
                           @Param("horaFin") String horaFin);

  @Query(value = """
      SELECT COUNT(1)
        FROM disponibilidad d
       WHERE d.id_vehiculo = :idVehiculo
         AND UPPER(d.dia) = :dia
         AND ( d.hora_inicio < TO_DATE(:horaFin,'YYYY-MM-DD HH24:MI:SS')
           AND d.hora_fin    > TO_DATE(:horaInicio,'YYYY-MM-DD HH24:MI:SS') )
      """, nativeQuery = true)
  int countSolapeVehiculo(@Param("idVehiculo") Long idVehiculo,
                          @Param("dia") String diaNormalizado,
                          @Param("horaInicio") String horaInicio,
                          @Param("horaFin") String horaFin);

  // ===== Insert (crear) =====
  @Modifying
  @Transactional
  @Query(value = """
      INSERT INTO disponibilidad
        (id_disponibilidad, dia, hora_inicio, hora_fin, tipo_servicio, id_vehiculo, id_usuario_conductor)
      VALUES
        (:id, :dia, TO_DATE(:horaInicio,'YYYY-MM-DD HH24:MI:SS'),
             TO_DATE(:horaFin,'YYYY-MM-DD HH24:MI:SS'), :tipoServicio, :idVehiculo, :idConductor)
      """, nativeQuery = true)
  void insertar(@Param("id") Long idDisponibilidad,
                @Param("dia") String dia,
                @Param("horaInicio") String horaInicio,
                @Param("horaFin") String horaFin,
                @Param("tipoServicio") String tipoServicio,
                @Param("idVehiculo") Long idVehiculo,
                @Param("idConductor") Long idConductor);

  // ===== Utilidades RF6 (modificar sin solapes) =====
  @Query(value = "SELECT id_usuario_conductor FROM disponibilidad WHERE id_disponibilidad = :id", nativeQuery = true)
  Long findConductorByDisponibilidad(@Param("id") Long idDisponibilidad);

  @Query(value = "SELECT id_vehiculo FROM disponibilidad WHERE id_disponibilidad = :id", nativeQuery = true)
  Long findVehiculoByDisponibilidad(@Param("id") Long idDisponibilidad);

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

  // ===== Update (modificar) =====
@Modifying(clearAutomatically = true, flushAutomatically = true)
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

  // ===== Selección candidata (RF8) =====
  interface Candidato {
    Long getIdDisponibilidad();
    Long getIdUsuarioConductor();
    Long getIdVehiculo();
  }

  @Query(value = """
SELECT d.id_disponibilidad   AS ID_DISPONIBILIDAD,
       d.id_usuario_conductor AS ID_USUARIO_CONDUCTOR,
       d.id_vehiculo          AS ID_VEHICULO
FROM disponibilidad d
WHERE d.id_disponibilidad = (
  SELECT id_disponibilidad
  FROM (
    SELECT d2.id_disponibilidad
    FROM disponibilidad d2
    WHERE UPPER(d2.dia) = :dia
      AND UPPER(d2.tipo_servicio) = :tipo
      AND TO_CHAR(SYSDATE,'HH24:MI:SS')
          BETWEEN TO_CHAR(d2.hora_inicio,'HH24:MI:SS')
              AND TO_CHAR(d2.hora_fin,'HH24:MI:SS')
      AND NOT EXISTS (
        SELECT 1 FROM viaje v
        WHERE v.id_usuario_conductor = d2.id_usuario_conductor
          AND v.hora_fin IS NULL
      )
    ORDER BY d2.hora_fin
  )
  WHERE ROWNUM = 1
)
FOR UPDATE SKIP LOCKED
""", nativeQuery = true)
Map<String,Object> pickDisponibilidadParaAsignar(@Param("dia") String dia,
                                                 @Param("tipo") String tipo);
}
