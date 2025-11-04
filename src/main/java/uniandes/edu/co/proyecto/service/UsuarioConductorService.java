// src/main/java/uniandes/edu/co/proyecto/service/UsuarioConductorService.java
package uniandes.edu.co.proyecto.service;

import java.util.Optional;
import uniandes.edu.co.proyecto.modelo.UsuarioConductor;
import uniandes.edu.co.proyecto.web.UsuarioConductorRequest;

public interface UsuarioConductorService {

  UsuarioConductor registrarConductor(UsuarioConductorRequest request);

  Optional<UsuarioConductor> obtenerConductor(Long idConductor);

  // Opcional: actualizar básicos (útil si te piden RFs de edición luego)
  UsuarioConductor actualizarConductor(Long idConductor, UsuarioConductorRequest request);

  // Opcional: borrar con guardas de integridad de negocio
  void eliminarConductor(Long idConductor);
}
