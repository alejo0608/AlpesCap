package uniandes.edu.co.proyecto.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uniandes.edu.co.proyecto.modelo.UsuarioConductor;
import uniandes.edu.co.proyecto.repositorio.UsuarioConductorRepository;
import uniandes.edu.co.proyecto.service.UsuarioConductorService;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioConductorServiceImpl implements UsuarioConductorService {

    @Autowired
    private UsuarioConductorRepository usuarioConductorRepository;

    @Override
    @Transactional
    public UsuarioConductor registrarConductor(UsuarioConductor conductor) {
        // Validar datos obligatorios
        if (conductor.getNombre() == null || conductor.getCorreo() == null ||
            conductor.getTelefono() == null || conductor.getCedula() == null) {
            throw new IllegalArgumentException("Todos los campos del conductor son obligatorios");
        }

        // Validar correo único
        boolean existeCorreo = usuarioConductorRepository.findAll().stream()
                .anyMatch(c -> c.getCorreo().equalsIgnoreCase(conductor.getCorreo()));
        if (existeCorreo) {
            throw new IllegalArgumentException("Ya existe un conductor con el correo " + conductor.getCorreo());
        }

        // Insertar el conductor (usando el método nativo o save)
        usuarioConductorRepository.insertarUsuarioConductor(
                conductor.getId(),
                conductor.getNombre(),
                conductor.getCorreo(),
                conductor.getTelefono(),
                conductor.getCedula(),
                conductor.getComision()
        );

        return conductor;
    }

    @Override
    public List<UsuarioConductor> obtenerTodos() {
        return usuarioConductorRepository.findAll();
    }

    @Override
    public Optional<UsuarioConductor> buscarPorId(Long id) {
        return usuarioConductorRepository.findById(id);
    }

    @Override
    public void eliminarConductor(Long id) {
        usuarioConductorRepository.deleteById(id);
    }
}