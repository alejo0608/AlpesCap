package uniandes.edu.co.proyecto.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "CIUDAD")
public class Ciudad {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CIUDAD_SEQ")
  @SequenceGenerator(name = "CIUDAD_SEQ", sequenceName = "CIUDAD_SEQ", allocationSize = 1)
  @Column(name = "ID_CIUDAD")
  private Long idCiudad;

  @Column(name = "NOMBRE", nullable = false, unique = true, length = 250)
  private String nombre;

  public Long getIdCiudad() { return idCiudad; }
  public void setIdCiudad(Long idCiudad) { this.idCiudad = idCiudad; }
  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }
}
