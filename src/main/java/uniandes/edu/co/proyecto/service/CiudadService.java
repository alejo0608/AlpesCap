// src/main/java/uniandes/edu/co/proyecto/service/CiudadService.java
package uniandes.edu.co.proyecto.service;

import java.util.Optional;
import uniandes.edu.co.proyecto.modelo.Ciudad;

public interface CiudadService {
  Ciudad registrar(String nombre);
  Optional<Ciudad> buscarPorNombre(String nombre);
}
