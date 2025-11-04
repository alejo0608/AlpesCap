// src/main/java/uniandes/edu/co/proyecto/web/UsuarioConductorRequest.java
package uniandes.edu.co.proyecto.web;

import jakarta.validation.constraints.*;

public record UsuarioConductorRequest(
    @NotBlank @Size(max = 250) String nombre,
    @NotBlank @Email @Size(max = 250) String correo,
    @NotBlank @Size(max = 30) String telefono,
    @NotBlank @Size(max = 50) String cedula,
    @NotNull @DecimalMin("0.0") @DecimalMax("1.0") Double comision
) {}
