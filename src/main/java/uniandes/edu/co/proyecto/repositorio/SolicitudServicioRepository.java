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

  /* Inserción nativa (usa formato de fecha YYYY-MM-DD) */
  @Modifying
  @Transactional
  @Query(value = """
      INSERT INTO solicitud_servicio
        (id_solicitud, tipo, nivel, fecha, estado, id_usuario_servicio, id_punto_partida)
      VALUES
        (:idSolicitud, :tipo, :nivel, TO_DATE(:fecha,'YYYY-MM-DD'), :estado, :idUsuarioServicio, :idPuntoPartida)
      """, nativeQuery = true)
  void insertarSolicitud(@Param("idSolicitud") Long idSolicitud,
                         @Param("tipo") String tipo,
                         @Param("nivel") String nivel,
                         @Param("fecha") String fecha,           // "2025-09-01"
                         @Param("estado") String estado,
                         @Param("idUsuarioServicio") Long idUsuarioServicio,
                         @Param("idPuntoPartida") Long idPuntoPartida);
}
