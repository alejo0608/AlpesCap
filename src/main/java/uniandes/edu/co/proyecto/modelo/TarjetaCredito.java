package uniandes.edu.co.proyecto.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "TARJETA_CREDITO")
public class TarjetaCredito {
    @Id
    @Column(name = "ID_TARJETA")
    private Long id;

    @Column(name = "NUMERO", nullable = false, unique = true)
    private Long numero;

    @Column(name = "NOMBRE", nullable = false, length = 250)
    private String nombre;

    @Column(name = "MES_VENCIMIENTO", nullable = false)
    private Integer mesVencimiento;

    @Column(name = "ANIO_VENCIMIENTO", nullable = false)
    private Integer anioVencimiento;

    @Column(name = "CODIGO_SEGURIDAD", nullable = false)
    private Integer codigoSeguridad;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_USUARIO_SERVICIO", nullable = false)
    private UsuarioServicio usuarioServicio;

    public TarjetaCredito() {}
    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getNumero() { return numero; }
    public void setNumero(Long numero) { this.numero = numero; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getMesVencimiento() { return mesVencimiento; }
    public void setMesVencimiento(Integer mesVencimiento) { this.mesVencimiento = mesVencimiento; }
    public Integer getAnioVencimiento() { return anioVencimiento; }
    public void setAnioVencimiento(Integer anioVencimiento) { this.anioVencimiento = anioVencimiento; }
    public Integer getCodigoSeguridad() { return codigoSeguridad; }
    public void setCodigoSeguridad(Integer codigoSeguridad) { this.codigoSeguridad = codigoSeguridad; }
    public UsuarioServicio getUsuarioServicio() { return usuarioServicio; }
    public void setUsuarioServicio(UsuarioServicio usuarioServicio) { this.usuarioServicio = usuarioServicio; }
}