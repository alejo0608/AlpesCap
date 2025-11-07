// src/main/java/uniandes/edu/co/proyecto/modelo/Pago.java
package uniandes.edu.co.proyecto.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
  name = "PAGO",
  uniqueConstraints = { @UniqueConstraint(name = "UQ_PAGO_VIAJE", columnNames = "ID_VIAJE") }
)
public class Pago {

  @Id
  @Column(name = "ID_PAGO")
  private Long idPago;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "ID_USUARIO_SERVICIO", nullable = false,
              foreignKey = @ForeignKey(name = "FK_PAGO_USR_SERV"))
  private UsuarioServicio usuarioServicio;

  @Column(name = "METODO", nullable = false, length = 30)
  private String metodo; // TARJETA | EFECTIVO | WALLET | PSE

  @ManyToOne(fetch = FetchType.LAZY, optional = true)
  @JoinColumn(name = "ID_TARJETA",
              foreignKey = @ForeignKey(name = "FK_PAGO_TARJETA"))
  private TarjetaCredito tarjeta; // null si no es TARJETA

  @OneToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "ID_VIAJE", nullable = false, unique = true,
              foreignKey = @ForeignKey(name = "FK_PAGO_VIAJE"))
  private Viaje viaje;

  @Column(name = "VALOR", nullable = false)
  private Double valor;

  @Column(name = "FECHA", nullable = false)
  private LocalDateTime fecha; // SYSDATE

  @Column(name = "ESTADO", nullable = false, length = 30)
  private String estado; // APROBADO | EN ESPERA | COMPLETADO | RECHAZADO

  public Pago() {}

  public Long getIdPago() { return idPago; }
  public void setIdPago(Long idPago) { this.idPago = idPago; }

  public UsuarioServicio getUsuarioServicio() { return usuarioServicio; }
  public void setUsuarioServicio(UsuarioServicio usuarioServicio) { this.usuarioServicio = usuarioServicio; }

  public String getMetodo() { return metodo; }
  public void setMetodo(String metodo) { this.metodo = metodo; }

  public TarjetaCredito getTarjeta() { return tarjeta; }
  public void setTarjeta(TarjetaCredito tarjeta) { this.tarjeta = tarjeta; }

  public Viaje getViaje() { return viaje; }
  public void setViaje(Viaje viaje) { this.viaje = viaje; }

  public Double getValor() { return valor; }
  public void setValor(Double valor) { this.valor = valor; }

  public LocalDateTime getFecha() { return fecha; }
  public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

  public String getEstado() { return estado; }
  public void setEstado(String estado) { this.estado = estado; }
}
