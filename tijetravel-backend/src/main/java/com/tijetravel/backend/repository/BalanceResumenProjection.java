package com.tijetravel.backend.repository;

import java.math.BigDecimal;

public interface BalanceResumenProjection {
    Long getCantidad();
    BigDecimal getTotal();
    BigDecimal getTotalVuelo();
    BigDecimal getTotalHotel();
}

