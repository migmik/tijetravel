package com.tijetravel.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vuelos")
public class Vuelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero", nullable = false, unique = true, length = 30)
    private String numero;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "origen", nullable = false, length = 100)
    private String origen;

    @Column(name = "destino", nullable = false, length = 100)
    private String destino;

    @Column(name = "asientos_totales", nullable = false)
    private Integer asientosTotales;

    @Column(name = "cupo_turista", nullable = false)
    private Integer cupoTurista;

    @Column(name = "cupo_primera", nullable = false)
    private Integer cupoPrimera;

    @Column(name = "precio_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioBase;

    public Vuelo() {
    }

    public Vuelo(Long id, String numero, LocalDateTime fechaHora, String origen, String destino, Integer asientosTotales, Integer cupoTurista, Integer cupoPrimera, BigDecimal precioBase) {
        this.id = id;
        this.numero = numero;
        this.fechaHora = fechaHora;
        this.origen = origen;
        this.destino = destino;
        this.asientosTotales = asientosTotales;
        this.cupoTurista = cupoTurista;
        this.cupoPrimera = cupoPrimera;
        this.precioBase = precioBase;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public Integer getAsientosTotales() {
        return asientosTotales;
    }

    public void setAsientosTotales(Integer asientosTotales) {
        this.asientosTotales = asientosTotales;
    }

    public Integer getCupoTurista() {
        return cupoTurista;
    }

    public void setCupoTurista(Integer cupoTurista) {
        this.cupoTurista = cupoTurista;
    }

    public Integer getCupoPrimera() {
        return cupoPrimera;
    }

    public void setCupoPrimera(Integer cupoPrimera) {
        this.cupoPrimera = cupoPrimera;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }
}

