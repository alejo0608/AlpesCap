package uniandes.edu.co.proyecto.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "VEHICULO")
public class Vehiculo {
    @Id
    @Column(name = "ID_VEHICULO")
    private Long id;

    @Column(name = "TIPO", nullable = false, length = 250)
    private String tipo;

    @Column(name = "MARCA", nullable = false, length = 250)
    private String marca;

    @Column(name = "MODELO", nullable = false, length = 250)
    private String modelo;

    @Column(name = "COLOR", nullable = false, length = 250)
    private String color;

    @Column(name = "PLACA", nullable = false, length = 50, unique = true)
    private String placa;

    @Column(name = "CAPACIDAD", nullable = false)
    private Integer capacidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_CONDUCTOR", nullable = false)
    @JsonIgnore
    private UsuarioConductor usuarioConductor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_CIUDAD_EXPEDICION", nullable = false)
    private Ciudad ciudadExpedicion;

    public Vehiculo() {
    }

    // getters/setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public UsuarioConductor getConductor() {
        return usuarioConductor;
    }

    public void setConductor(UsuarioConductor conductor) {
        this.usuarioConductor = conductor;
    }

    public Ciudad getCiudadExpedicion() {
        return ciudadExpedicion;
    }

    public void setCiudadExpedicion(Ciudad ciudadExpedicion) {
        this.ciudadExpedicion = ciudadExpedicion;
    }
}