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

  // ¿existe usuario de servicios?
  @Query(value = "SELECT COUNT(1) FROM USUARIO_SERVICIO WHERE ID_USUARIO_SERVICIO = :id", nativeQuery = true)
  int countUsuarioServicio(@Param("id") Long idUsuarioServicio);

  // Duplicado por número de tarjeta
  @Query(value = "SELECT COUNT(1) FROM TARJETA_CREDITO WHERE NUMERO = :numero", nativeQuery = true)
  int countByNumero(@Param("numero") String numero);

  // Insert nativo con columnas reales
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Transactional
  @Query(value = """
    INSERT INTO TARJETA_CREDITO
      (ID_TARJETA, NUMERO, NOMBRE, MES_VENCIMIENTO, ANIO_VENCIMIENTO, CODIGO_SEGURIDAD, ID_USUARIO_SERVICIO)
    VALUES
      (:idTarjeta, :numero, :nombre, :mesVto, :anioVto, :codigoSeguridad, :idUsuarioServicio)
    """, nativeQuery = true)
  int insertarTarjeta(@Param("idTarjeta") Long idTarjeta,
                      @Param("idUsuarioServicio") Long idUsuarioServicio,
                      @Param("numero") String numero,
                      @Param("nombre") String nombre,
                      @Param("mesVto") Integer mesVencimiento,
                      @Param("anioVto") Integer anioVencimiento,
                      @Param("codigoSeguridad") Integer codigoSeguridad);

  // Conteo de tarjetas vigentes (mes/año >= hoy)
  @Query(value = """
    SELECT COUNT(1)
      FROM TARJETA_CREDITO
     WHERE ID_USUARIO_SERVICIO = :idUsuario
       AND ( ANIO_VENCIMIENTO > TO_NUMBER(TO_CHAR(SYSDATE,'YYYY'))
          OR (ANIO_VENCIMIENTO = TO_NUMBER(TO_CHAR(SYSDATE,'YYYY'))
              AND MES_VENCIMIENTO >= TO_NUMBER(TO_CHAR(SYSDATE,'MM'))))
    """, nativeQuery = true)
  int countTarjetasVigentes(@Param("idUsuario") Long idUsuarioServicio);

  // ¿Esa idTarjeta pertenece al usuario y está vigente?
  @Query(value = """
    SELECT COUNT(1)
      FROM TARJETA_CREDITO
     WHERE ID_TARJETA = :idTarjeta
       AND ID_USUARIO_SERVICIO = :idUsuario
       AND ( ANIO_VENCIMIENTO > TO_NUMBER(TO_CHAR(SYSDATE,'YYYY'))
          OR (ANIO_VENCIMIENTO = TO_NUMBER(TO_CHAR(SYSDATE,'YYYY'))
              AND MES_VENCIMIENTO >= TO_NUMBER(TO_CHAR(SYSDATE,'MM'))))
    """, nativeQuery = true)
  int countTarjetaVigenteDeUsuario(@Param("idTarjeta") Long idTarjeta,
                                   @Param("idUsuario") Long idUsuarioServicio);

  // Dame cualquier tarjeta vigente (la “mejor” por vencimiento)
  @Query(value = """
    SELECT ID_TARJETA
      FROM TARJETA_CREDITO
     WHERE ID_USUARIO_SERVICIO = :idUsuario
       AND ( ANIO_VENCIMIENTO > TO_NUMBER(TO_CHAR(SYSDATE,'YYYY'))
          OR (ANIO_VENCIMIENTO = TO_NUMBER(TO_CHAR(SYSDATE,'YYYY'))
              AND MES_VENCIMIENTO >= TO_NUMBER(TO_CHAR(SYSDATE,'MM'))))
     ORDER BY ANIO_VENCIMIENTO, MES_VENCIMIENTO
     FETCH FIRST 1 ROWS ONLY
    """, nativeQuery = true)
  Long findAnyTarjetaVigente(@Param("idUsuario") Long idUsuarioServicio);
}