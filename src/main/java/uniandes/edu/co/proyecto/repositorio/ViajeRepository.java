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

  @Query(value = """
      SELECT COUNT(1)
      FROM VIAJE
      WHERE ID_USUARIO_CONDUCTOR = :idConductor
        AND HORA_FIN IS NULL
      """, nativeQuery = true)
  int countViajesAbiertosDeConductor(@Param("idConductor") Long idConductor);

  @Modifying
  @Transactional
  @Query(value = """
      INSERT INTO VIAJE
        (ID_VIAJE, FECHA_ASIGNACION, HORA_INICIO, DISTANCIA_KM, COSTO_TOTAL,
         ID_USUARIO_CONDUCTOR, ID_VEHICULO, ID_PUNTO_PARTIDA, ID_SOLICITUD)
      VALUES
        (:idViaje,
         TO_DATE(:fechaAsignacion,'YYYY-MM-DD'),
         TO_DATE(:horaInicio,'YYYY-MM-DD HH24:MI:SS'),
         :distanciaKm, :costoTotal,
         :idConductor, :idVehiculo, :idPuntoPartida, :idSolicitud)
      """, nativeQuery = true)
  void insertarViajeInicio(@Param("idViaje") Long idViaje,
      @Param("fechaAsignacion") String fechaAsignacion,
      @Param("horaInicio") String horaInicio,
      @Param("distanciaKm") Double distanciaKm,
      @Param("costoTotal") Double costoTotal,
      @Param("idConductor") Long idConductor,
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

  //
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Transactional
  @Query(value = """
        INSERT INTO VIAJE
          (ID_VIAJE, ID_USUARIO_SERVICIO, ID_USUARIO_CONDUCTOR, ID_VEHICULO,
           ID_PUNTO_PARTIDA, ID_PUNTO_LLEGADA, HORA_INICIO, HORA_FIN,
           DISTANCIA_KM, COSTO_TOTAL, TIPO_SERVICIO, NIVEL)
        VALUES
          (:idViaje, :idUsrServ, :idCond, :idVehiculo,
           :idPartida, :idLlegada, SYSDATE, NULL,
           :distKm, :costo, :tipo, :nivel)
      """, nativeQuery = true)
  void insertarViaje(@Param("idViaje") Long idViaje,
      @Param("idUsrServ") Long idUsuarioServicio,
      @Param("idCond") Long idUsuarioConductor,
      @Param("idVehiculo") Long idVehiculo,
      @Param("idPartida") Long idPuntoPartida,
      @Param("idLlegada") Long idPuntoLlegada,
      @Param("distKm") Double distanciaKm,
      @Param("costo") Double costoTotal,
      @Param("tipo") String tipoServicio,
      @Param("nivel") String nivel);
}
