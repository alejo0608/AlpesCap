// src/main/java/uniandes/edu/co/proyecto/repositorio/UsuarioServicioRepository.java
package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import uniandes.edu.co.proyecto.modelo.UsuarioServicio;

public interface UsuarioServicioRepository extends JpaRepository<UsuarioServicio, Long> {
  boolean existsByCorreoIgnoreCase(String correo);
  boolean existsByCedulaIgnoreCase(String cedula);
}
