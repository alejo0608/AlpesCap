// src/main/java/uniandes/edu/co/proyecto/modelo/SolicitudServicio.java
package uniandes.edu.co.proyecto.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "SOLICITUD_SERVICIO")
public class SolicitudServicio {

  @Id
  @Column(name = "ID_SOLICITUD")
  private Long idSolicitud; // Provisto por el cliente

  @Column(name = "TIPO", nullable = false, length = 30)
  private String tipo; // PASAJEROS | COMIDA | MERCANCÍAS/MERCANCIAS

  @Column(name = "NIVEL", nullable = false, length = 30)
  private String nivel; // ESTANDAR | PREMIUM (u otros definidos)

  @Column(name = "FECHA", nullable = false)
  private LocalDate fecha;

  @Column(name = "ESTADO", nullable = false, length = 30)
  private String estado; // p.ej., CREADA / ASIGNADA / CANCELADA

  // FK: Usuario de servicio (pasajero/cliente)
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "ID_USUARIO_SERVICIO", nullable = false)
  private UsuarioServicio usuarioServicio;

  // FK: Punto de partida
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "ID_PUNTO_PARTIDA", nullable = false)
  private PuntoGeografico puntoPartida;

  // FK: Punto de llegada
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "ID_PUNTO_LLEGADA", nullable = false)
  private PuntoGeografico puntoLlegada;

  public SolicitudServicio() {}

  public Long getIdSolicitud() { return idSolicitud; }
  public void setIdSolicitud(Long idSolicitud) { this.idSolicitud = idSolicitud; }

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

  public PuntoGeografico getPuntoLlegada() { return puntoLlegada; }
  public void setPuntoLlegada(PuntoGeografico puntoLlegada) { this.puntoLlegada = puntoLlegada; }
}
