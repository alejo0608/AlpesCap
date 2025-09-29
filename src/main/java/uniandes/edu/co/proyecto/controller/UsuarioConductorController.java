package uniandes.edu.co.proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import uniandes.edu.co.proyecto.modelo.UsuarioConductor;
import uniandes.edu.co.proyecto.repositorio.UsuarioConductorRepository;

@RestController
public class UsuarioConductorController {

    @Autowired
    private UsuarioConductorRepository usuarioConductorRepository;

    @PostMapping("/usuarios-conductor/new/save")
    public ResponseEntity<?> insertUsuarioConductor(@RequestBody UsuarioConductor conductor) {
        try {
            usuarioConductorRepository.insertarUsuarioConductor(
                conductor.getId(),
                conductor.getNombre(),
                conductor.getCorreo(),
                conductor.getTelefono(),
                conductor.getCedula(),
                conductor.getComision()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario conductor registrado correctamente");
        } catch (Exception e) {
            return new ResponseEntity<>("Error al registrar el usuario conductor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}