package com.tijetravel.backend.controller;

import com.tijetravel.backend.dto.CrearUsuarioAdminDTO;
import com.tijetravel.backend.dto.UsuarioDTO;
import com.tijetravel.backend.model.Rol;
import com.tijetravel.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioDTO>> buscar(
            @RequestParam(name = "codigo", required = false) String codigo,
            @RequestParam(name = "dni", required = false) String dni,
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "rol", required = false) Rol rol,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        Page<UsuarioDTO> resultado = usuarioService.buscarUsuarios(codigo, dni, username, rol, page, size);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UsuarioDTO> obtenerPorUsername(@PathVariable("username") String username) {
        UsuarioDTO usuario = usuarioService.obtenerPorUsername(username);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> crear(@Valid @RequestBody CrearUsuarioAdminDTO dto) {
        UsuarioDTO creado = usuarioService.crearUsuarioPorAdmin(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}/rol")
    public ResponseEntity<UsuarioDTO> cambiarRol(@PathVariable("id") Long id, @RequestParam("rol") Rol rol) {
        UsuarioDTO actualizado = usuarioService.cambiarRol(id, rol);
        return ResponseEntity.ok(actualizado);
    }
}

