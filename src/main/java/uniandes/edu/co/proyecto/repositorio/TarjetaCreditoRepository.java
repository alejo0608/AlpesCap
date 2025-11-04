// src/main/java/uniandes/edu/co/proyecto/repositorio/TarjetaCreditoRepository.java
package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import uniandes.edu.co.proyecto.modelo.TarjetaCredito;

public interface TarjetaCreditoRepository extends JpaRepository<TarjetaCredito, Long> {

  // RF8: ¿el usuario tiene al menos un medio de pago?
  @Query(value = "SELECT COUNT(1) FROM TARJETA_CREDITO WHERE ID_USUARIO_SERVICIO = :id", nativeQuery = true)
  int countByUsuarioServicio(@Param("id") Long idUsuarioServicio);

  // Útiles para validaciones (no indispensables para RF8, pero consistentes con el modelo)
  boolean existsByNumeroIgnoreCase(String numero);

  @Query(value = "SELECT COUNT(1) FROM TARJETA_CREDITO WHERE ID_USUARIO_SERVICIO = :id AND UPPER(NUMERO)=UPPER(:num)", nativeQuery = true)
  int countByUsuarioYNumero(@Param("id") Long idUsuarioServicio, @Param("num") String numero);
}
