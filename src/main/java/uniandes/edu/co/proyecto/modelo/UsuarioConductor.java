// src/main/java/uniandes/edu/co/proyecto/modelo/UsuarioConductor.java
package uniandes.edu.co.proyecto.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.Check;

import java.util.List;
import java.util.Locale;

@Entity
@Table(
    name = "USUARIO_CONDUCTOR",
    uniqueConstraints = {
        @UniqueConstraint(name = "UQ_USRCOND_CORREO", columnNames = "CORREO"),
        @UniqueConstraint(name = "UQ_USRCOND_CEDULA", columnNames = "CEDULA")
    }
)
@Check(constraints = "COMISION BETWEEN 0 AND 1")
public class UsuarioConductor {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USUARIO_CONDUCTOR_SEQ")
  @SequenceGenerator(name = "USUARIO_CONDUCTOR_SEQ", sequenceName = "USUARIO_CONDUCTOR_SEQ", allocationSize = 1)
  @Column(name = "ID_USUARIO_CONDUCTOR")
  private Long idUsuarioConductor;

  @NotBlank
  @Size(max = 250)
  @Column(name = "NOMBRE", nullable = false, length = 250)
  private String nombre;

  @NotBlank
  @Email
  @Size(max = 250)
  @Column(name = "CORREO", nullable = false, length = 250)
  private String correo;

  @NotBlank
  @Size(max = 30)
  @Column(name = "TELEFONO", nullable = false, length = 30)
  private String telefono;

  @NotBlank
  @Size(max = 50)
  @Column(name = "CEDULA", nullable = false, length = 50)
  private String cedula;

  @NotNull
  @DecimalMin("0.0") @DecimalMax("1.0")
  @Column(name = "COMISION", nullable = false)
  private Double comision;

  @OneToMany(mappedBy = "usuarioConductor", fetch = FetchType.LAZY)
  @JsonIgnore
  private List<Vehiculo> vehiculos;

  @OneToMany(mappedBy = "usuarioConductor", fetch = FetchType.LAZY)
  @JsonIgnore
  private List<Disponibilidad> disponibilidades;

  @PrePersist
  @PreUpdate
  private void normalize() {
    if (correo != null) correo = correo.trim().toLowerCase(Locale.ROOT);
    if (nombre != null) nombre = nombre.trim();
    if (telefono != null) telefono = telefono.trim();
    if (cedula != null) cedula = cedula.trim();
  }

  // Getters/Setters
  public Long getIdUsuarioConductor() { return idUsuarioConductor; }
  public void setIdUsuarioConductor(Long idUsuarioConductor) { this.idUsuarioConductor = idUsuarioConductor; }
  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }
  public String getCorreo() { return correo; }
  public void setCorreo(String correo) { this.correo = correo; }
  public String getTelefono() { return telefono; }
  public void setTelefono(String telefono) { this.telefono = telefono; }
  public String getCedula() { return cedula; }
  public void setCedula(String cedula) { this.cedula = cedula; }
  public Double getComision() { return comision; }
  public void setComision(Double comision) { this.comision = comision; }
  public List<Vehiculo> getVehiculos() { return vehiculos; }
  public void setVehiculos(List<Vehiculo> vehiculos) { this.vehiculos = vehiculos; }
  public List<Disponibilidad> getDisponibilidades() { return disponibilidades; }
  public void setDisponibilidades(List<Disponibilidad> disponibilidades) { this.disponibilidades = disponibilidades; }
}
