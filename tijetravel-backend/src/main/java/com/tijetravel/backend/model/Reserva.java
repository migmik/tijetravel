package com.tijetravel.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas", uniqueConstraints = {
    @UniqueConstraint(name = "uk_reserva_turista_vuelo", columnNames = {"turista_id", "vuelo_id"})
})
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "turista_id", nullable = false)
    private Turista turista;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "vuelo_id", nullable = false)
    private Vuelo vuelo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "sucursal_contratacion", nullable = false, length = 255)
    private String sucursalContratacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "clase_vuelo", nullable = false, length = 30)
    private ClaseVuelo claseVuelo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_hospedaje", nullable = false, length = 30)
    private TipoHospedaje tipoHospedaje;

    @Column(name = "fecha_llegada", nullable = false)
    private LocalDate fechaLlegada;

    @Column(name = "fecha_partida", nullable = false)
    private LocalDate fechaPartida;

    @Column(name = "precio_vuelo_capturado", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioVueloCapturado;

    @Column(name = "precio_hotel_capturado", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioHotelCapturado;

    @Column(name = "precio_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoReserva estado;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    public Reserva() {
    }

    public Reserva(Long id, String codigo, Turista turista, Vuelo vuelo, Hotel hotel, String sucursalContratacion, ClaseVuelo claseVuelo, TipoHospedaje tipoHospedaje, LocalDate fechaLlegada, LocalDate fechaPartida, BigDecimal precioVueloCapturado, BigDecimal precioHotelCapturado, BigDecimal precioTotal, EstadoReserva estado, LocalDateTime fechaPago) {
        this.id = id;
        this.codigo = codigo;
        this.turista = turista;
        this.vuelo = vuelo;
        this.hotel = hotel;
        this.sucursalContratacion = sucursalContratacion;
        this.claseVuelo = claseVuelo;
        this.tipoHospedaje = tipoHospedaje;
        this.fechaLlegada = fechaLlegada;
        this.fechaPartida = fechaPartida;
        this.precioVueloCapturado = precioVueloCapturado;
        this.precioHotelCapturado = precioHotelCapturado;
        this.precioTotal = precioTotal;
        this.estado = estado;
        this.fechaPago = fechaPago;
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

    public Turista getTurista() {
        return turista;
    }

    public void setTurista(Turista turista) {
        this.turista = turista;
    }

    public Vuelo getVuelo() {
        return vuelo;
    }

    public void setVuelo(Vuelo vuelo) {
        this.vuelo = vuelo;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
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

