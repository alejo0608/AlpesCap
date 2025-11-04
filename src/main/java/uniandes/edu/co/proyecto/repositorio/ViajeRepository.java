package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import uniandes.edu.co.proyecto.modelo.Viaje;

public interface ViajeRepository extends JpaRepository<Viaje, Long> {

  @Query(value = "SELECT COUNT(1) FROM solicitud_servicio WHERE id_solicitud = :id", nativeQuery = true)
  int countSolicitud(@Param("id") Long idSolicitud);

  @Query(value = "SELECT COUNT(1) FROM usuario_conductor WHERE id_usuario_conductor = :id", nativeQuery = true)
  int countConductor(@Param("id") Long idConductor);

  @Query(value = "SELECT COUNT(1) FROM vehiculo WHERE id_vehiculo = :id", nativeQuery = true)
  int countVehiculo(@Param("id") Long idVehiculo);

  @Query(value = "SELECT COUNT(1) FROM punto_geografico WHERE id_punto = :id", nativeQuery = true)
  int countPunto(@Param("id") Long idPunto);

  @Query(value = "SELECT id_solicitud FROM viaje WHERE id_viaje = :id", nativeQuery = true)
    Long findSolicitudIdByViaje(@Param("id") Long idViaje);

  @Query(value = "SELECT id_usuario_conductor FROM viaje WHERE id_viaje = :id", nativeQuery = true)
    Long findConductorIdByViaje(@Param("id") Long idViaje);

  // NUEVO: verifica si la solicitud ya tiene viaje
  @Query(value = "SELECT COUNT(1) FROM viaje WHERE id_solicitud = :id", nativeQuery = true)
  int countViajePorSolicitud(@Param("id") Long idSolicitud);

  @Modifying
  @Transactional
  @Query(value = """
      INSERT INTO viaje
        (id_viaje, fecha_asignacion, hora_inicio, hora_fin, distancia_km, costo_total,
         id_usuario_conductor, id_vehiculo, id_punto_partida, id_solicitud)
      VALUES
        (:idViaje,
         TO_DATE(:fechaAsignacion,'YYYY-MM-DD'),
         CASE WHEN :horaInicio IS NULL THEN NULL ELSE TO_TIMESTAMP(:horaInicio,'YYYY-MM-DD HH24:MI:SS') END,
         CASE WHEN :horaFin    IS NULL THEN NULL ELSE TO_TIMESTAMP(:horaFin,   'YYYY-MM-DD HH24:MI:SS') END,
         :distanciaKm, :costoTotal,
         :idUsuarioConductor, :idVehiculo, :idPuntoPartida, :idSolicitud)
      """, nativeQuery = true)
  void insertarViaje(@Param("idViaje") Long idViaje,
                     @Param("fechaAsignacion") String fechaAsignacion,
                     @Param("horaInicio") String horaInicio,
                     @Param("horaFin") String horaFin,
                     @Param("distanciaKm") Double distanciaKm,
                     @Param("costoTotal") Double costoTotal,
                     @Param("idUsuarioConductor") Long idUsuarioConductor,
                     @Param("idVehiculo") Long idVehiculo,
                     @Param("idPuntoPartida") Long idPuntoPartida,
                     @Param("idSolicitud") Long idSolicitud);

  @Modifying
  @Transactional
  @Query(value = """
      UPDATE viaje
         SET hora_inicio = CASE WHEN :horaInicio IS NULL THEN hora_inicio
                                ELSE TO_TIMESTAMP(:horaInicio,'YYYY-MM-DD HH24:MI:SS') END,
             hora_fin    = CASE WHEN :horaFin IS NULL THEN hora_fin
                                ELSE TO_TIMESTAMP(:horaFin,'YYYY-MM-DD HH24:MI:SS') END,
             distancia_km = :distanciaKm,
             costo_total  = :costoTotal
       WHERE id_viaje = :idViaje
      """, nativeQuery = true)
  int actualizarFinal(@Param("idViaje") Long idViaje,
                      @Param("horaInicio") String horaInicio,
                      @Param("horaFin") String horaFin,
                      @Param("distanciaKm") Double distanciaKm,
                      @Param("costoTotal") Double costoTotal);
}
