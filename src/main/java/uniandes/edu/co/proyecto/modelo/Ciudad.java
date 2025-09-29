package uniandes.edu.co.proyecto.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "CIUDAD")
public class Ciudad {
    @Id
    @Column(name = "ID_CIUDAD")
    private Long idCiudad;

    @Column(name = "NOMBRE", nullable = false, length = 250)
    private String nombre;

    public Ciudad() {}
    public Ciudad(Long idCiudad, String nombre) { this.idCiudad = idCiudad; this.nombre = nombre; }

    public Long getIdCiudad() { return idCiudad; }
    public void setIdCiudad(Long idCiudad) { this.idCiudad = idCiudad; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}