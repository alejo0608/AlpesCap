// src/main/java/uniandes/edu/co/proyecto/web/TarjetaCreditoRequest.java
package uniandes.edu.co.proyecto.web;

public record TarjetaCreditoRequest(
    Long idTarjeta,
    Long numero,
    String nombre,
    Integer mesVencimiento,
    Integer anioVencimiento,
    Integer codigoSeguridad,
    Long idUsuarioServicio
) {}
