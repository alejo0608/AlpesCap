// src/main/java/uniandes/edu/co/proyecto/web/UsuarioServicioRequest.java
package uniandes.edu.co.proyecto.web;

public record UsuarioServicioRequest(
    Long id,
    String nombre,
    String correo,
    String telefono,
    String cedula
) {}
