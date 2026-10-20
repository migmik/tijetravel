package com.tijetravel.frontend.controller;

import com.tijetravel.frontend.dto.AuthUserSession;
import com.tijetravel.frontend.dto.TuristaDTO;
import com.tijetravel.frontend.service.BackendApiClient;
import com.tijetravel.frontend.util.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/familia")
public class FamiliaController {

    private final BackendApiClient apiClient;

    public FamiliaController(BackendApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @PostMapping("/agregar")
    public String agregarFamiliar(
            @RequestParam("dni") String dni,
            @RequestParam("nombre") String nombre,
            @RequestParam("apellido") String apellido,
            @RequestParam("direccion") String direccion,
            @RequestParam("email") String email,
            @RequestParam(name = "telefonoFijo", required = false) String telefonoFijo,
            @RequestParam("telefonoCelular") String telefonoCelular,
            Authentication auth) {

        AuthUserSession user = SecurityUtils.getSessionUser(auth);
        if (user == null) {
            return "redirect:/login";
        }

        TuristaDTO nuevoFamiliar = new TuristaDTO();
        nuevoFamiliar.setDni(dni.trim());
        nuevoFamiliar.setNombre(nombre.trim());
        nuevoFamiliar.setApellido(apellido.trim());
        nuevoFamiliar.setDireccion(direccion.trim());
        nuevoFamiliar.setEmail(email.trim());
        nuevoFamiliar.setTelefonoFijo(telefonoFijo != null ? telefonoFijo.trim() : null);
        nuevoFamiliar.setTelefonoCelular(telefonoCelular.trim());

        try {
            apiClient.crearTurista(nuevoFamiliar, user);
        } catch (Exception ignored) {
        }

        return "redirect:/mi-cuenta";
    }
}

