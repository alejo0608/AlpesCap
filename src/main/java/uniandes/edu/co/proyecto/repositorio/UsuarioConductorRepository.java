// src/main/java/uniandes/edu/co/proyecto/repositorio/UsuarioConductorRepository.java
package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import uniandes.edu.co.proyecto.modelo.UsuarioConductor;

public interface UsuarioConductorRepository extends JpaRepository<UsuarioConductor, Long> {

  boolean existsByCedula(String cedula);
  boolean existsByCorreo(String correo);

  @Query("SELECT COUNT(c) > 0 FROM UsuarioConductor c WHERE LOWER(c.correo) = LOWER(:correo)")
  boolean existsByCorreoIgnoreCase(@Param("correo") String correo);

  @Query("SELECT c FROM UsuarioConductor c WHERE c.cedula = :cedula")
  UsuarioConductor findByCedula(@Param("cedula") String cedula);

  @Query("SELECT c FROM UsuarioConductor c WHERE LOWER(c.correo) = LOWER(:correo)")
  UsuarioConductor findByCorreoIgnoreCase(@Param("correo") String correo);

  // Útiles para reglas de negocio (bloquear borrado si tiene dependencias)
  @Query(value = "SELECT COUNT(1) FROM VEHICULO WHERE ID_USUARIO_CONDUCTOR = :id", nativeQuery = true)
  int countVehiculos(@Param("id") Long idConductor);

  @Query(value = "SELECT COUNT(1) FROM VIAJE WHERE ID_USUARIO_CONDUCTOR = :id AND HORA_FIN IS NULL", nativeQuery = true)
  int countViajesAbiertos(@Param("id") Long idConductor);
}
