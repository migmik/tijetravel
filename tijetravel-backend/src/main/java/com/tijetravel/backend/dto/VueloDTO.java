package com.tijetravel.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VueloDTO {

    private Long id;

    @NotBlank(message = "El numero de vuelo es obligatorio")
    private String numero;

    @NotNull(message = "La fecha y hora del vuelo es obligatoria")
    private LocalDateTime fechaHora;

    @NotBlank(message = "El origen es obligatorio")
    private String origen;

    @NotBlank(message = "El destino es obligatorio")
    private String destino;

    @NotNull(message = "Los asientos totales son obligatorios")
    @Min(value = 1, message = "Los asientos totales deben ser mayor a cero")
    private Integer asientosTotales;

    @NotNull(message = "El cupo turista es obligatorio")
    @Min(value = 0, message = "El cupo turista no puede ser negativo")
    private Integer cupoTurista;

    @NotNull(message = "El cupo primera es obligatorio")
    @Min(value = 0, message = "El cupo primera no puede ser negativo")
    private Integer cupoPrimera;

    @NotNull(message = "El precio base es obligatorio")
    @DecimalMin(value = "150000.00", message = "El precio base debe ser minimo $150.000")
    @DecimalMax(value = "300000.00", message = "El precio base no puede superar $300.000")
    private BigDecimal precioBase;

    private Long plazasTuristaDisponibles;
    private Long plazasPrimeraDisponibles;

    public VueloDTO() {
    }

    public VueloDTO(Long id, String numero, LocalDateTime fechaHora, String origen, String destino, Integer asientosTotales, Integer cupoTurista, Integer cupoPrimera, BigDecimal precioBase, Long plazasTuristaDisponibles, Long plazasPrimeraDisponibles) {
        this.id = id;
        this.numero = numero;
        this.fechaHora = fechaHora;
        this.origen = origen;
        this.destino = destino;
        this.asientosTotales = asientosTotales;
        this.cupoTurista = cupoTurista;
        this.cupoPrimera = cupoPrimera;
        this.precioBase = precioBase;
        this.plazasTuristaDisponibles = plazasTuristaDisponibles;
        this.plazasPrimeraDisponibles = plazasPrimeraDisponibles;
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

