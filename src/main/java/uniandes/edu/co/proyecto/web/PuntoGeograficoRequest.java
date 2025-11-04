// src/main/java/uniandes/edu/co/proyecto/web/PuntoGeograficoRequest.java
package uniandes.edu.co.proyecto.web;

public record PuntoGeograficoRequest(
    Long idPunto,
    String nombre,
    Double latitud,
    Double longitud,
    String direccion,
    Long idCiudad
) {}
