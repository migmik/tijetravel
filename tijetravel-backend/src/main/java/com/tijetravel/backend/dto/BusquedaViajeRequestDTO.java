package com.tijetravel.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class BusquedaViajeRequestDTO {

    @NotBlank(message = "El origen es obligatorio")
    private String origen;

    @NotBlank(message = "El destino es obligatorio")
    private String destino;

    @NotNull(message = "La fecha de llegada es obligatoria")
    private LocalDate fechaLlegada;

    @NotNull(message = "La fecha de salida es obligatoria")
    private LocalDate fechaPartida;

    @NotNull(message = "La cantidad de personas es obligatoria")
    @Min(value = 1, message = "Minimo 1 persona")
    @Max(value = 20, message = "Maximo 20 personas")
    private Integer personas;

    private String ordenPrecio; // "ASC" o "DESC", por defecto "ASC"

    public BusquedaViajeRequestDTO() {
        this.personas = 1;
        this.ordenPrecio = "ASC";
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

