// src/main/java/uniandes/edu/co/proyecto/service/impl/CiudadServiceImpl.java
package uniandes.edu.co.proyecto.service.impl;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uniandes.edu.co.proyecto.modelo.Ciudad;
import uniandes.edu.co.proyecto.repositorio.CiudadRepository;
import uniandes.edu.co.proyecto.service.CiudadService;

@Service
public class CiudadServiceImpl implements CiudadService {

  private final CiudadRepository repo;

  public CiudadServiceImpl(CiudadRepository repo) {
    this.repo = repo;
  }

  @Override
  @Transactional
  public Ciudad registrar(String nombre) {
    if (nombre == null || nombre.trim().isEmpty())
      throw new IllegalArgumentException("nombre requerido");
    String limpio = nombre.trim();
    if (repo.existsByNombreIgnoreCase(limpio))
      throw new IllegalStateException("nombre ya existe");
    Ciudad c = new Ciudad();
    c.setNombre(limpio);
    return repo.save(c); // usa CIUDAD_SEQ (@GeneratedValue)
  }

  @Override
  public Optional<Ciudad> buscarPorNombre(String nombre) {
    if (nombre == null) return Optional.empty();
    return repo.findByNombreIgnoreCase(nombre.trim());
  }
}
