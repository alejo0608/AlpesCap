// src/main/java/uniandes/edu/co/proyecto/service/PagoService.java
package uniandes.edu.co.proyecto.service;

import java.util.Optional;
import uniandes.edu.co.proyecto.modelo.Pago;

public interface PagoService {
  Pago registrar(Long idPago, Long idViaje, Double monto, String fecha, String estado);
  Optional<Pago> obtener(Long idPago);
  Pago actualizarEstado(Long idPago, String estado);
}
