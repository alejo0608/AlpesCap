// src/main/java/uniandes/edu/co/proyecto/repositorio/PagoRepository.java
package uniandes.edu.co.proyecto.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import uniandes.edu.co.proyecto.modelo.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {

  // ¿Ya existe pago para un viaje? (1:1 con VIAJE)
  boolean existsByViaje_IdViaje(Long idViaje);

  Optional<Pago> findByViaje_IdViaje(Long idViaje);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Transactional
  @Query(value = """
    INSERT INTO PAGO
      (ID_PAGO, ID_USUARIO_SERVICIO, METODO, ID_TARJETA, ID_VIAJE, VALOR, FECHA, ESTADO)
    VALUES
      (:idPago, :idUsrServ, :metodo, :idTarjeta, :idViaje, :valor, SYSDATE, :estado)
  """, nativeQuery = true)
  int insertarPagoConViaje(@Param("idPago") Long idPago,
                           @Param("idUsrServ") Long idUsuarioServicio,
                           @Param("metodo") String metodo,
                           @Param("idTarjeta") Long idTarjeta,   // puede ser NULL
                           @Param("idViaje") Long idViaje,
                           @Param("valor") Double valor,
                           @Param("estado") String estado);

  @Query(value = "SELECT COUNT(1) FROM PAGO WHERE ID_VIAJE = :id", nativeQuery = true)
  int countByViaje(@Param("id") Long idViaje);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Transactional
  @Query(value = "UPDATE PAGO SET ESTADO = :estado WHERE ID_PAGO = :id", nativeQuery = true)
  int actualizarEstado(@Param("id") Long idPago, @Param("estado") String estado);
}
