// src/main/java/uniandes/edu/co/proyecto/repositorio/TarjetaCreditoRepository.java
package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import uniandes.edu.co.proyecto.modelo.TarjetaCredito;

public interface TarjetaCreditoRepository extends JpaRepository<TarjetaCredito, Long> {

  // Alias para mantener compatibilidad con otros servicios (RF8 usa countByUsuario)
  @Query(value = "SELECT COUNT(1) FROM TARJETA_CREDITO WHERE ID_USUARIO_SERVICIO = :id", nativeQuery = true)
  int countByUsuario(@Param("id") Long idUsuarioServicio);

  // Lo que tu controlador actual está llamando
  @Query(value = "SELECT COUNT(1) FROM USUARIO_SERVICIO WHERE ID_USUARIO_SERVICIO = :id", nativeQuery = true)
  int countUsuarioServicio(@Param("id") Long idUsuarioServicio);

  @Query(value = "SELECT COUNT(1) FROM TARJETA_CREDITO WHERE NUMERO = :numero", nativeQuery = true)
  int countByNumero(@Param("numero") Long numero);

  @Modifying @Transactional
  @Query(value = """
      INSERT INTO TARJETA_CREDITO
        (ID_TARJETA, NUMERO, NOMBRE, MES_VENCIMIENTO, ANIO_VENCIMIENTO, CODIGO_SEGURIDAD, ID_USUARIO_SERVICIO)
      VALUES
        (:idTarjeta, :numero, :nombre, :mes, :anio, :cvv, :idUsuarioServicio)
      """, nativeQuery = true)
  void insertarTarjeta(@Param("idTarjeta") Long idTarjeta,
                       @Param("numero") Long numero,
                       @Param("nombre") String nombre,
                       @Param("mes") Integer mesVencimiento,
                       @Param("anio") Integer anioVencimiento,
                       @Param("cvv") Integer codigoSeguridad,
                       @Param("idUsuarioServicio") Long idUsuarioServicio);
}
