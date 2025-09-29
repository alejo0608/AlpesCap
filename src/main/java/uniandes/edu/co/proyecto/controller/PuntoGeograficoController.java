package uniandes.edu.co.proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import uniandes.edu.co.proyecto.repositorio.PuntoGeograficoRepository;

@RestController
public class PuntoGeograficoController {

    @Autowired
    private PuntoGeograficoRepository puntoRepo;

    @PostMapping("/puntos/new/save")
    public ResponseEntity<?> insertPunto(@RequestBody Map<String, Object> body) {
        try {
            puntoRepo.insertarPunto(
                ((Number) body.get("idPunto")).longValue(),
                (String) body.get("nombre"),
                ((Number) body.get("latitud")).doubleValue(),
                ((Number) body.get("longitud")).doubleValue(),
                (String) body.get("direccion"),
                ((Number) body.get("idCiudad")).longValue()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body("Punto geográfico registrado correctamente");
        } catch (Exception e) {
            return new ResponseEntity<>("Error al registrar el punto geográfico", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}