package com.tijetravel.backend.dto;

import com.tijetravel.backend.model.ClaseVuelo;
import com.tijetravel.backend.model.TipoHospedaje;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CrearReservaDTO {

    @NotBlank(message = "El DNI del turista es obligatorio")
    private String turistaDni;

    @NotBlank(message = "El numero de vuelo es obligatorio")
    private String vueloNumero;

    @NotBlank(message = "El codigo de hotel es obligatorio")
    private String hotelCodigo;

    @NotNull(message = "La clase de vuelo es obligatoria")
    private ClaseVuelo claseVuelo;

    @NotNull(message = "El tipo de hospedaje es obligatorio")
    private TipoHospedaje tipoHospedaje;

    @NotNull(message = "La fecha de llegada es obligatoria")
    private LocalDate fechaLlegada;

    @NotNull(message = "La fecha de partida es obligatoria")
    private LocalDate fechaPartida;

    public CrearReservaDTO() {
    }

    public String getTuristaDni() {
        return turistaDni;
    }

    public void setTuristaDni(String turistaDni) {
        this.turistaDni = turistaDni;
    }

    public String getVueloNumero() {
        return vueloNumero;
    }

    public void setVueloNumero(String vueloNumero) {
        this.vueloNumero = vueloNumero;
    }

    public String getHotelCodigo() {
        return hotelCodigo;
    }

    public void setHotelCodigo(String hotelCodigo) {
        this.hotelCodigo = hotelCodigo;
    }

    public ClaseVuelo getClaseVuelo() {
        return claseVuelo;
    }

    public void setClaseVuelo(ClaseVuelo claseVuelo) {
        this.claseVuelo = claseVuelo;
    }

    public TipoHospedaje getTipoHospedaje() {
        return tipoHospedaje;
    }

    public void setTipoHospedaje(TipoHospedaje tipoHospedaje) {
        this.tipoHospedaje = tipoHospedaje;
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
}

