// src/main/java/uniandes/edu/co/proyecto/repositorio/DisponibilidadRepository.java
package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import uniandes.edu.co.proyecto.modelo.Disponibilidad;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {

  @Query(value = "SELECT COUNT(1) FROM vehiculo WHERE id_vehiculo = :id", nativeQuery = true)
  int countVehiculo(@Param("id") Long idVehiculo);

  @Query(value = "SELECT COUNT(1) FROM usuario_conductor WHERE id_usuario_conductor = :id", nativeQuery = true)
  int countConductor(@Param("id") Long idUsuarioConductor);

  @Query(value = """
      SELECT COUNT(1)
      FROM disponibilidad d
      WHERE d.dia = :dia
        AND (d.id_vehiculo = :idVehiculo OR d.id_usuario_conductor = :idUsuarioConductor)
        AND (TO_TIMESTAMP(:horaInicio,'YYYY-MM-DD HH24:MI:SS') < d.hora_fin)
        AND (TO_TIMESTAMP(:horaFin,   'YYYY-MM-DD HH24:MI:SS') > d.hora_inicio)
      """, nativeQuery = true)
  int contarSolape(@Param("dia") String dia,
                   @Param("idVehiculo") Long idVehiculo,
                   @Param("idUsuarioConductor") Long idUsuarioConductor,
                   @Param("horaInicio") String horaInicio,
                   @Param("horaFin") String horaFin);

  @Modifying
  @Transactional
  @Query(value = """
      INSERT INTO disponibilidad
        (id_disponibilidad, dia, hora_inicio, hora_fin, tipo_servicio, id_vehiculo, id_usuario_conductor)
      VALUES
        (:idDisponibilidad, :dia,
         TO_TIMESTAMP(:horaInicio,'YYYY-MM-DD HH24:MI:SS'),
         TO_TIMESTAMP(:horaFin,   'YYYY-MM-DD HH24:MI:SS'),
         :tipoServicio, :idVehiculo, :idUsuarioConductor)
      """, nativeQuery = true)
  void insertarDisponibilidad(@Param("idDisponibilidad") Long idDisponibilidad,
                              @Param("dia") String dia,
                              @Param("horaInicio") String horaInicio,
                              @Param("horaFin") String horaFin,
                              @Param("tipoServicio") String tipoServicio,
                              @Param("idVehiculo") Long idVehiculo,
                              @Param("idUsuarioConductor") Long idUsuarioConductor);
}
