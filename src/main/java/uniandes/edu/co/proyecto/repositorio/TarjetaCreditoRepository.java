package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import uniandes.edu.co.proyecto.modelo.TarjetaCredito;

public interface TarjetaCreditoRepository extends JpaRepository<TarjetaCredito, Long> {

  @Query(value = "SELECT COUNT(1) FROM usuario_servicio WHERE id_usuario_servicio = :id", nativeQuery = true)
  int countUsuarioServicio(@Param("id") Long idUsuarioServicio);

  @Query(value = "SELECT COUNT(1) FROM tarjeta_credito WHERE numero = :numero", nativeQuery = true)
  int countByNumero(@Param("numero") String numero);

  @Modifying
  @Transactional
  @Query(value = """
      INSERT INTO tarjeta_credito
        (id_tarjeta, numero, nombre, mes_vencimiento, anio_vencimiento, codigo_seguridad, id_usuario_servicio)
      VALUES
        (:idTarjeta, :numero, :nombre, :mesVencimiento, :anioVencimiento, :codigoSeguridad, :idUsuarioServicio)
      """, nativeQuery = true)
  void insertarTarjeta(@Param("idTarjeta") Long idTarjeta,
                       @Param("numero") String numero,
                       @Param("nombre") String nombre,
                       @Param("mesVencimiento") Integer mesVencimiento,
                       @Param("anioVencimiento") Integer anioVencimiento,
                       @Param("codigoSeguridad") Integer codigoSeguridad,
                       @Param("idUsuarioServicio") Long idUsuarioServicio);
}
