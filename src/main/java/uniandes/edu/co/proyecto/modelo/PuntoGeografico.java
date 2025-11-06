package uniandes.edu.co.proyecto.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "PUNTO_GEOGRAFICO")
public class PuntoGeografico {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PUNTO_GEOGRAFICO_SEQ")
    @SequenceGenerator(name = "PUNTO_GEOGRAFICO_SEQ", sequenceName = "PUNTO_GEOGRAFICO_SEQ", allocationSize = 1)
    @Column(name = "ID_PUNTO")
    private Long id;

    @Column(name = "NOMBRE", length = 250)
    private String nombre;

    @Column(name = "LATITUD", nullable = false)
    private Double latitud;

    @Column(name = "LONGITUD", nullable = false)
    private Double longitud;

    @Column(name = "DIRECCION", nullable = false, length = 250)
    private String direccion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_CIUDAD", nullable = false)
    private Ciudad ciudad;

    public PuntoGeografico() {
    }

    // getters/setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }
}