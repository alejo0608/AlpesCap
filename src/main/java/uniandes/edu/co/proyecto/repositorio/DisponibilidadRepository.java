// src/main/java/uniandes/edu/co/proyecto/repositorio/DisponibilidadRepository.java
package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import uniandes.edu.co.proyecto.modelo.Disponibilidad;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {

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
        AND (d.hora_inicio < TO_DATE(:horaFin,'YYYY-MM-DD HH24:MI:SS')
         AND d.hora_fin   > TO_DATE(:horaInicio,'YYYY-MM-DD HH24:MI:SS'))
      """, nativeQuery = true)
  int countSolapeConductorExcluyendo(@Param("idConductor") Long idConductor,
                                     @Param("dia") String diaNormalizado,
                                     @Param("horaInicio") String horaInicio,
                                     @Param("horaFin") String horaFin,
                                     @Param("idActual") Long idActual);

  // Solape por VEHICULO (mismo día), excluyendo la disponibilidad actual
  @Query(value = """
      SELECT COUNT(1)
      FROM disponibilidad d
      WHERE d.id_vehiculo = :idVehiculo
        AND UPPER(d.dia) = :dia
        AND d.id_disponibilidad <> :idActual
        AND (d.hora_inicio < TO_DATE(:horaFin,'YYYY-MM-DD HH24:MI:SS')
         AND d.hora_fin   > TO_DATE(:horaInicio,'YYYY-MM-DD HH24:MI:SS'))
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
}
