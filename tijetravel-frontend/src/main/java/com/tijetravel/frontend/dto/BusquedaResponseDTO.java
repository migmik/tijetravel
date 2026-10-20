package com.tijetravel.frontend.dto;

import java.util.ArrayList;
import java.util.List;

public class BusquedaResponseDTO {

    private List<PaqueteViajeDTO> paquetes = new ArrayList<>();
    private List<VueloDTO> vuelos = new ArrayList<>();
    private List<HotelDTO> hoteles = new ArrayList<>();
    private List<VueloDTO> sugerenciasVuelos = new ArrayList<>();

    public BusquedaResponseDTO() {
    }

    public List<PaqueteViajeDTO> getPaquetes() {
        return paquetes;
    }

    public void setPaquetes(List<PaqueteViajeDTO> paquetes) {
        this.paquetes = paquetes;
    }

    public List<VueloDTO> getVuelos() {
        return vuelos;
    }

    public void setVuelos(List<VueloDTO> vuelos) {
        this.vuelos = vuelos;
    }

    public List<HotelDTO> getHoteles() {
        return hoteles;
    }

    public void setHoteles(List<HotelDTO> hoteles) {
        this.hoteles = hoteles;
    }

    public List<VueloDTO> getSugerenciasVuelos() {
        return sugerenciasVuelos;
    }

    public void setSugerenciasVuelos(List<VueloDTO> sugerenciasVuelos) {
        this.sugerenciasVuelos = sugerenciasVuelos;
    }
}

