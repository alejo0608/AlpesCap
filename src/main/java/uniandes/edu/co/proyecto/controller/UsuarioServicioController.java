package uniandes.edu.co.proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import uniandes.edu.co.proyecto.modelo.UsuarioServicio;
import uniandes.edu.co.proyecto.repositorio.UsuarioServicioRepository;

@RestController
public class UsuarioServicioController {

    @Autowired
    private UsuarioServicioRepository usuarioServicioRepository;

    @PostMapping("/usuarios-servicio/new/save")
    public ResponseEntity<?> insertUsuarioServicio(@RequestBody UsuarioServicio usuario) {
        try {
            usuarioServicioRepository.insertarUsuarioServicio(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getTelefono(),
                usuario.getCedula()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario de servicio registrado correctamente");
        } catch (Exception e) {
            return new ResponseEntity<>("Error al registrar el usuario de servicio", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}