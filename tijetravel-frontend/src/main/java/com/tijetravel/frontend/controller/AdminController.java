package com.tijetravel.frontend.controller;

import com.tijetravel.frontend.dto.AuthUserSession;
import com.tijetravel.frontend.dto.BalanceDTO;
import com.tijetravel.frontend.service.BackendApiClient;
import com.tijetravel.frontend.util.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final BackendApiClient apiClient;

    public AdminController(BackendApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @GetMapping("/balance")
    public String verBalance(Authentication auth, Model model) {
        AuthUserSession user = SecurityUtils.getSessionUser(auth);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            BalanceDTO balance = apiClient.obtenerBalance(user);
            model.addAttribute("balance", balance);
        } catch (Exception e) {
            model.addAttribute("errorServicio", "No se pudo obtener el balance financiero: " + e.getMessage());
        }

        return "admin_balance";
    }

    @GetMapping("/usuarios")
    public String listarUsuarios(
            @RequestParam(name = "codigo", required = false) String codigo,
            @RequestParam(name = "dni", required = false) String dni,
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "rol", required = false) String rol,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Authentication auth,
            Model model) {

        AuthUserSession user = SecurityUtils.getSessionUser(auth);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            Map<String, Object> pageData = apiClient.listarUsuarios(codigo, dni, username, rol, page, 15, user);
            model.addAttribute("paginaUsuarios", pageData);
            model.addAttribute("codigo", codigo);
            model.addAttribute("dni", dni);
            model.addAttribute("username", username);
            model.addAttribute("rol", rol);
        } catch (Exception e) {
            model.addAttribute("errorServicio", "Error al cargar usuarios: " + e.getMessage());
        }

        return "admin_usuarios";
    }

    @PostMapping("/usuarios/crear")
    public String crearUsuario(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("rol") String rol,
            @RequestParam("dni") String dni,
            Authentication auth,
            Model model) {

        AuthUserSession user = SecurityUtils.getSessionUser(auth);
        if (user != null) {
            Map<String, Object> req = new HashMap<>();
            req.put("username", username.trim());
            req.put("password", password);
            req.put("rol", rol);
            req.put("dni", dni.trim());

            try {
                apiClient.crearUsuarioAdmin(req, user);
            } catch (Exception ignored) {}
        }

        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/{id}/rol")
    public String cambiarRol(@PathVariable("id") Long id,
                             @RequestParam("rol") String rol,
                             Authentication auth) {
        AuthUserSession user = SecurityUtils.getSessionUser(auth);
        if (user != null) {
            try {
                apiClient.cambiarRolUsuario(id, rol, user);
            } catch (Exception ignored) {}
        }
        return "redirect:/admin/usuarios";
    }
}

