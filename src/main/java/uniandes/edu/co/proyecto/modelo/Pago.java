package uniandes.edu.co.proyecto.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "PAGO")
public class Pago {
    @Id
    @Column(name = "ID_PAGO")
    private Long id;

    @Column(name = "MONTO", nullable = false)
    private Double monto;

    @Column(name = "FECHA", nullable = false)
    private LocalDate fecha;

    @Column(name = "ESTADO", nullable = false, length = 50)
    private String estado;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_VIAJE", nullable = false, unique = true)
    private Viaje viaje;

    public Pago() {}
    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Viaje getViaje() { return viaje; }
    public void setViaje(Viaje viaje) { this.viaje = viaje; }
}
