// src/main/java/uniandes/edu/co/proyecto/modelo/Pago.java
package uniandes.edu.co.proyecto.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
    name = "PAGO",
    uniqueConstraints = {
        @UniqueConstraint(name = "UQ_PAGO_VIAJE", columnNames = "ID_VIAJE")
    }
)
public class Pago {

  @Id
  @Column(name = "ID_PAGO")
  private Long idPago; // Provisto por el cliente

  @Column(name = "MONTO", nullable = false)
  private Double monto;

  @Column(name = "FECHA", nullable = false)
  private LocalDate fecha;

  @Column(name = "ESTADO", nullable = false, length = 30)
  private String estado; // p.ej., APROBADO / RECHAZADO

  @OneToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(
      name = "ID_VIAJE",
      nullable = false,
      unique = true,
      foreignKey = @ForeignKey(name = "FK_PAGO_VIAJE")
  )
  private Viaje viaje;

  public Pago() {}

  public Long getIdPago() { return idPago; }
  public void setIdPago(Long idPago) { this.idPago = idPago; }

  public Double getMonto() { return monto; }
  public void setMonto(Double monto) { this.monto = monto; }

  public LocalDate getFecha() { return fecha; }
  public void setFecha(LocalDate fecha) { this.fecha = fecha; }

  public String getEstado() { return estado; }
  public void setEstado(String estado) { this.estado = estado; }

  public Viaje getViaje() { return viaje; }
  public void setViaje(Viaje viaje) { this.viaje = viaje; }
}
