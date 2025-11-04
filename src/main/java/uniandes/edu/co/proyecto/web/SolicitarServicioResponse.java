// src/main/java/uniandes/edu/co/proyecto/web/SolicitarServicioResponse.java
package uniandes.edu.co.proyecto.web;

public record SolicitarServicioResponse(
    Long idSolicitud,
    Long idViaje,
    Long idConductor,
    Long idVehiculo,
    Double distanciaKm,
    Double costoTotal
) {}
