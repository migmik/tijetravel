package com.tijetravel.backend.controller;

import com.tijetravel.backend.dto.LoginRequestDTO;
import com.tijetravel.backend.dto.LoginResponseDTO;
import com.tijetravel.backend.dto.RegistroClienteDTO;
import com.tijetravel.backend.dto.UsuarioDTO;
import com.tijetravel.backend.model.Turista;
import com.tijetravel.backend.model.Usuario;
import com.tijetravel.backend.repository.TuristaRepository;
import com.tijetravel.backend.repository.UsuarioRepository;
import com.tijetravel.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final TuristaRepository turistaRepository;
    private final UsuarioService usuarioService;

    public AuthController(AuthenticationManager authenticationManager,
                          UsuarioRepository usuarioRepository,
                          TuristaRepository turistaRepository,
                          UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.turistaRepository = turistaRepository;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername().trim(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        Usuario usuario = usuarioRepository.findByUsername(request.getUsername().trim()).orElseThrow();
        String nombreCompleto = usuario.getUsername();

        Turista turista = turistaRepository.findByDni(usuario.getDni()).orElse(null);
        if (turista != null) {
            nombreCompleto = turista.getNombre() + " " + turista.getApellido();
        }

        LoginResponseDTO response = new LoginResponseDTO(
                usuario.getCodigo(),
                usuario.getUsername(),
                usuario.getRol().name(),
                usuario.getDni(),
                nombreCompleto
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> registrar(@Valid @RequestBody RegistroClienteDTO request) {
        UsuarioDTO nuevoUsuario = usuarioService.registrarCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponseDTO> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario usuario = usuarioRepository.findByUsername(authentication.getName()).orElseThrow();
        String nombreCompleto = usuario.getUsername();

        Turista turista = turistaRepository.findByDni(usuario.getDni()).orElse(null);
        if (turista != null) {
            nombreCompleto = turista.getNombre() + " " + turista.getApellido();
        }

        LoginResponseDTO response = new LoginResponseDTO(
                usuario.getCodigo(),
                usuario.getUsername(),
                usuario.getRol().name(),
                usuario.getDni(),
                nombreCompleto
        );

        return ResponseEntity.ok(response);
    }
}

