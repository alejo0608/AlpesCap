// src/main/java/uniandes/edu/co/proyecto/modelo/Disponibilidad.java
package uniandes.edu.co.proyecto.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.Check;

import java.util.Date;
import java.util.Locale;

@Entity
@Table(name = "DISPONIBILIDAD")
@Check(constraints = "HORA_INICIO < HORA_FIN")
public class Disponibilidad {

  @Id
  @Column(name = "ID_DISPONIBILIDAD")
  private Long idDisponibilidad;  // si usas secuencia, agrega @GeneratedValue con tu SEQ

  @NotBlank
  @Size(max = 20)
  @Column(name = "DIA", nullable = false, length = 20)
  private String dia;

  @NotNull
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "HORA_INICIO", nullable = false)
  private Date horaInicio;

  @NotNull
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "HORA_FIN", nullable = false)
  private Date horaFin;

  @NotBlank
  @Size(max = 30)
  @Column(name = "TIPO_SERVICIO", nullable = false, length = 30)
  private String tipoServicio;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ID_VEHICULO", nullable = false)
  @JsonIgnore
  private Vehiculo vehiculo;

  // 🔴 Este campo DEBE llamarse igual que el mappedBy en UsuarioConductor
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ID_USUARIO_CONDUCTOR", nullable = false)
  @JsonIgnore
  private UsuarioConductor usuarioConductor;

  @PrePersist @PreUpdate
  private void normalize() {
    if (dia != null) dia = dia.trim().toUpperCase(Locale.ROOT);
    if (tipoServicio != null) tipoServicio = tipoServicio.trim().toUpperCase(Locale.ROOT);
  }

  // Getters/Setters
  public Long getIdDisponibilidad() { return idDisponibilidad; }
  public void setIdDisponibilidad(Long idDisponibilidad) { this.idDisponibilidad = idDisponibilidad; }
  public String getDia() { return dia; }
  public void setDia(String dia) { this.dia = dia; }
  public Date getHoraInicio() { return horaInicio; }
  public void setHoraInicio(Date horaInicio) { this.horaInicio = horaInicio; }
  public Date getHoraFin() { return horaFin; }
  public void setHoraFin(Date horaFin) { this.horaFin = horaFin; }
  public String getTipoServicio() { return tipoServicio; }
  public void setTipoServicio(String tipoServicio) { this.tipoServicio = tipoServicio; }
  public Vehiculo getVehiculo() { return vehiculo; }
  public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }
  public UsuarioConductor getUsuarioConductor() { return usuarioConductor; }
  public void setUsuarioConductor(UsuarioConductor usuarioConductor) { this.usuarioConductor = usuarioConductor; }
}
