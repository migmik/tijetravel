package com.tijetravel.backend.controller;

import com.tijetravel.backend.dto.HotelDTO;
import com.tijetravel.backend.service.HotelService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/hoteles")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public ResponseEntity<Page<HotelDTO>> buscar(
            @RequestParam(name = "ciudad", required = false) String ciudad,
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "orden", defaultValue = "ASC") String orden) {

        Page<HotelDTO> resultado = hotelService.buscarHoteles(ciudad, nombre, page, size, orden);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelDTO> obtenerPorId(@PathVariable("id") Long id) {
        HotelDTO hotel = hotelService.obtenerPorId(id);
        return ResponseEntity.ok(hotel);
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<HotelDTO> obtenerPorCodigo(@PathVariable("codigo") String codigo) {
        HotelDTO hotel = hotelService.obtenerPorCodigo(codigo);
        return ResponseEntity.ok(hotel);
    }

    @PostMapping
    public ResponseEntity<HotelDTO> crear(@Valid @RequestBody HotelDTO dto) {
        HotelDTO creado = hotelService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelDTO> actualizar(@PathVariable("id") Long id, @Valid @RequestBody HotelDTO dto) {
        HotelDTO actualizado = hotelService.actualizar(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        hotelService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

