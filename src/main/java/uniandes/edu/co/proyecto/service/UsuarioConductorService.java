package uniandes.edu.co.proyecto.service;

import uniandes.edu.co.proyecto.modelo.UsuarioConductor;
import java.util.List;
import java.util.Optional;

public interface UsuarioConductorService {

    UsuarioConductor registrarConductor(UsuarioConductor conductor);

    List<UsuarioConductor> obtenerTodos();

    Optional<UsuarioConductor> buscarPorId(Long id);

    void eliminarConductor(Long id);
}
