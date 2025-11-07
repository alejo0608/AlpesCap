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

  // Verifica existencia del usuario de servicios (FK)
  @Query(value = "SELECT COUNT(1) FROM USUARIO_SERVICIO WHERE ID_USUARIO_SERVICIO = :id", nativeQuery = true)
  int countUsuarioServicio(@Param("id") Long idUsuarioServicio);

  // Normaliza número (quita espacios y guiones) para evitar duplicados “trampa”
  @Query(value = """
      SELECT COUNT(1)
        FROM TARJETA_CREDITO
       WHERE REPLACE(REPLACE(NUMERO,'-',''),' ','')
           = REPLACE(REPLACE(:numero,'-',''),' ','')
      """, nativeQuery = true)
  int countByNumero(@Param("numero") String numero);

  /* ==== Inserción (usada por RF2/Controller de tarjetas) ==== */
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Transactional
  @Query(value = """
      INSERT INTO TARJETA_CREDITO
        (ID_TARJETA, NUMERO, TITULAR, MES_VENCIMIENTO, ANIO_VENCIMIENTO, CVV, ID_USUARIO_SERVICIO)
      VALUES
        (:idTarjeta, :numero, :titular, :mesVenc, :anioVenc, :cvv, :idUsuarioServicio)
      """, nativeQuery = true)
  int insertarTarjeta(@Param("idTarjeta") Long idTarjeta,
                      @Param("numero") String numero,
                      @Param("titular") String titular,
                      @Param("mesVenc") Integer mesVencimiento,
                      @Param("anioVenc") Integer anioVencimiento,
                      @Param("cvv") Integer cvv,
                      @Param("idUsuarioServicio") Long idUsuarioServicio);

  /* ==== RF8: Verificación de medio de pago (tarjeta) ==== */

  // ¿Cuántas tarjetas vigentes tiene el usuario?
  @Query(value = """
      SELECT COUNT(1)
        FROM TARJETA_CREDITO
       WHERE ID_USUARIO_SERVICIO = :id
         AND (ANIO_VENCIMIENTO > EXTRACT(YEAR FROM SYSDATE)
           OR (ANIO_VENCIMIENTO = EXTRACT(YEAR FROM SYSDATE)
           AND  MES_VENCIMIENTO  >= EXTRACT(MONTH FROM SYSDATE)))
      """, nativeQuery = true)
  int countTarjetasVigentes(@Param("id") Long idUsuarioServicio);

  // Devuelve una tarjeta vigente cualquiera del usuario (para cobrar)
  @Query(value = """
      SELECT ID_TARJETA
        FROM TARJETA_CREDITO
       WHERE ID_USUARIO_SERVICIO = :id
         AND (ANIO_VENCIMIENTO > EXTRACT(YEAR FROM SYSDATE)
           OR (ANIO_VENCIMIENTO = EXTRACT(YEAR FROM SYSDATE)
           AND  MES_VENCIMIENTO  >= EXTRACT(MONTH FROM SYSDATE)))
       FETCH FIRST 1 ROWS ONLY
      """, nativeQuery = true)
  Long findAnyTarjetaVigente(@Param("id") Long idUsuarioServicio);

  // Valida que una tarjeta específica sea del usuario y esté vigente
  @Query(value = """
      SELECT COUNT(1)
        FROM TARJETA_CREDITO
       WHERE ID_TARJETA = :idTarjeta
         AND ID_USUARIO_SERVICIO = :idUsrServ
         AND (ANIO_VENCIMIENTO > EXTRACT(YEAR FROM SYSDATE)
           OR (ANIO_VENCIMIENTO = EXTRACT(YEAR FROM SYSDATE)
           AND  MES_VENCIMIENTO  >= EXTRACT(MONTH FROM SYSDATE)))
      """, nativeQuery = true)
  int countTarjetaVigenteDeUsuario(@Param("idTarjeta") Long idTarjeta,
                                   @Param("idUsrServ") Long idUsuarioServicio);
}