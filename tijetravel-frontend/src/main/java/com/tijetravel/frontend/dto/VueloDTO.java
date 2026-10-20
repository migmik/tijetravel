package com.tijetravel.frontend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VueloDTO {

    private Long id;
    private String numero;
    private LocalDateTime fechaHora;
    private String origen;
    private String destino;
    private Integer asientosTotales;
    private Integer cupoTurista;
    private Integer cupoPrimera;
    private BigDecimal precioBase;
    private Long plazasTuristaDisponibles;
    private Long plazasPrimeraDisponibles;

    public VueloDTO() {
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

    public Long getPlazasTuristaDisponibles() {
        return plazasTuristaDisponibles;
    }

    public void setPlazasTuristaDisponibles(Long plazasTuristaDisponibles) {
        this.plazasTuristaDisponibles = plazasTuristaDisponibles;
    }

    public Long getPlazasPrimeraDisponibles() {
        return plazasPrimeraDisponibles;
    }

    public void setPlazasPrimeraDisponibles(Long plazasPrimeraDisponibles) {
        this.plazasPrimeraDisponibles = plazasPrimeraDisponibles;
    }
}

