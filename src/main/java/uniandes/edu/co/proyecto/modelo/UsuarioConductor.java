package uniandes.edu.co.proyecto.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "USUARIO_CONDUCTOR")
public class UsuarioConductor {
    @Id
    @Column(name = "ID_USUARIO_CONDUCTOR")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 250)
    private String nombre;

    @Column(name = "CORREO", nullable = false, length = 250, unique = true)
    private String correo;

    @Column(name = "TELEFONO", nullable = false, length = 50)
    private String telefono;

    @Column(name = "CEDULA", nullable = false, length = 50, unique = true)
    private String cedula;

    @Column(name = "COMISION", nullable = false)
    private Double comision; // 0..1

    public UsuarioConductor() {}
    public UsuarioConductor(Long id, String nombre, String correo, String telefono, String cedula, Double comision) {
        this.id = id; this.nombre = nombre; this.correo = correo; this.telefono = telefono; this.cedula = cedula; this.comision = comision;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
}