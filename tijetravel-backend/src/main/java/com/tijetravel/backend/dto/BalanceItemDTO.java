package com.tijetravel.backend.dto;

import java.math.BigDecimal;

public class BalanceItemDTO {

    private long cantidad;
    private BigDecimal total;
    private BigDecimal totalVuelos;
    private BigDecimal totalHoteles;

    public BalanceItemDTO() {
        this.cantidad = 0;
        this.total = BigDecimal.ZERO;
        this.totalVuelos = BigDecimal.ZERO;
        this.totalHoteles = BigDecimal.ZERO;
    }

    public BalanceItemDTO(long cantidad, BigDecimal total, BigDecimal totalVuelos, BigDecimal totalHoteles) {
        this.cantidad = cantidad;
        this.total = total != null ? total : BigDecimal.ZERO;
        this.totalVuelos = totalVuelos != null ? totalVuelos : BigDecimal.ZERO;
        this.totalHoteles = totalHoteles != null ? totalHoteles : BigDecimal.ZERO;
    }

    public long getCantidad() {
        return cantidad;
    }

    public void setCantidad(long cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotalVuelos() {
        return totalVuelos;
    }

    public void setTotalVuelos(BigDecimal totalVuelos) {
        this.totalVuelos = totalVuelos;
    }

    public BigDecimal getTotalHoteles() {
        return totalHoteles;
    }

    public void setTotalHoteles(BigDecimal totalHoteles) {
        this.totalHoteles = totalHoteles;
    }
}

