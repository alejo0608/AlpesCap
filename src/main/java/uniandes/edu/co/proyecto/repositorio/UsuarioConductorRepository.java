// src/main/java/uniandes/edu/co/proyecto/repositorio/UsuarioConductorRepository.java
package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import uniandes.edu.co.proyecto.modelo.UsuarioConductor;

public interface UsuarioConductorRepository extends JpaRepository<UsuarioConductor, Long> {
  boolean existsByCorreoIgnoreCase(String correo);
  boolean existsByCedulaIgnoreCase(String cedula);
}
