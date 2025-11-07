package uniandes.edu.co.proyecto.web;

public record PagoRequest(
  Long idPago,
  Long idUsuarioServicio,
  Long idViaje,
  Double monto,           // se enviará al campo VALOR
  String metodoPago,      // TARJETA|EFECTIVO|WALLET|PSE
  Long idTarjeta,         // requerido si metodoPago=TARJETA
  String estado           // APROBADO|EN ESPERA|COMPLETADO|RECHAZADO
) {}
