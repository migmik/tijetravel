package com.tijetravel.frontend.dto;

import java.time.LocalDate;

public class BusquedaForm {

    private String origen;
    private String destino;
    private LocalDate fechaLlegada;
    private LocalDate fechaPartida;
    private Integer personas = 1;
    private String ordenPrecio = "ASC";

    public BusquedaForm() {
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

    public LocalDate getFechaLlegada() {
        return fechaLlegada;
    }

    public void setFechaLlegada(LocalDate fechaLlegada) {
        this.fechaLlegada = fechaLlegada;
    }

    public LocalDate getFechaPartida() {
        return fechaPartida;
    }

    public void setFechaPartida(LocalDate fechaPartida) {
        this.fechaPartida = fechaPartida;
    }

    public Integer getPersonas() {
        return personas;
    }

    public void setPersonas(Integer personas) {
        this.personas = personas;
    }

    public String getOrdenPrecio() {
        return ordenPrecio;
    }

    public void setOrdenPrecio(String ordenPrecio) {
        this.ordenPrecio = ordenPrecio;
    }
}

