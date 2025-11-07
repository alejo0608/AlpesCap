package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import uniandes.edu.co.proyecto.modelo.SolicitudServicio;

public interface SolicitudServicioRepository extends JpaRepository<SolicitudServicio, Long> {

  /* Validaciones de existencia para FK antes de insertar */
  @Query(value = "SELECT COUNT(1) FROM usuario_servicio WHERE id_usuario_servicio = :id", nativeQuery = true)
  int countUsuarioServicio(@Param("id") Long idUsuarioServicio);

  @Query(value = "SELECT COUNT(1) FROM punto_geografico WHERE id_punto = :id", nativeQuery = true)
  int countPuntoGeografico(@Param("id") Long idPunto);

  @Query(value = "SELECT estado FROM solicitud_servicio WHERE id_solicitud = :id", nativeQuery = true)
    String findEstadoById(@Param("id") Long idSolicitud);

  @Query(value = "SELECT id_usuario_servicio FROM solicitud_servicio WHERE id_solicitud = :id", nativeQuery = true)
    Long findUsuarioServicioIdBySolicitud(@Param("id") Long idSolicitud);

  /* Inserción nativa (usa formato de fecha YYYY-MM-DD) */
  @Modifying @Transactional
  @Query(value = """
    INSERT INTO SOLICITUD_SERVICIO
      (ID_SOLICITUD,
       ID_USUARIO_SERVICIO, ID_PUNTO_PARTIDA, ID_PUNTO_LLEGADA,
       TIPO, NIVEL,
       FECHA, FECHA_SOLICITUD,
       ESTADO)
    VALUES
      (:idSolicitud,
       :idUsuarioServicio, :idPuntoPartida, :idPuntoLlegada,
       :tipo, :nivel,
       SYSDATE, SYSDATE,
       'CREADA')
  """, nativeQuery = true)
  int insertarSolicitud(@Param("idSolicitud") Long idSolicitud,
                        @Param("idUsuarioServicio") Long idUsuarioServicio,
                        @Param("idPuntoPartida") Long idPuntoPartida,
                        @Param("idPuntoLlegada") Long idPuntoLlegada,
                        @Param("tipo") String tipo,
                        @Param("nivel") String nivel);
  
}
