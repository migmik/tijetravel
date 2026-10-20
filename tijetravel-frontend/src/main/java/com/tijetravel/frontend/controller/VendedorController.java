package com.tijetravel.frontend.controller;

import com.tijetravel.frontend.dto.*;
import com.tijetravel.frontend.service.BackendApiClient;
import com.tijetravel.frontend.util.SecurityUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Controller
public class VendedorController {

    private final BackendApiClient apiClient;

    public VendedorController(BackendApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @GetMapping("/reservas-admin")
    public String verTodasLasReservas(
            @RequestParam(name = "codigoTurista", required = false) String codigoTurista,
            @RequestParam(name = "dni", required = false) String dni,
            @RequestParam(name = "numeroVuelo", required = false) String numeroVuelo,
            @RequestParam(name = "codigoHotel", required = false) String codigoHotel,
            @RequestParam(name = "fechaDesde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(name = "fechaHasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(name = "estado", required = false) String estado,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Authentication auth,
            Model model) {

        AuthUserSession user = SecurityUtils.getSessionUser(auth);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            Map<String, Object> pageData = apiClient.listarReservas(
                    codigoTurista, dni, numeroVuelo, codigoHotel, fechaDesde, fechaHasta, estado, page, 15, user
            );
            model.addAttribute("paginaReservas", pageData);
            model.addAttribute("codigoTurista", codigoTurista);
            model.addAttribute("dni", dni);
            model.addAttribute("numeroVuelo", numeroVuelo);
            model.addAttribute("codigoHotel", codigoHotel);
            model.addAttribute("fechaDesde", fechaDesde);
            model.addAttribute("fechaHasta", fechaHasta);
            model.addAttribute("estado", estado);
        } catch (Exception e) {
            model.addAttribute("errorServicio", "Error al cargar reservas: " + e.getMessage());
        }

        return "vendedor_reservas";
    }

    @PostMapping("/reservas-admin/{id}/cancelar")
    public String cancelarReserva(@PathVariable("id") Long id, Authentication auth) {
        AuthUserSession user = SecurityUtils.getSessionUser(auth);
        if (user != null) {
            try {
                apiClient.eliminarReservaPendiente(id, user);
            } catch (Exception ignored) {}
        }
        return "redirect:/reservas-admin";
    }

    @GetMapping("/gestion")
    public String panelGestion(
            @RequestParam(name = "seccion", defaultValue = "vuelos") String seccion,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Authentication auth,
            Model model) {

        AuthUserSession user = SecurityUtils.getSessionUser(auth);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("seccionActiva", seccion);

        try {
            if ("vuelos".equals(seccion)) {
                model.addAttribute("datosPagina", apiClient.buscarVuelos(null, null, page, 10, "ASC", user));
            } else if ("hoteles".equals(seccion)) {
                model.addAttribute("datosPagina", apiClient.buscarHoteles(null, null, page, 10, "ASC", user));
            } else if ("sucursales".equals(seccion)) {
                model.addAttribute("sucursales", apiClient.obtenerSucursales());
            } else if ("turistas".equals(seccion)) {
                model.addAttribute("datosPagina", apiClient.listarTuristas(null, null, null, page, 10, user));
            }
        } catch (Exception e) {
            model.addAttribute("errorServicio", "Error al cargar datos: " + e.getMessage());
        }

        return "vendedor_gestion";
    }

    @PostMapping("/gestion/vuelos/crear")
    public String crearVuelo(
            @RequestParam("numero") String numero,
            @RequestParam("fechaHora") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora,
            @RequestParam("origen") String origen,
            @RequestParam("destino") String destino,
            @RequestParam("asientosTotales") Integer asientosTotales,
            @RequestParam("cupoTurista") Integer cupoTurista,
            @RequestParam("cupoPrimera") Integer cupoPrimera,
            @RequestParam("precioBase") BigDecimal precioBase,
            Authentication auth) {

        AuthUserSession user = SecurityUtils.getSessionUser(auth);
        if (user != null) {
            VueloDTO dto = new VueloDTO();
            dto.setNumero(numero.trim());
            dto.setFechaHora(fechaHora);
            dto.setOrigen(origen.trim());
            dto.setDestino(destino.trim());
            dto.setAsientosTotales(asientosTotales);
            dto.setCupoTurista(cupoTurista);
            dto.setCupoPrimera(cupoPrimera);
            dto.setPrecioBase(precioBase);
            try {
                apiClient.crearVuelo(dto, user);
            } catch (Exception ignored) {}
        }
        return "redirect:/gestion?seccion=vuelos";
    }

    @PostMapping("/gestion/vuelos/{id}/eliminar")
    public String eliminarVuelo(@PathVariable("id") Long id, Authentication auth) {
        AuthUserSession user = SecurityUtils.getSessionUser(auth);
        if (user != null) {
            try {
                apiClient.eliminarVuelo(id, user);
            } catch (Exception ignored) {}
        }
        return "redirect:/gestion?seccion=vuelos";
    }

    @PostMapping("/gestion/hoteles/crear")
    public String crearHotel(
            @RequestParam("codigo") String codigo,
            @RequestParam("nombre") String nombre,
            @RequestParam("ciudad") String ciudad,
            @RequestParam("direccion") String direccion,
            @RequestParam("telefono") String telefono,
            @RequestParam("capacidadTotal") Integer capacidadTotal,
            @RequestParam("precioBaseNoche") BigDecimal precioBaseNoche,
            Authentication auth) {

        AuthUserSession user = SecurityUtils.getSessionUser(auth);
        if (user != null) {
            HotelDTO dto = new HotelDTO();
            dto.setCodigo(codigo.trim());
            dto.setNombre(nombre.trim());
            dto.setCiudad(ciudad.trim());
            dto.setDireccion(direccion.trim());
            dto.setTelefono(telefono.trim());
            dto.setCapacidadTotal(capacidadTotal);
            dto.setPrecioBaseNoche(precioBaseNoche);
            try {
                apiClient.crearHotel(dto, user);
            } catch (Exception ignored) {}
        }
        return "redirect:/gestion?seccion=hoteles";
    }

    @PostMapping("/gestion/hoteles/{id}/eliminar")
    public String eliminarHotel(@PathVariable("id") Long id, Authentication auth) {
        AuthUserSession user = SecurityUtils.getSessionUser(auth);
        if (user != null) {
            try {
                apiClient.eliminarHotel(id, user);
            } catch (Exception ignored) {}
        }
        return "redirect:/gestion?seccion=hoteles";
    }

    @PostMapping("/gestion/sucursales/crear")
    public String crearSucursal(
            @RequestParam("codigo") String codigo,
            @RequestParam("ciudad") String ciudad,
            @RequestParam("direccion") String direccion,
            @RequestParam("telefono") String telefono,
            Authentication auth) {

        AuthUserSession user = SecurityUtils.getSessionUser(auth);
        if (user != null) {
            SucursalDTO dto = new SucursalDTO();
            dto.setCodigo(codigo.trim());
            dto.setCiudad(ciudad.trim());
            dto.setDireccion(direccion.trim());
            dto.setTelefono(telefono.trim());
            try {
                apiClient.crearSucursal(dto, user);
            } catch (Exception ignored) {}
        }
        return "redirect:/gestion?seccion=sucursales";
    }

    @PostMapping("/gestion/sucursales/{id}/eliminar")
    public String eliminarSucursal(@PathVariable("id") Long id, Authentication auth) {
        AuthUserSession user = SecurityUtils.getSessionUser(auth);
        if (user != null) {
            try {
                apiClient.eliminarSucursal(id, user);
            } catch (Exception ignored) {}
        }
        return "redirect:/gestion?seccion=sucursales";
    }
}

