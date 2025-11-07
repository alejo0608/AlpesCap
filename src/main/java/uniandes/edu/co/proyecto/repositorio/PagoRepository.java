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
      (ID_PAGO, METODO, ID_TARJETA, ID_VIAJE, MONTO, FECHA, ESTADO)
    VALUES
      (:idPago, :metodo, :idTarjeta, :idViaje, :monto, SYSDATE, :estado)
  """, nativeQuery = true)
  int insertarPagoConViaje(@Param("idPago") Long idPago,
                           @Param("metodo") String metodo,      // TARJETA | EFECTIVO | WALLET | PSE
                           @Param("idTarjeta") Long idTarjeta,  // null si no aplica
                           @Param("idViaje") Long idViaje,
                           @Param("monto") Double monto,
                           @Param("estado") String estado);     // APROBADO | RECHAZADO | EN ESPERA

  
//
@Query(value = "SELECT COUNT(1) FROM PAGO WHERE ID_VIAJE = :idViaje", nativeQuery = true)
int countByViaje(@Param("idViaje") Long idViaje);

@Query(value = "SELECT ID_PAGO FROM PAGO WHERE ID_VIAJE = :idViaje FETCH FIRST 1 ROWS ONLY", nativeQuery = true)
Long findIdByViaje(@Param("idViaje") Long idViaje);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Transactional
  @Query(value = "UPDATE PAGO SET ESTADO = :estado WHERE ID_PAGO = :id", nativeQuery = true)
  int actualizarEstado(@Param("id") Long idPago, @Param("estado") String estado);
}
