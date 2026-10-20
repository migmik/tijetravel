package com.tijetravel.backend.controller;

import com.tijetravel.backend.dto.BusquedaViajeRequestDTO;
import com.tijetravel.backend.dto.BusquedaViajeResponseDTO;
import com.tijetravel.backend.dto.SucursalDTO;
import com.tijetravel.backend.service.BusquedaService;
import com.tijetravel.backend.service.HotelService;
import com.tijetravel.backend.service.SucursalService;
import com.tijetravel.backend.service.VueloService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/public")
public class PublicCatalogController {

    private final BusquedaService busquedaService;
    private final VueloService vueloService;
    private final HotelService hotelService;
    private final SucursalService sucursalService;

    public PublicCatalogController(BusquedaService busquedaService,
                                   VueloService vueloService,
                                   HotelService hotelService,
                                   SucursalService sucursalService) {
        this.busquedaService = busquedaService;
        this.vueloService = vueloService;
        this.hotelService = hotelService;
        this.sucursalService = sucursalService;
    }

    @PostMapping("/buscar")
    public ResponseEntity<BusquedaViajeResponseDTO> buscar(@Valid @RequestBody BusquedaViajeRequestDTO request) {
        BusquedaViajeResponseDTO resultado = busquedaService.buscarViajes(request);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/buscar")
    public ResponseEntity<BusquedaViajeResponseDTO> buscarGet(
            @RequestParam(name = "origen") String origen,
            @RequestParam(name = "destino") String destino,
            @RequestParam(name = "fechaLlegada") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaLlegada,
            @RequestParam(name = "fechaPartida") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaPartida,
            @RequestParam(name = "personas", defaultValue = "1") Integer personas,
            @RequestParam(name = "ordenPrecio", defaultValue = "ASC") String ordenPrecio) {

        BusquedaViajeRequestDTO request = new BusquedaViajeRequestDTO();
        request.setOrigen(origen);
        request.setDestino(destino);
        request.setFechaLlegada(fechaLlegada);
        request.setFechaPartida(fechaPartida);
        request.setPersonas(personas);
        request.setOrdenPrecio(ordenPrecio);

        BusquedaViajeResponseDTO resultado = busquedaService.buscarViajes(request);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/ciudades-origen")
    public ResponseEntity<List<String>> obtenerOrigenes() {
        return ResponseEntity.ok(vueloService.obtenerOrigenes());
    }

    @GetMapping("/ciudades-destino")
    public ResponseEntity<List<String>> obtenerDestinos() {
        return ResponseEntity.ok(vueloService.obtenerDestinos());
    }

    @GetMapping("/ciudades-hoteles")
    public ResponseEntity<List<String>> obtenerCiudadesHoteles() {
        return ResponseEntity.ok(hotelService.obtenerCiudades());
    }

    @GetMapping("/nombres-hoteles")
    public ResponseEntity<List<String>> obtenerNombresHoteles() {
        return ResponseEntity.ok(hotelService.obtenerNombres());
    }

    @GetMapping("/ciudades-sucursales")
    public ResponseEntity<List<String>> obtenerCiudadesSucursales() {
        return ResponseEntity.ok(sucursalService.obtenerCiudades());
    }

    @GetMapping("/sucursales")
    public ResponseEntity<List<SucursalDTO>> obtenerSucursales() {
        return ResponseEntity.ok(sucursalService.obtenerTodas());
    }
}

