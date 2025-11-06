package uniandes.edu.co.proyecto.web;

import jakarta.validation.constraints.*;

public record PuntoGeograficoRequest( 
    @NotNull @Positive Long idPunto,
    @NotBlank String nombre,
    @NotNull @DecimalMin(value = "-90.0", inclusive = true) @DecimalMax(value = "90.0", inclusive = true) Double latitud,
    @NotNull @DecimalMin(value = "-180.0", inclusive = true) @DecimalMax(value = "180.0", inclusive = true) Double longitud,
    @NotBlank @Size(max = 250) String direccion,
    @NotNull @Positive Long idCiudad
) {}

