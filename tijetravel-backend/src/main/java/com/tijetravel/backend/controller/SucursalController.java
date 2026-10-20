package com.tijetravel.backend.controller;

import com.tijetravel.backend.dto.SucursalDTO;
import com.tijetravel.backend.service.SucursalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sucursales")
public class SucursalController {

    private final SucursalService sucursalService;

    public SucursalController(SucursalService sucursalService) {
        this.sucursalService = sucursalService;
    }

    @GetMapping
    public ResponseEntity<List<SucursalDTO>> listarTodas() {
        return ResponseEntity.ok(sucursalService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SucursalDTO> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(sucursalService.obtenerPorId(id));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<SucursalDTO> obtenerPorCodigo(@PathVariable("codigo") String codigo) {
        return ResponseEntity.ok(sucursalService.obtenerPorCodigo(codigo));
    }

    @PostMapping
    public ResponseEntity<SucursalDTO> crear(@Valid @RequestBody SucursalDTO dto) {
        SucursalDTO creada = sucursalService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SucursalDTO> actualizar(@PathVariable("id") Long id, @Valid @RequestBody SucursalDTO dto) {
        SucursalDTO actualizada = sucursalService.actualizar(id, dto);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        sucursalService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

