package uniandes.edu.co.proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uniandes.edu.co.proyecto.modelo.Vehiculo;
import uniandes.edu.co.proyecto.service.VehiculoService;

@RestController
public class VehiculoController {

    @Autowired
    private VehiculoService vehiculoService;

    @PostMapping("/vehiculos/new/save")
    public ResponseEntity<?> registrarVehiculo(@RequestBody Vehiculo vehiculo) {
        try {
            vehiculoService.registrarVehiculo(vehiculo);
            return ResponseEntity.status(HttpStatus.CREATED).body("Vehículo registrado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en los datos: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar el vehículo: " + e.getMessage());
        }
    }
}
