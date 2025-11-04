// src/main/java/uniandes/edu/co/proyecto/modelo/Viaje.java
package uniandes.edu.co.proyecto.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "VIAJE")
public class Viaje {

  @Id
  @Column(name = "ID_VIAJE")
  private Long idViaje; // Provisto por el cliente

  @Column(name = "FECHA_ASIGNACION", nullable = false)
  private LocalDate fechaAsignacion;

  @Column(name = "HORA_INICIO", nullable = false)
  private LocalDateTime horaInicio;

  // Si tu columna en BD es NOT NULL, asegúrate de ponerle valor al crear (RF8) y actualizar en RF9.
  @Column(name = "HORA_FIN")
  private LocalDateTime horaFin;

  @Column(name = "DISTANCIA_KM")
  private Double distanciaKm;

  @Column(name = "COSTO_TOTAL")
  private Double costoTotal;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "ID_USUARIO_CONDUCTOR", nullable = false)
  private UsuarioConductor usuarioConductor;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "ID_VEHICULO", nullable = false)
  private Vehiculo vehiculo;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "ID_PUNTO_PARTIDA", nullable = false)
  private PuntoGeografico puntoPartida;

  // Relación 1:1 lógica con Solicitud (en BD suele haber UNIQUE en ID_SOLICITUD)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ID_SOLICITUD")
  private SolicitudServicio solicitud;

  public Viaje() {}

  public Long getIdViaje() { return idViaje; }
  public void setIdViaje(Long idViaje) { this.idViaje = idViaje; }

  public LocalDate getFechaAsignacion() { return fechaAsignacion; }
  public void setFechaAsignacion(LocalDate fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

  public LocalDateTime getHoraInicio() { return horaInicio; }
  public void setHoraInicio(LocalDateTime horaInicio) { this.horaInicio = horaInicio; }

  public LocalDateTime getHoraFin() { return horaFin; }
  public void setHoraFin(LocalDateTime horaFin) { this.horaFin = horaFin; }

  public Double getDistanciaKm() { return distanciaKm; }
  public void setDistanciaKm(Double distanciaKm) { this.distanciaKm = distanciaKm; }

  public Double getCostoTotal() { return costoTotal; }
  public void setCostoTotal(Double costoTotal) { this.costoTotal = costoTotal; }

  public UsuarioConductor getUsuarioConductor() { return usuarioConductor; }
  public void setUsuarioConductor(UsuarioConductor usuarioConductor) { this.usuarioConductor = usuarioConductor; }

  public Vehiculo getVehiculo() { return vehiculo; }
  public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }

  public PuntoGeografico getPuntoPartida() { return puntoPartida; }
  public void setPuntoPartida(PuntoGeografico puntoPartida) { this.puntoPartida = puntoPartida; }

  public SolicitudServicio getSolicitud() { return solicitud; }
  public void setSolicitud(SolicitudServicio solicitud) { this.solicitud = solicitud; }
}
