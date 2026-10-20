package com.tijetravel.backend.controller;

import com.tijetravel.backend.dto.CrearReservaDTO;
import com.tijetravel.backend.dto.ReservaDTO;
import com.tijetravel.backend.model.EstadoReserva;
import com.tijetravel.backend.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public ResponseEntity<Page<ReservaDTO>> listar(
            @RequestParam(name = "codigoTurista", required = false) String codigoTurista,
            @RequestParam(name = "dni", required = false) String dni,
            @RequestParam(name = "numeroVuelo", required = false) String numeroVuelo,
            @RequestParam(name = "codigoHotel", required = false) String codigoHotel,
            @RequestParam(name = "fechaDesde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(name = "fechaHasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(name = "estado", required = false) EstadoReserva estado,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Authentication auth) {

        Page<ReservaDTO> reservas = reservaService.listarReservas(
                codigoTurista, dni, numeroVuelo, codigoHotel, fechaDesde, fechaHasta, estado, page, size, auth.getName()
        );
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> obtenerPorId(@PathVariable("id") Long id, Authentication auth) {
        ReservaDTO reserva = reservaService.obtenerPorId(id, auth.getName());
        return ResponseEntity.ok(reserva);
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ReservaDTO> obtenerPorCodigo(@PathVariable("codigo") String codigo, Authentication auth) {
        ReservaDTO reserva = reservaService.obtenerPorCodigo(codigo, auth.getName());
        return ResponseEntity.ok(reserva);
    }

    @PostMapping
    public ResponseEntity<ReservaDTO> crear(@Valid @RequestBody CrearReservaDTO dto, Authentication auth) {
        ReservaDTO creada = reservaService.crearReserva(dto, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PostMapping("/{id}/pago")
    public ResponseEntity<ReservaDTO> procesarPago(@PathVariable("id") Long id, Authentication auth) {
        ReservaDTO pagada = reservaService.procesarPago(id, auth.getName());
        return ResponseEntity.ok(pagada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaDTO> modificarPendiente(@PathVariable("id") Long id,
                                                        @Valid @RequestBody CrearReservaDTO dto,
                                                        Authentication auth) {
        ReservaDTO modificada = reservaService.modificarReservaPendiente(id, dto, auth.getName());
        return ResponseEntity.ok(modificada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPendiente(@PathVariable("id") Long id, Authentication auth) {
        reservaService.eliminarReservaPendiente(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}

