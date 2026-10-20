package com.tijetravel.backend.service;

import com.tijetravel.backend.dto.BalanceDTO;
import com.tijetravel.backend.dto.BalanceItemDTO;
import com.tijetravel.backend.model.EstadoReserva;
import com.tijetravel.backend.repository.BalanceResumenProjection;
import com.tijetravel.backend.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BalanceService {

    private final ReservaRepository reservaRepository;

    public BalanceService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @Transactional(readOnly = true)
    public BalanceDTO obtenerBalance() {
        BalanceResumenProjection pagadasProj = reservaRepository.obtenerBalancePorEstado(EstadoReserva.PAGADO);
        BalanceResumenProjection pendientesProj = reservaRepository.obtenerBalancePorEstado(EstadoReserva.PENDIENTE);

        BalanceItemDTO pagadas = new BalanceItemDTO(
                pagadasProj != null && pagadasProj.getCantidad() != null ? pagadasProj.getCantidad() : 0L,
                pagadasProj != null ? pagadasProj.getTotal() : null,
                pagadasProj != null ? pagadasProj.getTotalVuelo() : null,
                pagadasProj != null ? pagadasProj.getTotalHotel() : null
        );

        BalanceItemDTO pendientes = new BalanceItemDTO(
                pendientesProj != null && pendientesProj.getCantidad() != null ? pendientesProj.getCantidad() : 0L,
                pendientesProj != null ? pendientesProj.getTotal() : null,
                pendientesProj != null ? pendientesProj.getTotalVuelo() : null,
                pendientesProj != null ? pendientesProj.getTotalHotel() : null
        );

        return new BalanceDTO(pagadas, pendientes);
    }
}

