// src/main/java/uniandes/edu/co/proyecto/modelo/TarjetaCredito.java
package uniandes.edu.co.proyecto.modelo;

import jakarta.persistence.*;

@Entity
@Table(
    name = "TARJETA_CREDITO",
    uniqueConstraints = {
        @UniqueConstraint(name = "UQ_TARJETA_NUMERO", columnNames = "NUMERO")
    }
)
public class TarjetaCredito {

  @Id
  @Column(name = "ID_TARJETA")
  private Long idTarjeta; // Provisto por el cliente (sin @GeneratedValue)

  @Column(name = "NUMERO", nullable = false, length = 32)
  private String numero; // como String para no perder ceros a la izquierda

  @Column(name = "NOMBRE", nullable = false, length = 250)
  private String nombre; // nombre del titular

  @Column(name = "MES_VENCIMIENTO", nullable = false)
  private Integer mesVencimiento; // 1..12

  @Column(name = "ANIO_VENCIMIENTO", nullable = false)
  private Integer anioVencimiento; // ej. 2025

  @Column(name = "CODIGO_SEGURIDAD", nullable = false, length = 4)
  private String codigoSeguridad; // CVV/CVC (3-4 dígitos)

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "ID_USUARIO_SERVICIO", nullable = false,
      foreignKey = @ForeignKey(name = "FK_TARJETA_USR_SERV"))
  private UsuarioServicio usuarioServicio;

  public TarjetaCredito() {}

  public Long getIdTarjeta() { return idTarjeta; }
  public void setIdTarjeta(Long idTarjeta) { this.idTarjeta = idTarjeta; }

  public String getNumero() { return numero; }
  public void setNumero(String numero) { this.numero = numero; }

  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }

  public Integer getMesVencimiento() { return mesVencimiento; }
  public void setMesVencimiento(Integer mesVencimiento) { this.mesVencimiento = mesVencimiento; }

  public Integer getAnioVencimiento() { return anioVencimiento; }
  public void setAnioVencimiento(Integer anioVencimiento) { this.anioVencimiento = anioVencimiento; }

  public String getCodigoSeguridad() { return codigoSeguridad; }
  public void setCodigoSeguridad(String codigoSeguridad) { this.codigoSeguridad = codigoSeguridad; }

  public UsuarioServicio getUsuarioServicio() { return usuarioServicio; }
  public void setUsuarioServicio(UsuarioServicio usuarioServicio) { this.usuarioServicio = usuarioServicio; }
}
