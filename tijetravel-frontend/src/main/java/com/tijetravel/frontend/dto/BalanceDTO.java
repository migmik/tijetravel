package com.tijetravel.frontend.dto;

public class BalanceDTO {

    private BalanceItemDTO pagadas;
    private BalanceItemDTO pendientes;

    public BalanceDTO() {
        this.pagadas = new BalanceItemDTO();
        this.pendientes = new BalanceItemDTO();
    }

    public BalanceItemDTO getPagadas() {
        return pagadas;
    }

    public void setPagadas(BalanceItemDTO pagadas) {
        this.pagadas = pagadas;
    }

    public BalanceItemDTO getPendientes() {
        return pendientes;
    }

    public void setPendientes(BalanceItemDTO pendientes) {
        this.pendientes = pendientes;
    }
}

