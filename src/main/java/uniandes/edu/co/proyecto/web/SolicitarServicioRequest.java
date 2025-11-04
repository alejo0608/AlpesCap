// src/main/java/uniandes/edu/co/proyecto/web/SolicitarServicioRequest.java
package uniandes.edu.co.proyecto.web;

public record SolicitarServicioRequest(
    Long idSolicitud,
    Long idViaje,
    Long idPago,                 // opcional: si quieres registrar el pago en la misma transacción
    Long idUsuarioServicio,
    Long idPuntoPartida,
    Long idPuntoLlegada,         // opcional
    String tipo,                 // PASAJEROS | COMIDA | MERCANCIAS
    String nivel                 // ESTANDAR | CONFORT | LARGE
) {}
