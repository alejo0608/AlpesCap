package uniandes.edu.co.proyecto.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "DISPONIBILIDAD")
public class Disponibilidad {
    @Id
    @Column(name = "ID_DISPONIBILIDAD")
    private Long id;

    @Column(name = "DIA", nullable = false, length = 50)
    private String dia;

    @Column(name = "HORA_INICIO", nullable = false)
    private LocalDateTime horaInicio;

    @Column(name = "HORA_FIN", nullable = false)
    private LocalDateTime horaFin;

    @Column(name = "TIPO_SERVICIO", nullable = false, length = 50)
    private String tipoServicio;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_VEHICULO", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_USUARIO_CONDUCTOR", nullable = false)
    private UsuarioConductor conductor;

    public Disponibilidad() {}
    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDia() { return dia; }
    public void setDia(String dia) { this.dia = dia; }
    public LocalDateTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalDateTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalDateTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalDateTime horaFin) { this.horaFin = horaFin; }
    public String getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(String tipoServicio) { this.tipoServicio = tipoServicio; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }
    public UsuarioConductor getConductor() { return conductor; }
    public void setConductor(UsuarioConductor conductor) { this.conductor = conductor; }
}