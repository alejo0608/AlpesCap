// src/main/java/uniandes/edu/co/proyecto/modelo/UsuarioServicio.java
package uniandes.edu.co.proyecto.modelo;

import jakarta.persistence.*;

@Entity
@Table(
    name = "USUARIO_SERVICIO",
    uniqueConstraints = {
        @UniqueConstraint(name = "UQ_USR_SERV_CORREO", columnNames = "CORREO"),
        @UniqueConstraint(name = "UQ_USR_SERV_CEDULA", columnNames = "CEDULA")
    }
)
public class UsuarioServicio {

  @Id
  @Column(name = "ID_USUARIO_SERVICIO")
  private Long idUsuarioServicio;  // Se envía desde el cliente (sin @GeneratedValue)

  @Column(name = "NOMBRE", nullable = false, length = 250)
  private String nombre;

  @Column(name = "CORREO", nullable = false, length = 250)
  private String correo;

  @Column(name = "TELEFONO", nullable = false, length = 30)
  private String telefono;

  @Column(name = "CEDULA", nullable = false, length = 50)
  private String cedula;

  public UsuarioServicio() {}

  public Long getIdUsuarioServicio() { return idUsuarioServicio; }
  public void setIdUsuarioServicio(Long idUsuarioServicio) { this.idUsuarioServicio = idUsuarioServicio; }

  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }

  public String getCorreo() { return correo; }
  public void setCorreo(String correo) { this.correo = correo; }

  public String getTelefono() { return telefono; }
  public void setTelefono(String telefono) { this.telefono = telefono; }

  public String getCedula() { return cedula; }
  public void setCedula(String cedula) { this.cedula = cedula; }
}
