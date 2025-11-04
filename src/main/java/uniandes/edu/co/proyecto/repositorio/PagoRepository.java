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

  @Query(value = "SELECT COUNT(1) FROM PAGO WHERE ID_VIAJE = :id", nativeQuery = true)
  int countByViaje(@Param("id") Long idViaje);

  Optional<Pago> findByViaje_IdViaje(Long idViaje);

  // Útil para RF9 si cambias estado del pago
  @Modifying
  @Transactional
  @Query(value = "UPDATE PAGO SET ESTADO = :estado WHERE ID_PAGO = :idPago", nativeQuery = true)
  int actualizarEstado(@Param("idPago") Long idPago, @Param("estado") String estado);
}
