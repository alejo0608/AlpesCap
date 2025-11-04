// src/main/java/uniandes/edu/co/proyecto/web/PagoRequest.java
package uniandes.edu.co.proyecto.web;

public record PagoRequest(
    Long idPago,
    Long idViaje,
    Double monto,
    String fecha,   // YYYY-MM-DD
    String estado   // EN ESPERA | COMPLETADO | RECHAZADO
) {}
