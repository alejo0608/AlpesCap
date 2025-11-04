package uniandes.edu.co.proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uniandes.edu.co.proyecto.modelo.UsuarioConductor;
import uniandes.edu.co.proyecto.service.UsuarioConductorService;

@RestController
public class UsuarioConductorController {

    @Autowired
    private UsuarioConductorService usuarioConductorService;

    @PostMapping("/usuarios-conductor/new/save")
    public ResponseEntity<?> insertUsuarioConductor(@RequestBody UsuarioConductor conductor) {
        try {
            usuarioConductorService.registrarConductor(conductor);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Usuario conductor registrado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error en los datos: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar el usuario conductor: " + e.getMessage());
        }
    }
}