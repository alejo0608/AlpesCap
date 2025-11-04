// src/main/java/uniandes/edu/co/proyecto/service/PuntoGeograficoService.java
package uniandes.edu.co.proyecto.service;

import uniandes.edu.co.proyecto.modelo.PuntoGeografico;

public interface PuntoGeograficoService {
  PuntoGeografico registrar(Long idPunto, String nombre, Double latitud, Double longitud,
                            String direccion, Long idCiudad);
}
