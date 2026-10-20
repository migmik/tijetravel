package com.tijetravel.backend.controller;

import com.tijetravel.backend.dto.VueloDTO;
import com.tijetravel.backend.service.VueloService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/vuelos")
public class VueloController {

    private final VueloService vueloService;

    public VueloController(VueloService vueloService) {
        this.vueloService = vueloService;
    }

    @GetMapping
    public ResponseEntity<Page<VueloDTO>> buscar(
            @RequestParam(name = "origen", required = false) String origen,
            @RequestParam(name = "destino", required = false) String destino,
            @RequestParam(name = "desde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(name = "hasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "orden", defaultValue = "ASC") String orden) {

        Page<VueloDTO> resultado = vueloService.buscarVuelos(origen, destino, desde, hasta, page, size, orden);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VueloDTO> obtenerPorId(@PathVariable("id") Long id) {
        VueloDTO vuelo = vueloService.obtenerPorId(id);
        return ResponseEntity.ok(vuelo);
    }

    @GetMapping("/numero/{numero}")
    public ResponseEntity<VueloDTO> obtenerPorNumero(@PathVariable("numero") String numero) {
        VueloDTO vuelo = vueloService.obtenerPorNumero(numero);
        return ResponseEntity.ok(vuelo);
    }

    @PostMapping
    public ResponseEntity<VueloDTO> crear(@Valid @RequestBody VueloDTO dto) {
        VueloDTO creado = vueloService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VueloDTO> actualizar(@PathVariable("id") Long id, @Valid @RequestBody VueloDTO dto) {
        VueloDTO actualizado = vueloService.actualizar(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        vueloService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

