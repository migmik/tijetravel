package com.tijetravel.frontend.controller;

import com.tijetravel.frontend.dto.AuthUserSession;
import com.tijetravel.frontend.dto.ReservaDTO;
import com.tijetravel.frontend.dto.TuristaDTO;
import com.tijetravel.frontend.service.BackendApiClient;
import com.tijetravel.frontend.util.SecurityUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final BackendApiClient apiClient;

    public ReservaController(BackendApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @GetMapping("/nueva")
    public String formularioReserva(
            @RequestParam("vueloNumero") String vueloNumero,
            @RequestParam("hotelCodigo") String hotelCodigo,
            @RequestParam("fechaLlegada") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaLlegada,
            @RequestParam("fechaPartida") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaPartida,
            @RequestParam(name = "personas", defaultValue = "1") int personas,
            Authentication auth,
            Model model) {

        AuthUserSession user = SecurityUtils.getSessionUser(auth);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("vueloNumero", vueloNumero);
        model.addAttribute("hotelCodigo", hotelCodigo);
        model.addAttribute("fechaLlegada", fechaLlegada);
        model.addAttribute("fechaPartida", fechaPartida);
        model.addAttribute("personas", personas);

        // Si es cliente, cargar los turistas de su grupo familiar para que elija a quién reservar
        if ("CLIENTE".equals(user.getRol())) {
            try {
                List<TuristaDTO> familia = apiClient.obtenerGrupoFamiliar(user.getDni(), user);
                model.addAttribute("miembrosFamilia", familia);
            } catch (Exception e) {
                model.addAttribute("errorServicio", "No se pudo cargar el grupo familiar.");
            }
        }

        return "reserva_formulario";
    }

    @PostMapping("/nueva")
    public String crearReserva(
            @RequestParam("turistaDni") String turistaDni,
            @RequestParam("vueloNumero") String vueloNumero,
            @RequestParam("hotelCodigo") String hotelCodigo,
            @RequestParam("claseVuelo") String claseVuelo,
            @RequestParam("tipoHospedaje") String tipoHospedaje,
            @RequestParam("fechaLlegada") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaLlegada,
            @RequestParam("fechaPartida") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaPartida,
            Authentication auth,
            Model model) {

        AuthUserSession user = SecurityUtils.getSessionUser(auth);
        if (user == null) {
            return "redirect:/login";
        }

        Map<String, Object> req = new HashMap<>();
        req.put("turistaDni", turistaDni.trim());
        req.put("vueloNumero", vueloNumero.trim());
        req.put("hotelCodigo", hotelCodigo.trim());
        req.put("claseVuelo", claseVuelo);
        req.put("tipoHospedaje", tipoHospedaje);
        req.put("fechaLlegada", fechaLlegada.toString());
        req.put("fechaPartida", fechaPartida.toString());

        try {
            ReservaDTO reservaCreada = apiClient.crearReserva(req, user);
            // Redirigir a la pantalla de "Procesando pago" de 4 segundos
            return "redirect:/reservas/" + reservaCreada.getId() + "/procesando-pago";
        } catch (Exception e) {
            model.addAttribute("errorValidacion", "No se pudo crear la reserva: " + e.getMessage());
            model.addAttribute("vueloNumero", vueloNumero);
            model.addAttribute("hotelCodigo", hotelCodigo);
            model.addAttribute("fechaLlegada", fechaLlegada);
            model.addAttribute("fechaPartida", fechaPartida);

            if ("CLIENTE".equals(user.getRol())) {
                try {
                    model.addAttribute("miembrosFamilia", apiClient.obtenerGrupoFamiliar(user.getDni(), user));
                } catch (Exception ignored) {}
            }
            return "reserva_formulario";
        }
    }

    @GetMapping("/{id}/procesando-pago")
    public String procesandoPago(@PathVariable("id") Long id, Authentication auth, Model model) {
        AuthUserSession user = SecurityUtils.getSessionUser(auth);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            ReservaDTO reserva = apiClient.obtenerReservaPorId(id, user);
            model.addAttribute("reserva", reserva);
            return "pago_procesando";
        } catch (Exception e) {
            model.addAttribute("errorServicio", "Error al consultar la reserva: " + e.getMessage());
            return "redirect:/reservas/mis-reservas";
        }
    }

    @GetMapping("/{id}/confirmar-pago")
    public String confirmarPago(@PathVariable("id") Long id, Authentication auth, Model model) {
        AuthUserSession user = SecurityUtils.getSessionUser(auth);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            // Confirmación de pago idempotente en backend
            apiClient.procesarPagoReserva(id, user);
            return "redirect:/reservas/" + id + "/detalle?pagoExitoso=true";
        } catch (Exception e) {
            model.addAttribute("errorServicio", "Error al procesar el pago: " + e.getMessage());
            return "redirect:/reservas/" + id + "/detalle";
        }
    }

    @GetMapping("/{id}/detalle")
    public String verDetalle(@PathVariable("id") Long id,
                            @RequestParam(name = "pagoExitoso", required = false) String pagoExitoso,
                            Authentication auth,
                            Model model) {
        AuthUserSession user = SecurityUtils.getSessionUser(auth);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            ReservaDTO reserva = apiClient.obtenerReservaPorId(id, user);
            model.addAttribute("reserva", reserva);
            if (pagoExitoso != null) {
                model.addAttribute("exitoMensaje", "¡Pago procesado con éxito! Su comprobante de viaje ha sido emitido.");
            }
            return "reserva_detalle";
        } catch (Exception e) {
            model.addAttribute("errorServicio", "No se encontró la reserva solicitada o no tiene permiso para verla.");
            return "redirect:/reservas/mis-reservas";
        }
    }

    @GetMapping("/mis-reservas")
    public String misReservas(Authentication auth, Model model) {
        AuthUserSession user = SecurityUtils.getSessionUser(auth);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            Map<String, Object> pageData = apiClient.listarReservas(null, null, null, null, null, null, null, 0, 50, user);
            model.addAttribute("paginaReservas", pageData);
        } catch (Exception e) {
            model.addAttribute("errorServicio", "Error al cargar las reservas: " + e.getMessage());
        }

        return "mis_reservas";
    }
}

