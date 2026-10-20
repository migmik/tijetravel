package com.tijetravel.frontend.dto;

import java.math.BigDecimal;

public class HotelDTO {

    private Long id;
    private String codigo;
    private String nombre;
    private String ciudad;
    private String direccion;
    private String telefono;
    private Integer capacidadTotal;
    private BigDecimal precioBaseNoche;
    private Long plazasDisponibles;

    public HotelDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getCapacidadTotal() {
        return capacidadTotal;
    }

    public void setCapacidadTotal(Integer capacidadTotal) {
        this.capacidadTotal = capacidadTotal;
    }

    public BigDecimal getPrecioBaseNoche() {
        return precioBaseNoche;
    }

    public void setPrecioBaseNoche(BigDecimal precioBaseNoche) {
        this.precioBaseNoche = precioBaseNoche;
    }

    public Long getPlazasDisponibles() {
        return plazasDisponibles;
    }

    public void setPlazasDisponibles(Long plazasDisponibles) {
        this.plazasDisponibles = plazasDisponibles;
    }
}

