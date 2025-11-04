// src/main/java/uniandes/edu/co/proyecto/service/ReseniaService.java
package uniandes.edu.co.proyecto.service;

public interface ReseniaService {
    void pasajeroCalificaConductor(Long idResenia, Long idViaje, Long idUsuarioServicioAutor,
                                   Integer calificacion, String comentario);

    void conductorCalificaPasajero(Long idResenia, Long idViaje, Long idUsuarioConductorAutor,
                                   Integer calificacion, String comentario);
}
