// src/main/java/uniandes/edu/co/proyecto/service/ViajeService.java
package uniandes.edu.co.proyecto.service;

import java.util.Map;

public interface ViajeService {
    void finalizarViaje(Long idViaje, Double distanciaKm);
    Map<String, Object> finalizar(Long idViaje, Double distanciaKm, Double costoTotal);
}
