package uniandes.edu.co.proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import uniandes.edu.co.proyecto.modelo.Ciudad;
import uniandes.edu.co.proyecto.repositorio.CiudadRepository;

@RestController
public class CiudadController {

    @Autowired
    private CiudadRepository ciudadRepository;

    @PostMapping("/ciudades/new/save") // <-- usa comillas normales y empieza con "/"
    public ResponseEntity<?> insertCiudad(@RequestBody Ciudad ciudad) {
        try {
            ciudadRepository.insertarCiudad(ciudad.getIdCiudad(), ciudad.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body("Ciudad registrada correctamente");
        } catch (Exception e) {
            return new ResponseEntity<>("Error al registrar la ciudad", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}