package uniandes.edu.co.proyecto.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "VIAJE")
public class Viaje {
    @Id
    @Column(name = "ID_VIAJE")
    private Long id;

    @Column(name = "FECHA_ASIGNACION", nullable = false)
    private LocalDate fechaAsignacion;

    @Column(name = "HORA_INICIO", nullable = false)
    private LocalDateTime horaInicio;

    @Column(name = "HORA_FIN", nullable = false)
    private LocalDateTime horaFin;

    @Column(name = "DISTANCIA_KM", nullable = false)
    private Double distanciaKm;

    @Column(name = "COSTO_TOTAL", nullable = false)
    private Double costoTotal;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_USUARIO_CONDUCTOR", nullable = false)
    private UsuarioConductor conductor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_VEHICULO", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_PUNTO_PARTIDA", nullable = false)
    private PuntoGeografico puntoPartida;

    @ManyToOne
    @JoinColumn(name = "ID_SOLICITUD")
    private SolicitudServicio solicitud;

    public Viaje() {}
    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public UsuarioConductor getConductor() { return conductor; }
    public void setConductor(UsuarioConductor conductor) { this.conductor = conductor; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }
    public PuntoGeografico getPuntoPartida() { return puntoPartida; }
    public void setPuntoPartida(PuntoGeografico puntoPartida) { this.puntoPartida = puntoPartida; }
    public SolicitudServicio getSolicitud() { return solicitud; }
    public void setSolicitud(SolicitudServicio solicitud) { this.solicitud = solicitud; }
}