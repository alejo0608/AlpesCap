package uniandes.edu.co.proyecto.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "RESENIA")
public class Resenia {
    @Id
    @Column(name = "ID_RESENIA")
    private Long id;

    @Column(name = "CALIFICACION", nullable = false)
    private Integer calificacion;

    @Column(name = "COMENTARIO", length = 1000)
    private String comentario;

    @Column(name = "FECHA", nullable = false)
    private LocalDate fecha;

    @Column(name = "AUTOR_ROL", nullable = false, length = 20)
    private String autorRol; // 'USR' o 'COND'

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_VIAJE", nullable = false)
    private Viaje viaje;

    public Resenia() {}
    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getCalificacion() { return calificacion; }
    public void setCalificacion(Integer calificacion) { this.calificacion = calificacion; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getAutorRol() { return autorRol; }
    public void setAutorRol(String autorRol) { this.autorRol = autorRol; }
    public Viaje getViaje() { return viaje; }
    public void setViaje(Viaje viaje) { this.viaje = viaje; }
}