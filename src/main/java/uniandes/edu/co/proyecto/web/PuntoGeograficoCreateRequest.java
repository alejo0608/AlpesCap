package uniandes.edu.co.proyecto.web;
import jakarta.validation.constraints.*;

public record PuntoGeograficoCreateRequest(
    @NotBlank @Size(max = 250) String nombre,
    @NotNull @DecimalMin(value="-90.0") @DecimalMax(value="90.0") Double latitud,
    @NotNull @DecimalMin(value="-180.0") @DecimalMax(value="180.0") Double longitud,
    @NotBlank @Size(max = 250) String direccion,
    @NotNull @Positive Long idCiudad
) {}
