package com.tijetravel.backend.dto;

import com.tijetravel.backend.model.ClaseVuelo;
import com.tijetravel.backend.model.EstadoReserva;
import com.tijetravel.backend.model.TipoHospedaje;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservaDTO {

    private Long id;
    private String codigo;

    private Long turistaId;
    private String turistaDni;
    private String turistaNombreCompleto;

    private Long vueloId;
    private String vueloNumero;
    private String vueloOrigen;
    private String vueloDestino;
    private LocalDateTime vueloFechaHora;

    private Long hotelId;
    private String hotelCodigo;
    private String hotelNombre;
    private String hotelCiudad;

    private String sucursalContratacion;
    private ClaseVuelo claseVuelo;
    private TipoHospedaje tipoHospedaje;
    private LocalDate fechaLlegada;
    private LocalDate fechaPartida;
    private long noches;

    private BigDecimal precioVueloCapturado;
    private BigDecimal precioHotelCapturado;
    private BigDecimal precioTotal;

    private EstadoReserva estado;
    private LocalDateTime fechaPago;

    public ReservaDTO() {
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

    public Long getTuristaId() {
        return turistaId;
    }

    public void setTuristaId(Long turistaId) {
        this.turistaId = turistaId;
    }

    public String getTuristaDni() {
        return turistaDni;
    }

    public void setTuristaDni(String turistaDni) {
        this.turistaDni = turistaDni;
    }

    public String getTuristaNombreCompleto() {
        return turistaNombreCompleto;
    }

    public void setTuristaNombreCompleto(String turistaNombreCompleto) {
        this.turistaNombreCompleto = turistaNombreCompleto;
    }

    public Long getVueloId() {
        return vueloId;
    }

    public void setVueloId(Long vueloId) {
        this.vueloId = vueloId;
    }

    public String getVueloNumero() {
        return vueloNumero;
    }

    public void setVueloNumero(String vueloNumero) {
        this.vueloNumero = vueloNumero;
    }

    public String getVueloOrigen() {
        return vueloOrigen;
    }

    public void setVueloOrigen(String vueloOrigen) {
        this.vueloOrigen = vueloOrigen;
    }

    public String getVueloDestino() {
        return vueloDestino;
    }

    public void setVueloDestino(String vueloDestino) {
        this.vueloDestino = vueloDestino;
    }

    public LocalDateTime getVueloFechaHora() {
        return vueloFechaHora;
    }

    public void setVueloFechaHora(LocalDateTime vueloFechaHora) {
        this.vueloFechaHora = vueloFechaHora;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public String getHotelCodigo() {
        return hotelCodigo;
    }

    public void setHotelCodigo(String hotelCodigo) {
        this.hotelCodigo = hotelCodigo;
    }

    public String getHotelNombre() {
        return hotelNombre;
    }

    public void setHotelNombre(String hotelNombre) {
        this.hotelNombre = hotelNombre;
    }

    public String getHotelCiudad() {
        return hotelCiudad;
    }

    public void setHotelCiudad(String hotelCiudad) {
        this.hotelCiudad = hotelCiudad;
    }

    public String getSucursalContratacion() {
        return sucursalContratacion;
    }

    public void setSucursalContratacion(String sucursalContratacion) {
        this.sucursalContratacion = sucursalContratacion;
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

    public long getNoches() {
        return noches;
    }

    public void setNoches(long noches) {
        this.noches = noches;
    }

    public BigDecimal getPrecioVueloCapturado() {
        return precioVueloCapturado;
    }

    public void setPrecioVueloCapturado(BigDecimal precioVueloCapturado) {
        this.precioVueloCapturado = precioVueloCapturado;
    }

    public BigDecimal getPrecioHotelCapturado() {
        return precioHotelCapturado;
    }

    public void setPrecioHotelCapturado(BigDecimal precioHotelCapturado) {
        this.precioHotelCapturado = precioHotelCapturado;
    }

    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }
}

