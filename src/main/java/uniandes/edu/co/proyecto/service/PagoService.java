package uniandes.edu.co.proyecto.service;

import java.util.Optional;
import uniandes.edu.co.proyecto.modelo.Pago;

public interface PagoService {
  Pago registrar(Long idPago, Long idUsuarioServicio, Long idViaje,
                 Double monto, String metodoPago, Long idTarjeta, String estado);

  Optional<Pago> obtener(Long idPago);

  Pago actualizarEstado(Long idPago, String estado);
}
