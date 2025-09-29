package uniandes.edu.co.proyecto.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "SOLICITUD_SERVICIO")
public class SolicitudServicio {
    @Id
    @Column(name = "ID_SOLICITUD")
    private Long id;

    @Column(name = "TIPO", nullable = false, length = 250)
    private String tipo;

    @Column(name = "NIVEL", length = 50)
    private String nivel;

    @Column(name = "FECHA", nullable = false)
    private LocalDate fecha;

    @Column(name = "ESTADO", nullable = false, length = 250)
    private String estado;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_USUARIO_SERVICIO", nullable = false)
    private UsuarioServicio usuarioServicio;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_PUNTO_PARTIDA", nullable = false)
    private PuntoGeografico puntoPartida;

    public SolicitudServicio() {}
    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public UsuarioServicio getUsuarioServicio() { return usuarioServicio; }
    public void setUsuarioServicio(UsuarioServicio usuarioServicio) { this.usuarioServicio = usuarioServicio; }
    public PuntoGeografico getPuntoPartida() { return puntoPartida; }
    public void setPuntoPartida(PuntoGeografico puntoPartida) { this.puntoPartida = puntoPartida; }
}