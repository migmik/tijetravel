package com.tijetravel.frontend.controller;

import com.tijetravel.frontend.dto.AuthUserSession;
import com.tijetravel.frontend.dto.SucursalDTO;
import com.tijetravel.frontend.dto.TuristaDTO;
import com.tijetravel.frontend.service.BackendApiClient;
import com.tijetravel.frontend.util.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AuthController {

    private final BackendApiClient apiClient;

    public AuthController(BackendApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @GetMapping("/login")
    public String loginPage(Authentication auth,
                            @RequestParam(name = "error", required = false) String error,
                            @RequestParam(name = "logout", required = false) String logout,
                            @RequestParam(name = "registered", required = false) String registered,
                            Model model) {
        // Si ya está autenticado, no mostrar el formulario de login y redirigir a Mi Cuenta
        if (SecurityUtils.isAuthenticated(auth)) {
            return "redirect:/mi-cuenta";
        }

        if (error != null) {
            model.addAttribute("errorMensaje", "Usuario o contraseña incorrectos. Verifique sus credenciales.");
        }
        if (logout != null) {
            model.addAttribute("infoMensaje", "Sesión cerrada correctamente.");
        }
        if (registered != null) {
            model.addAttribute("exitoMensaje", "Registro completado con éxito. Ingrese con sus nuevas credenciales.");
        }

        return "login";
    }

    @GetMapping("/registro")
    public String registroPage(Authentication auth, Model model) {
        if (SecurityUtils.isAuthenticated(auth)) {
            return "redirect:/mi-cuenta";
        }

        try {
            List<SucursalDTO> sucursales = apiClient.obtenerSucursales();
            model.addAttribute("sucursales", sucursales);
        } catch (Exception e) {
            model.addAttribute("errorServicio", "No se pudieron cargar las sucursales disponibles.");
        }

        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(
            @RequestParam("dni") String dni,
            @RequestParam("nombre") String nombre,
            @RequestParam("apellido") String apellido,
            @RequestParam("direccion") String direccion,
            @RequestParam("email") String email,
            @RequestParam(name = "telefonoFijo", required = false) String telefonoFijo,
            @RequestParam("telefonoCelular") String telefonoCelular,
            @RequestParam("sucursalCodigo") String sucursalCodigo,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            Model model) {

        // Validar DNI
        if (dni == null || !dni.trim().matches("^[0-9]{7,8}$")) {
            model.addAttribute("errorValidacion", "El DNI debe contener 7 u 8 dígitos numéricos.");
            try {
                model.addAttribute("sucursales", apiClient.obtenerSucursales());
            } catch (Exception ignored) {}
            return "registro";
        }

        Map<String, Object> req = new HashMap<>();
        req.put("dni", dni.trim());
        req.put("nombre", nombre.trim());
        req.put("apellido", apellido.trim());
        req.put("direccion", direccion.trim());
        req.put("email", email.trim());
        req.put("telefonoFijo", telefonoFijo != null ? telefonoFijo.trim() : null);
        req.put("telefonoCelular", telefonoCelular.trim());
        req.put("sucursalCodigo", sucursalCodigo.trim());
        req.put("username", username.trim());
        req.put("password", password);

        try {
            apiClient.registrarCliente(req);
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("errorValidacion", "No se pudo completar el registro: " + e.getMessage());
            try {
                model.addAttribute("sucursales", apiClient.obtenerSucursales());
            } catch (Exception ignored) {}
            return "registro";
        }
    }

    @GetMapping("/mi-cuenta")
    public String miCuenta(Authentication auth, Model model) {
        AuthUserSession user = SecurityUtils.getSessionUser(auth);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", user);

        // Si es CLIENTE, cargar su información de turista y su grupo familiar
        if ("CLIENTE".equals(user.getRol())) {
            try {
                TuristaDTO turista = apiClient.obtenerTuristaPorDni(user.getDni(), user);
                model.addAttribute("turista", turista);

                List<TuristaDTO> grupoFamiliar = apiClient.obtenerGrupoFamiliar(user.getDni(), user);
                model.addAttribute("grupoFamiliar", grupoFamiliar);
            } catch (Exception e) {
                model.addAttribute("errorCarga", "No se pudo cargar la información del perfil.");
            }
        }

        return "mi_cuenta";
    }
}

