package com.tijetravel.frontend.controller;

import com.tijetravel.frontend.dto.AuthUserSession;
import com.tijetravel.frontend.dto.SucursalDTO;
import com.tijetravel.frontend.service.BackendApiClient;
import com.tijetravel.frontend.util.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class CatalogoController {

    private final BackendApiClient apiClient;

    public CatalogoController(BackendApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @GetMapping("/vuelos")
    public String explorarVuelos(
            @RequestParam(name = "origen", required = false) String origen,
            @RequestParam(name = "destino", required = false) String destino,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "orden", defaultValue = "ASC") String orden,
            Authentication auth,
            Model model) {

        try {
            model.addAttribute("ciudadesOrigen", apiClient.obtenerOrigenes());
            model.addAttribute("ciudadesDestino", apiClient.obtenerDestinos());
        } catch (Exception ignored) {}

        model.addAttribute("filtroOrigen", origen);
        model.addAttribute("filtroDestino", destino);
        model.addAttribute("filtroOrden", orden);

        // Si el usuario está autenticado, puede consultar el listado
        if (SecurityUtils.isAuthenticated(auth)) {
            AuthUserSession user = SecurityUtils.getSessionUser(auth);
            try {
                Map<String, Object> pageData = apiClient.buscarVuelos(origen, destino, page, 10, orden, user);
                model.addAttribute("paginaVuelos", pageData);
            } catch (Exception e) {
                model.addAttribute("errorServicio", "Error al cargar vuelos: " + e.getMessage());
            }
        }

        return "vuelos";
    }

    @GetMapping("/hoteles")
    public String explorarHoteles(
            @RequestParam(name = "ciudad", required = false) String ciudad,
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "orden", defaultValue = "ASC") String orden,
            Authentication auth,
            Model model) {

        try {
            model.addAttribute("ciudadesHoteles", apiClient.obtenerCiudadesHoteles());
            // Sugerencias de nombres para datalist HTML sin JavaScript
            model.addAttribute("nombresHoteles", apiClient.obtenerNombresHoteles());
        } catch (Exception ignored) {}

        model.addAttribute("filtroCiudad", ciudad);
        model.addAttribute("filtroNombre", nombre);
        model.addAttribute("filtroOrden", orden);

        if (SecurityUtils.isAuthenticated(auth)) {
            AuthUserSession user = SecurityUtils.getSessionUser(auth);
            try {
                Map<String, Object> pageData = apiClient.buscarHoteles(ciudad, nombre, page, 10, orden, user);
                model.addAttribute("paginaHoteles", pageData);
            } catch (Exception e) {
                model.addAttribute("errorServicio", "Error al cargar hoteles: " + e.getMessage());
            }
        }

        return "hoteles";
    }

    @GetMapping("/sucursales")
    public String explorarSucursales(
            @RequestParam(name = "ciudad", required = false) String ciudad,
            Model model) {

        try {
            model.addAttribute("ciudadesSucursales", apiClient.obtenerCiudadesSucursales());
            List<SucursalDTO> todas = apiClient.obtenerSucursales();

            if (ciudad != null && !ciudad.trim().isEmpty()) {
                List<SucursalDTO> filtradas = new java.util.ArrayList<>();
                for (int i = 0; i < todas.size(); i++) {
                    if (todas.get(i).getCiudad().equalsIgnoreCase(ciudad.trim())) {
                        filtradas.add(todas.get(i));
                    }
                }
                model.addAttribute("sucursales", filtradas);
                model.addAttribute("filtroCiudad", ciudad);
            } else {
                model.addAttribute("sucursales", todas);
            }
        } catch (Exception e) {
            model.addAttribute("errorServicio", "Error al cargar sucursales: " + e.getMessage());
        }

        return "sucursales";
    }
}

