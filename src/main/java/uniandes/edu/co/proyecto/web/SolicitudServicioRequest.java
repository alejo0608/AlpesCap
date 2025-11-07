package uniandes.edu.co.proyecto.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SolicitudServicioRequest(
    @NotNull @Positive Long idUsuarioServicio,
    @NotNull @Positive Long idPuntoPartida,
    @NotNull @Positive Long idPuntoLlegada,
    @NotBlank String tipo,
    String nivel,
    String metodoPago,
    @Positive Long idTarjeta,
    @Positive Long idViaje,
    @Positive Long idPago
) {}
