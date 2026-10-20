package com.tijetravel.frontend.dto;

import java.math.BigDecimal;

public class PaqueteViajeDTO {

    private VueloDTO vuelo;
    private HotelDTO hotel;
    private long noches;
    private BigDecimal precioVueloPorPersona;
    private BigDecimal precioHotelPorPersona;
    private BigDecimal precioPorPersona;
    private BigDecimal precioTotalGrupo;

    public PaqueteViajeDTO() {
    }

    public VueloDTO getVuelo() {
        return vuelo;
    }

    public void setVuelo(VueloDTO vuelo) {
        this.vuelo = vuelo;
    }

    public HotelDTO getHotel() {
        return hotel;
    }

    public void setHotel(HotelDTO hotel) {
        this.hotel = hotel;
    }

    public long getNoches() {
        return noches;
    }

    public void setNoches(long noches) {
        this.noches = noches;
    }

    public BigDecimal getPrecioVueloPorPersona() {
        return precioVueloPorPersona;
    }

    public void setPrecioVueloPorPersona(BigDecimal precioVueloPorPersona) {
        this.precioVueloPorPersona = precioVueloPorPersona;
    }

    public BigDecimal getPrecioHotelPorPersona() {
        return precioHotelPorPersona;
    }

    public void setPrecioHotelPorPersona(BigDecimal precioHotelPorPersona) {
        this.precioHotelPorPersona = precioHotelPorPersona;
    }

    public BigDecimal getPrecioPorPersona() {
        return precioPorPersona;
    }

    public void setPrecioPorPersona(BigDecimal precioPorPersona) {
        this.precioPorPersona = precioPorPersona;
    }

    public BigDecimal getPrecioTotalGrupo() {
        return precioTotalGrupo;
    }

    public void setPrecioTotalGrupo(BigDecimal precioTotalGrupo) {
        this.precioTotalGrupo = precioTotalGrupo;
    }
}

