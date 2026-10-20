package com.tijetravel.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class HotelDTO {

    private Long id;

    @NotBlank(message = "El codigo es obligatorio")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;

    @NotBlank(message = "La direccion es obligatoria")
    private String direccion;

    @NotBlank(message = "El telefono es obligatorio")
    private String telefono;

    @NotNull(message = "La capacidad total es obligatoria")
    @Min(value = 1, message = "La capacidad total debe ser mayor a cero")
    private Integer capacidadTotal;

    @NotNull(message = "El precio base por noche es obligatorio")
    @DecimalMin(value = "25000.00", message = "El precio base debe ser minimo $25.000")
    @DecimalMax(value = "55000.00", message = "El precio base no puede superar $55.000")
    private BigDecimal precioBaseNoche;

    private Long plazasDisponibles;

    public HotelDTO() {
    }

    public HotelDTO(Long id, String codigo, String nombre, String ciudad, String direccion, String telefono, Integer capacidadTotal, BigDecimal precioBaseNoche, Long plazasDisponibles) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.telefono = telefono;
        this.capacidadTotal = capacidadTotal;
        this.precioBaseNoche = precioBaseNoche;
        this.plazasDisponibles = plazasDisponibles;
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

