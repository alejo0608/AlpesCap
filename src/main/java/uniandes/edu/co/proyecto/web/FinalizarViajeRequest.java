package uniandes.edu.co.proyecto.web;

public record FinalizarViajeRequest(
    Long idViaje,
    Double distanciaKm,
    Double costoTotal) {}