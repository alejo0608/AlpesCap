// src/main/java/uniandes/edu/co/proyecto/repositorio/CiudadRepository.java
package uniandes.edu.co.proyecto.repositorio;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import uniandes.edu.co.proyecto.modelo.Ciudad;

public interface CiudadRepository extends JpaRepository<Ciudad, Long> {
  boolean existsByNombreIgnoreCase(String nombre);
  Optional<Ciudad> findByNombreIgnoreCase(String nombre);
}
