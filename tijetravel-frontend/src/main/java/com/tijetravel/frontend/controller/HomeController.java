package com.tijetravel.frontend.controller;

import com.tijetravel.frontend.dto.BusquedaForm;
import com.tijetravel.frontend.dto.BusquedaResponseDTO;
import com.tijetravel.frontend.service.BackendApiClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;
import java.util.List;

@Controller
public class HomeController {

    private final BackendApiClient apiClient;

    public HomeController(BackendApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @GetMapping("/")
    public String index(Model model) {
        try {
            List<String> origenes = apiClient.obtenerOrigenes();
            List<String> destinos = apiClient.obtenerDestinos();
            model.addAttribute("ciudadesOrigen", origenes);
            model.addAttribute("ciudadesDestino", destinos);
        } catch (Exception e) {
            model.addAttribute("errorServicio", "No se pudo conectar con el catálogo de viajes. Verifique que el backend esté activo.");
        }

        BusquedaForm form = new BusquedaForm();
        form.setFechaLlegada(LocalDate.now().plusDays(1));
        form.setFechaPartida(LocalDate.now().plusDays(4));
        form.setPersonas(1);
        form.setOrdenPrecio("ASC");
        model.addAttribute("busquedaForm", form);

        return "home";
    }

    @GetMapping("/buscar")
    public String buscar(@ModelAttribute("busquedaForm") BusquedaForm form, Model model) {
        try {
            model.addAttribute("ciudadesOrigen", apiClient.obtenerOrigenes());
            model.addAttribute("ciudadesDestino", apiClient.obtenerDestinos());
        } catch (Exception e) {
            // Ignorar si falla la recarga de desplegables
        }

        // Validaciones en servidor
        if (form.getOrigen() == null || form.getOrigen().trim().isEmpty() ||
            form.getDestino() == null || form.getDestino().trim().isEmpty()) {
            model.addAttribute("errorValidacion", "Debe seleccionar ciudad de origen y ciudad de destino.");
            return "home";
        }

        if (form.getOrigen().trim().equalsIgnoreCase(form.getDestino().trim())) {
            model.addAttribute("errorValidacion", "El origen y el destino deben ser ciudades diferentes.");
            return "home";
        }

        if (form.getFechaLlegada() == null || form.getFechaPartida() == null) {
            model.addAttribute("errorValidacion", "Debe ingresar fecha de llegada y fecha de salida.");
            return "home";
        }

        if (!form.getFechaPartida().isAfter(form.getFechaLlegada())) {
            model.addAttribute("errorValidacion", "La fecha de salida del hotel debe ser posterior al día de llegada.");
            return "home";
        }

        if (form.getPersonas() == null || form.getPersonas() < 1 || form.getPersonas() > 20) {
            model.addAttribute("errorValidacion", "La cantidad de personas debe ser entre 1 y 20.");
            return "home";
        }

        try {
            BusquedaResponseDTO resultado = apiClient.buscarViajes(form);
            model.addAttribute("resultado", resultado);
            model.addAttribute("busquedaForm", form);
            return "resultados";
        } catch (Exception e) {
            model.addAttribute("errorServicio", "Ocurrió un error al consultar la disponibilidad: " + e.getMessage());
            return "home";
        }
    }
}

