package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import uniandes.edu.co.proyecto.modelo.Viaje;

public interface ViajeRepository extends JpaRepository<Viaje, Long> {

  /* ===== Validaciones de existencia (FKs) ===== */
  @Query(value = "SELECT COUNT(1) FROM solicitud_servicio WHERE id_solicitud = :id", nativeQuery = true)
  int countSolicitud(@Param("id") Long idSolicitud);

  @Query(value = "SELECT COUNT(1) FROM usuario_conductor WHERE id_usuario_conductor = :id", nativeQuery = true)
  int countConductor(@Param("id") Long idConductor);

  @Query(value = "SELECT COUNT(1) FROM vehiculo WHERE id_vehiculo = :id", nativeQuery = true)
  int countVehiculo(@Param("id") Long idVehiculo);

  @Query(value = "SELECT COUNT(1) FROM punto_geografico WHERE id_punto = :id", nativeQuery = true)
  int countPunto(@Param("id") Long idPunto);

  /* ===== Insertar viaje =====
     Nota: horaInicio/horaFin pueden venir nulos; TO_TIMESTAMP(NULL, ...) devuelve NULL en Oracle.
  */
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
                     @Param("fechaAsignacion") String fechaAsignacion,   // "YYYY-MM-DD"
                     @Param("horaInicio") String horaInicio,             // "YYYY-MM-DD HH:MM:SS" o null
                     @Param("horaFin") String horaFin,                   // idem
                     @Param("distanciaKm") Double distanciaKm,           // puede ser null
                     @Param("costoTotal") Double costoTotal,             // puede ser null
                     @Param("idUsuarioConductor") Long idUsuarioConductor,
                     @Param("idVehiculo") Long idVehiculo,
                     @Param("idPuntoPartida") Long idPuntoPartida,
                     @Param("idSolicitud") Long idSolicitud);            // puede ser null

  /* ===== Finalizar/actualizar viaje (setear métricas) ===== */
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
                      @Param("horaInicio") String horaInicio,     // puede ser null
                      @Param("horaFin") String horaFin,           // puede ser null
                      @Param("distanciaKm") Double distanciaKm,
                      @Param("costoTotal") Double costoTotal);
}
