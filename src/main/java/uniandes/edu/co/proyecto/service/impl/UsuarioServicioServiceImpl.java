// src/main/java/uniandes/edu/co/proyecto/service/impl/UsuarioServicioServiceImpl.java
package uniandes.edu.co.proyecto.service.impl;

import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uniandes.edu.co.proyecto.modelo.UsuarioServicio;
import uniandes.edu.co.proyecto.repositorio.UsuarioServicioRepository;
import uniandes.edu.co.proyecto.service.UsuarioServicioService;

@Service
public class UsuarioServicioServiceImpl implements UsuarioServicioService {

  private final UsuarioServicioRepository repo;

  public UsuarioServicioServiceImpl(UsuarioServicioRepository repo) {
    this.repo = repo;
  }

  private static final Pattern EMAIL_RX =
      Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
  private static final Pattern PHONE_RX =
      Pattern.compile("^[0-9]{7,15}$");

  @Override
  @Transactional
  public UsuarioServicio registrar(Long id, String nombre, String correo, String telefono, String cedula) {
    // Reglas de negocio / validaciones (400)
    if (id == null || id <= 0) throw new IllegalArgumentException("id requerido y debe ser positivo");
    if (nombre == null || nombre.trim().isEmpty()) throw new IllegalArgumentException("nombre requerido");
    if (correo == null || correo.trim().isEmpty()) throw new IllegalArgumentException("correo requerido");
    if (!EMAIL_RX.matcher(correo.trim()).matches()) throw new IllegalArgumentException("correo inválido");
    if (telefono == null || telefono.trim().isEmpty()) throw new IllegalArgumentException("telefono requerido");
    if (!PHONE_RX.matcher(telefono.trim()).matches()) throw new IllegalArgumentException("telefono inválido (7-15 dígitos)");
    if (cedula == null || cedula.trim().isEmpty()) throw new IllegalArgumentException("cedula requerida");

    String correoNorm = correo.trim().toLowerCase();
    String nombreNorm = nombre.trim();
    String telefonoNorm = telefono.trim();
    String cedulaNorm = cedula.trim();

    // Conflictos (409)
    if (repo.existsById(id)) throw new IllegalStateException("id ya existe");
    if (repo.existsByCorreoIgnoreCase(correoNorm)) throw new IllegalStateException("correo ya existe");
    if (repo.existsByCedulaIgnoreCase(cedulaNorm)) throw new IllegalStateException("cedula ya existe");

    // Persistencia
    UsuarioServicio u = new UsuarioServicio();
    u.setIdUsuarioServicio(id);
    u.setNombre(nombreNorm);
    u.setCorreo(correoNorm);
    u.setTelefono(telefonoNorm);
    u.setCedula(cedulaNorm);

    return repo.save(u);
  }
}
