// src/main/java/uniandes/edu/co/proyecto/service/impl/UsuarioConductorServiceImpl.java
package uniandes.edu.co.proyecto.service.impl;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uniandes.edu.co.proyecto.modelo.UsuarioConductor;
import uniandes.edu.co.proyecto.repositorio.UsuarioConductorRepository;
import uniandes.edu.co.proyecto.service.UsuarioConductorService;
import uniandes.edu.co.proyecto.web.UsuarioConductorRequest;

import java.util.Locale;
import java.util.Optional;

@Service
public class UsuarioConductorServiceImpl implements UsuarioConductorService {

  private final UsuarioConductorRepository repo;

  public UsuarioConductorServiceImpl(UsuarioConductorRepository repo) {
    this.repo = repo;
  }

  @Override
  @Transactional
  public UsuarioConductor registrarConductor(@Valid UsuarioConductorRequest request) {
    String correo = request.correo().trim().toLowerCase(Locale.ROOT);
    String cedula = request.cedula().trim();

    if (repo.existsByCedula(cedula)) throw new IllegalStateException("Cédula ya registrada");
    if (repo.existsByCorreoIgnoreCase(correo)) throw new IllegalStateException("Correo ya registrado");

    UsuarioConductor c = new UsuarioConductor();
    c.setNombre(request.nombre());
    c.setCorreo(correo);
    c.setTelefono(request.telefono());
    c.setCedula(cedula);
    c.setComision(request.comision());

    return repo.save(c);
  }

  @Override
  public Optional<UsuarioConductor> obtenerConductor(Long idConductor) {
    return repo.findById(idConductor);
  }

  @Override
  @Transactional
  public UsuarioConductor actualizarConductor(Long idConductor, @Valid UsuarioConductorRequest request) {
    UsuarioConductor c = repo.findById(idConductor)
        .orElseThrow(() -> new RuntimeException("Conductor no existe: " + idConductor));

    String correo = request.correo().trim().toLowerCase(Locale.ROOT);
    String cedula = request.cedula().trim();

    // Si cambia correo, validar unicidad
    if (!correo.equalsIgnoreCase(c.getCorreo()) && repo.existsByCorreoIgnoreCase(correo)) {
      throw new IllegalStateException("Correo ya registrado");
    }
    // Si cambia cédula, validar unicidad
    if (!cedula.equals(c.getCedula()) && repo.existsByCedula(cedula)) {
      throw new IllegalStateException("Cédula ya registrada");
    }

    c.setNombre(request.nombre());
    c.setCorreo(correo);
    c.setTelefono(request.telefono());
    c.setCedula(cedula);
    c.setComision(request.comision());

    return repo.save(c);
  }

  @Override
  @Transactional
  public void eliminarConductor(Long idConductor) {
    UsuarioConductor c = repo.findById(idConductor)
        .orElseThrow(() -> new RuntimeException("Conductor no existe: " + idConductor));

    if (repo.countVehiculos(idConductor) > 0) {
      throw new IllegalStateException("No se puede eliminar: tiene vehículos asociados");
    }
    if (repo.countViajesAbiertos(idConductor) > 0) {
      throw new IllegalStateException("No se puede eliminar: tiene viajes abiertos");
    }
    repo.delete(c);
  }
}
